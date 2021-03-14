package com.example.android.rxproject.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.rxproject.data.earthQuakeContract.earthQuakeEntry;

public class earthQuakeDbHelper extends SQLiteOpenHelper {

    /** Name of the database file */
    private static final String DATABASE_NAME = "quakes.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    public earthQuakeDbHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the quakes table
        String SQL_CREATE_QUAKES_TABLE = "CREATE TABLE "+ earthQuakeEntry.TABLE_NAME + " ("
                + earthQuakeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + earthQuakeEntry.COLUMN_MAGNITUDE + " REAL,"
                + earthQuakeEntry.COLUMN_DATE + " INTEGER,"
                + earthQuakeEntry.COLUMN_LOCATION + " TEXT,"
                + earthQuakeEntry.COLUMN_URL + " TEXT);";
        sqLiteDatabase.execSQL(SQL_CREATE_QUAKES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
