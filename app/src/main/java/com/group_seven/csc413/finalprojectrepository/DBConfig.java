package com.group_seven.csc413.finalprojectrepository;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import java.util.*;
/**
 * Created by Jose Ortiz Costa on 4/18/15.
 * Just a base class to start working on the database
 *
 */
public class DBConfig {

    private Context context;
    private SQLiteDatabase db;
    private String userContext;
    private static final String DATABASE_NAME = "appDatabase.db";
    private static final String APP_INFO_TABLE = "appInfo";
    static final String CAR_LOC_TABLE = "car";
    static final String HISTORY_TABLE = "history";
    private static final String APP_NAME_COLUMN = "appName";
    private static final String APP_VERSION_COLUMN = "appVersion";
    private static final String APP_STATUS = "appStatus";
    private static final String CONTEXT_COLUMN = "context";
    private static final String LATITUDE_COLUMN = "latitude";
    private static final String LONGITUDE_COLUMN = "longitude";
    private static final String APP_TIMES_LAUNCHED = "appTimesLaunched";
    private static final String TIME_COLUMN = "time";
    private static final String IS_IN_FAVORITES = "isInFavorites";
    private static final String IS_IN_HISTORY = "isInHistory";

    private static final String FAVORITES_COLUMN = "favorites";

    /**
     * Author: Jose Ortiz
     * Description: Private constructor only can be invoked
     * by a instance method
     *
     * @param c Context of the application
     */
    private DBConfig(Context c) {
        this.context = c.getApplicationContext();
        loadDatabaseConfig();
    }

    /**
     * Description: Instance method that creates an object of this class
     * by calling the private constructor
     *
     * @param c Context of the application
     * @return a instance object of this class
     */
    public static DBConfig loadDbConfiguration(Context c) {
        return new DBConfig(c);
    }

    /**
     * Description: Opens or creates a database and its tables
     */
    private void loadDatabaseConfig() {

        try {

            // Open the database if exists. otherwise, creates a new one
            db = this.context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
            // Creates tables if not exist
            // App Info table
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    APP_INFO_TABLE +
                    " ( id integer primary key autoincrement, " +
                    APP_NAME_COLUMN + " Text," +
                    APP_VERSION_COLUMN + " Text, " +
                    APP_STATUS + " INTEGER, " +
                    APP_TIMES_LAUNCHED + " INTEGER DEFAULT 0); ");

            // Car Place
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    CAR_LOC_TABLE +
                    " ( id integer primary key autoincrement, " +
                    CONTEXT_COLUMN + " Text, " +
                    LATITUDE_COLUMN + " Real, " +
                    LONGITUDE_COLUMN + " Real, " +
                    TIME_COLUMN + " Text, " +
                    IS_IN_HISTORY + " Text,  " +
                    IS_IN_FAVORITES + " Text );");
            // History
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    HISTORY_TABLE +
                    " ( id integer primary key autoincrement, " +
                    CONTEXT_COLUMN + " Text, " +
                    TIME_COLUMN + " Text, " +
                    LATITUDE_COLUMN + " Real, " +
                    LONGITUDE_COLUMN + " Real, " +
                    IS_IN_FAVORITES + " Text );");


        } catch (SQLException e) {
            // Error database couldn't be created or loaded
            Log.d("DbConfiguration: ", "Error while creating " + DATABASE_NAME +
                    " Detailed error: " + e.getMessage());
        }
    }



    private void closeDatabase ()
    {
        db.close();
    }

    public void setUserContext(String context) {
        this.userContext = context;
    }

    public String getUserContext() {
        return userContext;
    }

    public int getProfilesCount()
    {

        String countQuery = "SELECT  * FROM " + CAR_LOC_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }




    public int getProfilesCount(String table)
    {

        String countQuery = "SELECT  * FROM " + table;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    /**
     * Description: Delete a database
     *
     * @param databaseName name of the database
     */
    public void deleteDatabase(String databaseName)
    {
        try {

            if (this.context.deleteDatabase(databaseName))
                Log.d("DbConfiguration: ", "database deleted");

        } catch (SQLException e) {
            Log.d("DbConfiguration: ", "Error deleting " +
                    databaseName + ". Detailed Error: " + e.getMessage());
        }
    }


    /**
     * Description: Checks database integrity
     *
     * @return true if the database integrity is ok.
     * Otherwise, returns false.
     */
    public boolean isDatabaseOk()
    {

        boolean isDatabaseOk = false;
        if (db.isDatabaseIntegrityOk())
        {
            closeDatabase();
            isDatabaseOk = true;
        }
        return isDatabaseOk;

    }

    /**
     * Description: Rebuild the whole database
     *
     * @param context  Activity context: normally this.getBaseContext()
     * @param database database name
     *                 Note: All the data stored in the database will be deleted
     */
    public void reBuildDatabase(Context context, String database) {
        try {

            deleteDatabase(database);
            loadDbConfiguration(context);
        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }

    }

    public void saveLocation(Locations loc, String table)
    {

        String isInHistory = "0";
        String isInFavorites = "0";
        if (loc.isInHistory() == true)
            isInHistory = "1";
        if (loc.isInFavorites() == true)
            isInFavorites = "1";
        try
        {

            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, loc.getStreet());
            cv.put(LATITUDE_COLUMN, loc.getLatitude());
            cv.put(LONGITUDE_COLUMN, loc.getLongitude());
            if (table.equals(CAR_LOC_TABLE))
              cv.put(IS_IN_HISTORY, isInHistory);
            cv.put(IS_IN_FAVORITES, isInFavorites);
            cv.put(TIME_COLUMN, loc.getLastParkingDateToString());
            db.insert(table, null, cv);

        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }
    }


    public void clearLocation(Locations loc, String table) {
        try
        {

            String column = "id";
            String context = "1";
            if (table.equals(CAR_LOC_TABLE)) {
                column = CONTEXT_COLUMN;
                context = loc.getStreet();
            }

            db.delete(table, column + "=? ", new String[]{context});

        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }
    }

    public Locations getLocationBy(String streetName, String table) {
        Locations loc = new Locations(this);
        try
        {

            double lat = 0;
            double lng = 0;
            String whereColumn = "id";
            String context = "1";
            if (!table.equals(CAR_LOC_TABLE)) {
                whereColumn = CONTEXT_COLUMN;
                context = streetName;
            }
            Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE " + whereColumn + " =? ", new String[]{context});

            while (c.moveToNext()) {
                loc.setStreetName(c.getString(c.getColumnIndex(CONTEXT_COLUMN)));
                lat = c.getDouble(c.getColumnIndex(LATITUDE_COLUMN));
                lng = c.getDouble(c.getColumnIndex(LONGITUDE_COLUMN));
                loc.setCoordinates(new LatLng(lat, lng));
                if (table.equals(CAR_LOC_TABLE) && c.getString(c.getColumnIndex(IS_IN_HISTORY)).equals("1"))
                    loc.setInHistory(true);
                else if (table.equals(CAR_LOC_TABLE))
                    loc.setInHistory(false);
                if (c.getString(c.getColumnIndex(IS_IN_FAVORITES)).equals("1"))
                    loc.setInFavorites(true);
                else
                    loc.setInFavorites(false);
                loc.setLastTimeParked(loc.stringToDate(c.getString(c.getColumnIndex(TIME_COLUMN)), "EEE MMM d HH:mm:ss zz yyyy"));


            }
            c.close();

            return loc;
        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }
        return loc;
    }

    public void updateLocation(Locations loc, String table) {
        String isInHistory = "0";
        String isInFavorites = "0";
        String column = "id";
        String context = "1";
        if (!table.equals(CAR_LOC_TABLE)) {
            column = CONTEXT_COLUMN;
            context = loc.getStreet();
        }
        if (loc.isInHistory() == true)
            isInHistory = "1";
        if (loc.isInFavorites() == true)
            isInFavorites = "1";
        try
        {

            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, loc.getStreet());
            cv.put(LATITUDE_COLUMN, loc.getLatitude());
            cv.put(LONGITUDE_COLUMN, loc.getLongitude());
            if (table.equals(CAR_LOC_TABLE))
              cv.put(IS_IN_HISTORY, isInHistory);
            cv.put(IS_IN_FAVORITES, isInFavorites);
            cv.put(TIME_COLUMN, loc.getLastParkingDateToString());
            String[] args = new String[]{context};
            db.update(table, cv, column +
                    "=?", args);

        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }
    }

    public boolean chekIfLocationExist(Locations loc, String table)
    {
        try
        {

            String column = "id";
            String context = "1";
            if (!table.equals(CAR_LOC_TABLE))
            {
                column = CONTEXT_COLUMN;
                context = loc.getStreet();
            }
            Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE " + column + " =? ", new String[]{context});
            if (c.getCount()>0)
                return true;


        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return false;
    }

    public void clearLocationAtIndex (Locations loc, String table, int atIndex)
    {
        try
        {

            String column = "id";
            String context = String.valueOf(atIndex);
            db.delete(table, column + "=? ", new String[]{context});

        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }

    }

    public Locations getLocationByIndex (int index, String table)
    {
        Locations loc = new Locations (this);
        try
        {

            double lat = 0.0, lng = 0.0;
            String context = String.valueOf(index);
            Cursor c = db.rawQuery("SELECT * FROM " + table + " WHERE id=?", new String[]{context} );
            if (c.moveToFirst())
            {
                loc.setStreetName(c.getString(c.getColumnIndex(CONTEXT_COLUMN)));
                lat = c.getDouble(c.getColumnIndex(LATITUDE_COLUMN));
                lng = c.getDouble(c.getColumnIndex(LONGITUDE_COLUMN));
                loc.setCoordinates(new LatLng(lat, lng));
                if (table.equals(CAR_LOC_TABLE) && c.getString(c.getColumnIndex(IS_IN_HISTORY)).equals("1"))
                    loc.setInHistory(true);
                else if (table.equals(CAR_LOC_TABLE))
                    loc.setInHistory(false);
                if (c.getString(c.getColumnIndex(IS_IN_FAVORITES)).equals("1"))
                    loc.setInFavorites(true);
                else
                    loc.setInFavorites(false);
                loc.setLastTimeParked(loc.stringToDate(c.getString(c.getColumnIndex(TIME_COLUMN)), "EEE MMM d HH:mm:ss zz yyyy"));
            }

            return loc;
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return loc;
    }

    public ArrayList <Locations> getAllLocations (String table)
    {
        ArrayList <Locations> locations = new ArrayList<>();

        try
        {

            int numberOfItems = getProfilesCount(table);
            for (int i = 1; i<=numberOfItems; i++)
                locations.add(getLocationByIndex(i, table));
            return locations;
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return locations;
    }
}

