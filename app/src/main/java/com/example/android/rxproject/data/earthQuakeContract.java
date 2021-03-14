package com.example.android.rxproject.data;

public final class earthQuakeContract {
    private earthQuakeContract() {}

    public static abstract class earthQuakeEntry  {

        public static final String TABLE_NAME = "earthQuakes";
        public static final String _ID = "id";
        public static final String COLUMN_MAGNITUDE = "magnitude";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_URL = "link";
    }
}