package com.example.mapdemo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;



public final class LocalDatabase {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry.COLUMN_NAME_PID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_DEVICEUID + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_ALTITUDE + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_HEADING + TEXT_TYPE + COMMA_SEP +
                    FeedEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    public SQLiteDatabase currDB;



    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LocalDatabase() {
    }

    public void reset() {

    }

    public boolean isTableExists(SQLiteDatabase db, String tableName, boolean openDb, Context curContext) {
        if (openDb) {
            String DATABASE_NAME = "FeedReader_grid.db";

            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(curContext, DATABASE_NAME);
            if (db == null || !db.isOpen()) {
                db = mDbHelper.getReadableDatabase();
            }

            if (!db.isReadOnly()) {
                db.close();
                db = mDbHelper.getReadableDatabase();
            }
        }

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void insertDataDevice(String device_id, String latitude, String longitude, String altitude, String heading, String timestamp, Context curContext, boolean doReset) {
        String DATABASE_NAME = "FeedReader_grid.db";

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(curContext, DATABASE_NAME);
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        if (doReset) {
            if (isTableExists(db, FeedEntry.TABLE_NAME, false, curContext))
                db.delete(FeedEntry.TABLE_NAME, null, null);
            else
                db.execSQL(SQL_CREATE_ENTRIES);
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_DEVICEUID, device_id);
        values.put(FeedEntry.COLUMN_NAME_LATITUDE, latitude);
        values.put(FeedEntry.COLUMN_NAME_LONGITUDE, longitude);
        values.put(FeedEntry.COLUMN_NAME_ALTITUDE, altitude);
        values.put(FeedEntry.COLUMN_NAME_HEADING, heading);
        values.put(FeedEntry.COLUMN_NAME_TIMESTAMP, timestamp);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                FeedEntry.TABLE_NAME,
                FeedEntry.COLUMN_NAME_NULLABLE,
                values);
        db.close();
    }

    public void close() {
        currDB.close();
    }

    public Cursor retrieveData(Context curContext, String selection, String[] selectionArgs) {
        String DATABASE_NAME = "FeedReader_grid.db";

        //Log.d("DATABASE DEBUG-READ", "NAME: " + DATABASE_NAME);
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(curContext, DATABASE_NAME);
        currDB = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.

        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_DEVICEUID,
                FeedEntry.COLUMN_NAME_LATITUDE,
                FeedEntry.COLUMN_NAME_LONGITUDE,
                FeedEntry.COLUMN_NAME_ALTITUDE,
                FeedEntry.COLUMN_NAME_HEADING,
                FeedEntry.COLUMN_NAME_TIMESTAMP };

        // How you want the results sorted in the resulting Cursor
        String sortOrder = FeedEntry.COLUMN_NAME_PID + " ASC";

        Cursor c = currDB.query(
                FeedEntry.TABLE_NAME,                   // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        return c;
    }

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "devices";
        public static final String COLUMN_NAME_NULLABLE = "nullable";
        public static final String COLUMN_NAME_PID = "pid";
        public static final String COLUMN_NAME_DEVICEUID = "device_uid";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ALTITUDE = "altitude";
        public static final String COLUMN_NAME_HEADING = "heading";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    public static class FeedReaderDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        //public static final String DATABASE_NAME = "FeedReader_grid_" + MuseumSelector.museumID + ".db";

        public FeedReaderDbHelper(Context context, String dbName) {
            super(context, dbName, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
}	
