package com.example.android.rxproject;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class earthQuakeAdapter extends ArrayAdapter<earthQuake> {

    public earthQuakeAdapter(Context context, ArrayList<earthQuake> earthQuakes) {
        super(context,0, earthQuakes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        earthQuake currentRecord = getItem(position);

        double magnitude = currentRecord.getMagnitude();
        DecimalFormat magnitudeFormat = new DecimalFormat("0.0");
        String formattedMagnitude = magnitudeFormat.format(magnitude);

        TextView magTextView = (TextView) listItemView.findViewById(R.id.magnitude);
        magTextView.setText(formattedMagnitude);

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magTextView.getBackground();

        // Get the appropriate background color based on the current earthquake magnitude
        int magnitudeColor = getMagnitudeColor(currentRecord.getMagnitude());

        // Set the color on the magnitude circle
        magnitudeCircle.setColor(magnitudeColor);

        String fullLocation = currentRecord.getLocation();
        String locationOffset = "";
        String primaryLocation;
        int seperationIndex;
        if(fullLocation.contains("km"))
        {
            /**
             Splitting full location using String.split function
             */
//          String[] parts = fullLocation.split("of ");
//          primaryLocation = parts[1];
//          locationOffset = parts[0] + "of ";

            /**
             Splitting full location using String.substring function
             */
            seperationIndex = fullLocation.indexOf("of ");
            locationOffset = fullLocation.substring(0,seperationIndex+3);
            primaryLocation = fullLocation.substring(seperationIndex+3, fullLocation.length());

        }
        else {
            locationOffset = "Near The ";
            primaryLocation = fullLocation;
        }
        TextView offsetTextView = (TextView) listItemView.findViewById(R.id.offset);
        offsetTextView.setText(locationOffset);


        TextView locTextView = (TextView) listItemView.findViewById(R.id.location);
        locTextView.setText(primaryLocation);

        // getting timeInMilliseconds of current earthquake
        Date dateObject = new Date(currentRecord.getTimeInMilliseconds());

        // converting timeInMilliseconds of current earthquake to readable date
        SimpleDateFormat dateFormatter = new SimpleDateFormat("DD MM, yyyy");
        String dateToDisplay = dateFormatter.format(dateObject);

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        dateTextView.setText(dateToDisplay);

        // getting the time at which earthquake happened
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String timeToDisplay = timeFormatter.format(dateObject);

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        timeTextView.setText(timeToDisplay);


        return listItemView;
    }

    int getMagnitudeColor(double mag)
    {
        int magnitudeColor;
        int magnitudeFloor = (int) Math.floor(mag);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColor = R.color.magnitude1;
                break;
            case 2:
                magnitudeColor = R.color.magnitude2;
                break;
            case 3:
                magnitudeColor = R.color.magnitude3;
                break;
            case 4:
                magnitudeColor = R.color.magnitude4;
                break;
            case 5:
                magnitudeColor = R.color.magnitude5;
                break;
            case 6:
                magnitudeColor = R.color.magnitude8;
                break;
            case 7:
                magnitudeColor = R.color.magnitude9;
                break;
            default:
                magnitudeColor = R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(), magnitudeColor);
    }
}