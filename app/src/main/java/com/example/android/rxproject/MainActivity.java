package com.example.android.rxproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.rxproject.data.earthQuakeDbHelper;
import com.example.android.rxproject.data.earthQuakeContract.earthQuakeEntry;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    /** URL for earthquake data from the USGS dataset from 1-1-2020 to 1-10-2020*/
    private static final String[] USGS_REQUEST_URLS =
            {"https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2020-01-01&endtime=2020-10-1&minmagnitude=1&maxmagnitude=3&limit=10",
                    "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2020-01-01&endtime=2020-10-1&minmagnitude=4.7&maxmagnitude=6&limit=10",
                    "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2020-01-01&endtime=2020-10-1&minmagnitude=6.9"};


    earthQuakeDbHelper mDbHelper = new earthQuakeDbHelper(this);
    SQLiteDatabase mDb;

    // Create a fake list of earthquakes.
    ArrayList<earthQuake> earthquakes = new ArrayList<>();

    // Find a reference to the ListView in the layout
    ListView earthquakeListView;

    // Find a reference to the buttons in the layout
    Button requestButton;
    Button saveButton;


    /** Projection for database queries */
    String[] projection = new String[] {
            earthQuakeEntry.COLUMN_MAGNITUDE,
            earthQuakeEntry.COLUMN_DATE,
            earthQuakeEntry.COLUMN_LOCATION,
            earthQuakeEntry.COLUMN_URL
    };
    /** Object to store data from database.query() method */
    Cursor cursor;

    /** Object for the progress dialog to show when getting data from api and storing in the database*/
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestButton = findViewById(R.id.request_button);
        saveButton = findViewById(R.id.save_button);
        saveButton.setEnabled(false);
        Button displayButton = findViewById(R.id.display_button);
        earthquakeListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(emptyView);

        if(getCount() == 0)
            displayButton.setEnabled(false);
        else
            requestButton.setEnabled(false);


        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                earthQuake quake =earthquakes.get(position);

                Uri webpage = Uri.parse(quake.getURL());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(MainActivity.this, "Getting data", "Please wait...", true);
                requestButton.setEnabled(false);
                sendRequest();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertData();
                saveButton.setEnabled(false);
                displayButton.setEnabled(true);
            }
        });

        displayButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                updateUi();
            }
        });

    }

    public int getCount()
    {
        mDb = mDbHelper.getReadableDatabase();

        cursor = mDb.query(earthQuakeEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        return cursor.getCount();
    }

    /**
     * performing request for api with different urls by calling execute function with the url
     */
    public void sendRequest()
    {
        Observable observable = Observable.fromArray(USGS_REQUEST_URLS)
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String s) throws Throwable {
                        return Utilities.fetchEarthquakeData(s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Toast.makeText(MainActivity.this, "Requests have been sent", Toast.LENGTH_SHORT).show();

        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Object o) {
                earthquakes.addAll(Utilities.extractEarthquakes((String)o));
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Toast.makeText(MainActivity.this, "An error occurred please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {
                saveButton.setEnabled(true);
                dialog.dismiss();
            }
        };

        observable.subscribe(observer);

    }

    /**
     * Insert the earthquakes data into the database
     */
    public void insertData()
    {
        mDb = mDbHelper.getWritableDatabase();
        int i;
        ContentValues values = new ContentValues();
        for(i = 0; i < earthquakes.size(); i++)
        {
            values.put(earthQuakeEntry.COLUMN_MAGNITUDE,earthquakes.get(i).getMagnitude());
            values.put(earthQuakeEntry.COLUMN_LOCATION,earthquakes.get(i).getLocation());
            values.put(earthQuakeEntry.COLUMN_DATE,earthquakes.get(i).getTimeInMilliseconds());
            values.put(earthQuakeEntry.COLUMN_URL,earthquakes.get(i).getURL());
            mDb.insert(earthQuakeEntry.TABLE_NAME,null,values);
        }
        Toast.makeText(this, R.string.data_saved, Toast.LENGTH_SHORT).show();
        earthquakes = new ArrayList<>();
    }


    /**
     * Update the UI with the earthquakes Arraylist.
     */
    public void updateUi()
    {
        // load earthquakes from database to earthquakes arraylist
        loadDataFromDb();

        // attach the earthquakes arraylist to the adapter
        earthQuakeAdapter adapter = new earthQuakeAdapter(this,earthquakes);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(adapter);
    }

    /**
     * Load earthquakes data from the database.
     */

    public void loadDataFromDb()
    {
        earthquakes = new ArrayList<>();
        mDb = mDbHelper.getReadableDatabase();

        cursor = mDb.query(earthQuakeEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        // Toast to inform the user that the data being loaded from the database
        Toast.makeText(this, R.string.loading_data, Toast.LENGTH_SHORT).show();

        int magnitudeColumnindex = cursor.getColumnIndex(earthQuakeEntry.COLUMN_MAGNITUDE);
        int locationColumnindex = cursor.getColumnIndex(earthQuakeEntry.COLUMN_LOCATION);
        int dateColumnindex = cursor.getColumnIndex(earthQuakeEntry.COLUMN_DATE);
        int urlColumnindex = cursor.getColumnIndex(earthQuakeEntry.COLUMN_URL);

        while(cursor.moveToNext())
        {
            earthquakes.add(new earthQuake(cursor.getFloat(magnitudeColumnindex),
                    cursor.getString(locationColumnindex),
                    cursor.getLong(dateColumnindex),
                    cursor.getString(urlColumnindex)));
        }

        // Always close the cursor when you're done reading from it. This releases all its
        // resources and makes it invalid.
        cursor.close();
    }
}