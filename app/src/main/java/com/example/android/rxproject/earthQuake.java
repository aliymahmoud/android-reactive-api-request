package com.example.android.rxproject;

public class earthQuake {
    private double magnitude;
    private String location;
    private Long timeInMilliseconds;
    private String URL;

    public earthQuake(float mag, String loc, Long date, String link)
    {
        this.magnitude = mag;
        this.location = loc;
        this.timeInMilliseconds = date;
        this.URL = link;
    }

    public String getURL() {
        return URL;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public Long getTimeInMilliseconds() {
        return timeInMilliseconds;
    }
}