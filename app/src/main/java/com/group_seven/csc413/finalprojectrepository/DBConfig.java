package com.group_seven.csc413.finalprojectrepository;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;
import java.util.*;
/**
 * Created by Jose Ortiz Costa on 4/18/15.
 * Just a base class to start working on the database
 *
 */
public class DBConfig
{
    private Context context;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "appDatabase.db";
    private static final String APP_INFO_TABLE = "appInfo";
    private static final String USER_LOC_TABLE = "user";
    private static final String CAR_LOC_TABLE = "car";
    private static final String APP_NAME_COLUMN = "appName";
    private static final String APP_VERSION_COLUMN = "appVersion";
    private static final String APP_STATUS = "appStatus";
    private static final String LOCATION_COLUMN = "Location";
    private static final String CONTEXT_COLUMN = "context";
    private static final String LATITUDE_COLUMN = "latitude";
    private static final String LONGITUDE_COLUMN = "longitude";
    private static final String TIME_COLUMN = "time";
    private static final String DIST_FROM_COLUMN = "distanceFrom";
    private static final String IS_CAR_PARKED_COLUMN = "isCarParked";
    public static final int LOCATION = 0;
    public static final int LONGITUDE = 1;
    public static final int LATITUDE = 2;
    public static final int DISTANCE_FROM_OTHER_LOCATION = 3;
    public static final int TIME = 4;
    public static final int IS_CAR_PARKED = 5;
    private long itemsInserted;
    /**
     * Author: Jose Ortiz
     * Description: Private constructor only can be invoked
     *              by a instance method
     * @param c Context of the application
    */
    private DBConfig (Context c)
    {
        this.context = c.getApplicationContext();
        itemsInserted = 0;
        loadDatabaseConfig();
    }

    /**
     * Description: Instance method that creates an object of this class
     *              by calling the private constructor
     * @param c Context of the application
     * @return a instance object of this class
     */
    public static DBConfig loadDbConfiguration (Context c)
    {
        return new DBConfig(c);
    }

    /**
     * Description: Opens or creates a database and its tables
     *
     */
    private void loadDatabaseConfig ()
    {

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
                    APP_STATUS + " integer) ");
            // User Location  data table
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    USER_LOC_TABLE +
                    " ( id integer primary key autoincrement, " +
                    CONTEXT_COLUMN + " Text, " +
                    LOCATION_COLUMN + " Text, " +
                    LATITUDE_COLUMN + " Real, " +
                    LONGITUDE_COLUMN + " Real, " +
                    DIST_FROM_COLUMN + " Real, " +
                    TIME_COLUMN + " Text )");
            // Car Location
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    CAR_LOC_TABLE +
                    " ( id integer primary key autoincrement, " +
                    CONTEXT_COLUMN + " Text, " +
                    LOCATION_COLUMN + " Text, " +
                    LATITUDE_COLUMN + " Real, " +
                    LONGITUDE_COLUMN + " Real, " +
                    DIST_FROM_COLUMN + " Real, " +
                    TIME_COLUMN + " Text, " +
                    IS_CAR_PARKED_COLUMN + " Integer )");

        }
        catch (SQLException e)
        {
            // Error database couldn't be created or loaded
            Log.d("DbConfiguration: ", "Error while creating " + DATABASE_NAME +
                    " Detailed error: " + e.getMessage());
        }
    }

    /**
     * Description: Delete a database
     * @param databaseName name of the database
    */
    public void deleteDatabase (String databaseName)
    {
        try
        {
            if (this.context.deleteDatabase(databaseName))
                Log.d("DbConfiguration: ", "database deleted");
        }
        catch (SQLException e)
        {
            Log.d("DbConfiguration: ", "Error deleting " +
                    databaseName + ". Detailed Error: "  + e.getMessage());
        }
    }

    /**
     * Description: Checks database integrity
     * @return true if the database integrity is ok.
     *         Otherwise, returns false.
     */
    public boolean isDatabaseOk ()
    {
        return db.isDatabaseIntegrityOk();
    }

    /**
     * Description: Save a parking location with its correspondent info
     * @param context context in which the data is save ex: my_location1
     * @param location location where the car is parked
     * @param time time at the car was parked
     * @param distanceFromOtherLocation distance of the car from other location
     * @param isCarParked determines if the car is parked
     * @return number of items inserted
     */
    public long saveParkingLocation (String context, String location, String time, double distanceFromOtherLocation, boolean isCarParked)
    {
        int cp = 0;
        if (isCarParked = true)
            cp = 1;
        try {
            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, context);
            cv.put(LOCATION_COLUMN, location);
            cv.put(DIST_FROM_COLUMN, distanceFromOtherLocation);
            cv.put(TIME_COLUMN, time);
            cv.put(IS_CAR_PARKED_COLUMN, cp);
            return db.insert(CAR_LOC_TABLE, null, cv);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return 0;
    }

    /**
     * Description: Saves a car parking location by latitude and longitude
     * @param context
     * @param latitude
     * @param longitude
     * @param distanceFromOtherLocation
     * @param time
     * @param isCarParked
     * @return
     */
    public long saveParkingLocationByCoordinates (String context, float latitude, float longitude,
                                                  String distanceFromOtherLocation, String time, boolean isCarParked)
    {
        int cp = 0;
        if (isCarParked = true)
            cp = 1;
        try
        {
            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, context);
            cv.put(LATITUDE_COLUMN, latitude);
            cv.put(LONGITUDE_COLUMN, longitude);
            cv.put(TIME_COLUMN, time);
            cv.put(DIST_FROM_COLUMN, distanceFromOtherLocation);
            cv.put(IS_CAR_PARKED_COLUMN, cp);
            return db.insert(CAR_LOC_TABLE, null, cv);

        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return 0;
    }

    /**
     * Description gets the car parking info location
     * @param context the context in which the info was saved: eg: my_Location1
     * Usage: Create an ArrayList eg: ArrayList a = new ArrayList <>()
     *        then, a = getParkingLocationInfo(your context);
     *        to get a item for example location: a.get(DBConfig.LOCATION);
     *        see a complete example in the method testingDatabaseIntegrity
     * @return an Arraylist of objects containing the parking info
     *
     *
    */
    public ArrayList  getParkingLocationInFo (String context)
    {
        ArrayList  carParkingInfo = new ArrayList<>();
        try
        {
            Cursor c = db.rawQuery("SELECT * FROM " + CAR_LOC_TABLE +
                    " WHERE " + CONTEXT_COLUMN + " = ? ", new String[]{context});
            while (c.moveToNext())
            {
                carParkingInfo.add(c.getString(c.getColumnIndex(LOCATION_COLUMN)));
                carParkingInfo.add(c.getFloat(c.getColumnIndex(LATITUDE_COLUMN)));
                carParkingInfo.add(c.getFloat(c.getColumnIndex(LONGITUDE_COLUMN)));
                carParkingInfo.add(c.getFloat(c.getColumnIndex(DIST_FROM_COLUMN)));
                carParkingInfo.add(c.getString(c.getColumnIndex(TIME_COLUMN)));
                carParkingInfo.add(c.getInt(c.getColumnIndex(IS_CAR_PARKED_COLUMN)));


            }
            c.close();
            return carParkingInfo;
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }

        return null;
    }

    /**
     *
     * @param context
     * @param location
     * @param time
     * @param distanceFromOtherLocation
     * @return
     */
    public long saveUserLocation (String context, String location, String time, double distanceFromOtherLocation)
    {

        try {
            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, context);
            cv.put(LOCATION_COLUMN, location);
            cv.put(DIST_FROM_COLUMN, distanceFromOtherLocation);
            cv.put(TIME_COLUMN, time);
            return db.insert(USER_LOC_TABLE, null, cv);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return 0;
    }

    /**
     *
     * @param context
     * @param latitude
     * @param longitude
     * @param distanceFromOtherLocation
     * @param time
     * @return
     */
    public long saveUserLocationByCoordinates (String context, float latitude, float longitude,
                                               double distanceFromOtherLocation, String time)
    {

        try
        {
            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, context);
            cv.put(LATITUDE_COLUMN, latitude);
            cv.put(LONGITUDE_COLUMN, longitude);
            cv.put(TIME_COLUMN, time);
            cv.put(DIST_FROM_COLUMN, distanceFromOtherLocation);
            return db.insert(USER_LOC_TABLE, null, cv);

        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return 0;
    }

    /**
     *
     * @param context
     * @return
     */
    public ArrayList  getUserLocationInFo (String context)
    {
        ArrayList  carParkingInfo = new ArrayList<>();
        try
        {
            Cursor c = db.rawQuery("SELECT * FROM " + USER_LOC_TABLE +
                    " WHERE " + CONTEXT_COLUMN + " = ? ", new String[]{context});
            while (c.moveToNext())
            {
                carParkingInfo.add(c.getString(c.getColumnIndex(LOCATION_COLUMN)));
                carParkingInfo.add(c.getFloat(c.getColumnIndex(LATITUDE_COLUMN)));
                carParkingInfo.add(c.getFloat(c.getColumnIndex(LONGITUDE_COLUMN)));
                carParkingInfo.add(c.getFloat(c.getColumnIndex(DIST_FROM_COLUMN)));
                carParkingInfo.add(c.getString(c.getColumnIndex(TIME_COLUMN)));

            }
            c.close();
            return carParkingInfo;
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }

        return null;
    }

    /**
     *
    */
    public void testingDatabaseIntegrity ()
    {
        // Test Integrity
        if (db.isDatabaseIntegrityOk())
            Log.d("DBTest: ", "Database Integrity OK");
        else
            Log.d("DBTest: ", "Database Integrity OK");
        // Add a car parking Location
        long locationsSavedInRow1 = saveParkingLocation("cs413test1", "97 Valencia San Francisco CA", "6:00", 34.00 ,true );
        long locationsSavedInRow2 = saveUserLocation("cs413test2", "100 Market San Francisco CA", "2:00", 55.00 );
        // Check items inserted
        Log.d("DBTest: ", "Items inserted in row1: " + Long.toString(locationsSavedInRow1));
        Log.d("DBTest: ", "Items inserted in row2: " + Long.toString(locationsSavedInRow2));
        // Get items
        ArrayList carLocationInfo = new ArrayList<>();
        carLocationInfo = getParkingLocationInFo("cs413test1");
        Log.d("DBTest: ", "Car Location: " + carLocationInfo.get(DBConfig.LOCATION));
        Log.d("DBTest: ", "Car parked at time: " + carLocationInfo.get(DBConfig.TIME));
        Log.d("DBTest: ", "Distance from other location : " + carLocationInfo.get(DBConfig.DISTANCE_FROM_OTHER_LOCATION));
        boolean isCarParked = false;
        if ((int) carLocationInfo.get(IS_CAR_PARKED) > 0)
            isCarParked = true;
        else
            isCarParked = false;
        Log.d("DBTest: ", "Is the car parked: " + isCarParked);
        ArrayList userLocationInfo = new ArrayList<>();
        userLocationInfo = getUserLocationInFo("cs413test2");
        Log.d("DBTest: ", "User Location : " + userLocationInfo.get(DBConfig.LOCATION));
    }

    /*
       Update and delete methods comming soon
     */

}