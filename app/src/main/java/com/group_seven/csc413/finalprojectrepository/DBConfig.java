package com.group_seven.csc413.finalprojectrepository;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

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
    private String userContext;
    private static final String DATABASE_NAME = "appDatabase.db";
    private static final String APP_INFO_TABLE = "appInfo";
    private static final String CAR_LOC_TABLE = "car";
    private static final String APP_NAME_COLUMN = "appName";
    private static final String APP_VERSION_COLUMN = "appVersion";
    private static final String APP_STATUS = "appStatus";
    private static final String CONTEXT_COLUMN = "context";
    private static final String LATITUDE_COLUMN = "latitude";
    private static final String LONGITUDE_COLUMN = "longitude";
    private static final String APP_TIMES_LAUNCHED = "appTimesLaunched";
    private static final String LATITUDE_TO_STRING_COLUMN = "latitudeToString";
    private static final String LONGITUDE_TO_STRING_COLUMN = "longitudeToString";
    private static final String TIME_COLUMN = "time";
    private static final String DIST_FROM_COLUMN = "distanceFrom";
    private static final String IS_CAR_PARKED_COLUMN = "isCarParked";
    private static final String FAVORITES_COLUMN = "favorites";

    /**
     * Author: Jose Ortiz
     * Description: Private constructor only can be invoked
     *              by a instance method
     * @param c Context of the application
    */
    private DBConfig (Context c)
    {
        this.context = c.getApplicationContext();
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
                    APP_STATUS + " INTEGER, " +
                    APP_TIMES_LAUNCHED + " INTEGER DEFAULT 0); ");

            // Car Location
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    CAR_LOC_TABLE +
                    " ( id integer primary key autoincrement, " +
                    CONTEXT_COLUMN + " Text, " +
                    LATITUDE_COLUMN + " Real, " +
                    LONGITUDE_COLUMN + " Real, " +
                    LATITUDE_TO_STRING_COLUMN + " Text, " +
                    LONGITUDE_TO_STRING_COLUMN + " Text, " +
                    DIST_FROM_COLUMN + " Real, " +
                    TIME_COLUMN + " Text, " +
                    IS_CAR_PARKED_COLUMN + " INTEGER DEFAULT 0 );");
            if (getProfilesCount() == 0 )
            {
                saveParkingCoordinates("Default", new LatLng(0,0));
            }

        }
        catch (SQLException e)
        {
            // Error database couldn't be created or loaded
            Log.d("DbConfiguration: ", "Error while creating " + DATABASE_NAME +
                    " Detailed error: " + e.getMessage());
        }
    }

    public void setUserContext (String context)
    {
        this.userContext = context;
    }

    public String getUserContext ()
    {
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
     * Description: Rebuild the whole database
     * @param context Activity context: normally this.getBaseContext()
     * @param database database name
     * Note: All the data stored in the database will be deleted
     */
    public void reBuildDatabase (Context context, String database)
    {
        try
        {
            deleteDatabase(database);
            loadDbConfiguration(context);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }

    }

    /**
     * Description: Saves parking coordinates in the database
     * @param context user context for the parking
     * @param latlng LatLong object containing the coordinates to save
     * @see com.google.android.gms.maps.model.LatLng class
     */
    public void saveParkingCoordinates (String context, LatLng latlng)
    {
        try
        {
            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, context);
            cv.put(LATITUDE_COLUMN, latlng.latitude);
            cv.put(LONGITUDE_COLUMN, latlng.longitude);
            db.insert(CAR_LOC_TABLE, null, cv);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
    }



    public void clearParkingCoordinates ()
    {
        try
        {
               db.delete(CAR_LOC_TABLE, "*", null);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
    }




    /**
     * Description: gets the coordinates of the vehicle parked
     * @param context user context for the parking
     * @return a LatLng object with the parking coordinates
     */
    public LatLng getParkingCoordinates (String context)
    {
        try
        {
            Cursor c = db.rawQuery("SELECT " + LATITUDE_COLUMN + ", " + LONGITUDE_COLUMN +
                    " FROM " + CAR_LOC_TABLE + " WHERE " + CONTEXT_COLUMN + " =? ", new String[]{context});
            double lat = 0, lng = 0;
            while (c.moveToNext()) {
                lat = c.getDouble(c.getColumnIndex(LATITUDE_COLUMN));
                lng = c.getDouble(c.getColumnIndex(LONGITUDE_COLUMN));
            }
            c.close();
            return new LatLng(lat, lng);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return new LatLng(0,0);
    }

    /**
     * Description: updates parking coordinates in a given context
     * @param context user context for the parking
     * @param latlng LatLong object containing the coordinates to update
     */
    public void updateParkingCoordinates (String context, LatLng latlng) {
        try
        {
            ContentValues newValues = new ContentValues();
            newValues.put(LATITUDE_COLUMN, latlng.latitude);
            newValues.put(LONGITUDE_COLUMN, latlng.longitude);
            String[] args = new String[]{context};
            db.update(CAR_LOC_TABLE, newValues, CONTEXT_COLUMN +
                    "=?", args);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
    }

    private HashMap <String, String > prepareLatLngListToInsert ( LatLng [] latlngList)
    {
         HashMap <String, String > hm = new HashMap<>();
         String conctLat = "";
         String concLong = "";
         int counter = 0;
         for (LatLng l : latlngList)
         {
             if (counter < latlngList.length - 1) {
                 conctLat += String.valueOf(l.latitude) + "$";
                 concLong += String.valueOf(l.longitude) + "$";
             }
             else
                conctLat += String.valueOf(l.latitude);
             counter ++;
         }
         hm.put("latitudes", conctLat);
         hm.put("longitudes", concLong);
         return hm;

    }

    public void saveMultipleParkingCoordinates (String userContext, LatLng [] latlngList)
    {
        try
        {
            HashMap<String, String> hm = prepareLatLngListToInsert(latlngList);
            String latitudes = hm.get("latitudes");
            String longitudes = hm.get("longitudes");
            ContentValues cv = new ContentValues();
            cv.put(LATITUDE_TO_STRING_COLUMN, latitudes);
            cv.put(LONGITUDE_TO_STRING_COLUMN, longitudes);
            db.insert(CAR_LOC_TABLE, null, cv);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }

    }

    public LatLng [] getMultipleParkingCoordinates (String userContext)
    {
        try
        {

            Cursor c = db.rawQuery("SELECT " + LATITUDE_TO_STRING_COLUMN + ", " + LONGITUDE_TO_STRING_COLUMN +
                    " FROM " + CAR_LOC_TABLE + " WHERE " + CONTEXT_COLUMN + " =? ", new String[]{userContext});
            String latitude = "", longitude = "";
            while (c.moveToNext()) {
                latitude = c.getString(c.getColumnIndex(LATITUDE_TO_STRING_COLUMN));
                longitude = c.getString(c.getColumnIndex(LONGITUDE_TO_STRING_COLUMN));
            }
            String [] latitudes = latitude.split("$");
            String [] longitudes = longitude.split("$");
            LatLng [] ltnlng = new LatLng[latitudes.length];
            for (int i = 0; i<latitudes.length; i++)
            {

                ltnlng[i] = new LatLng(Double.parseDouble(latitudes[i]),
                                       Double.parseDouble(longitudes[i]));
            }
            return  ltnlng;
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return null;

    }

    /**
     * Description: Save the state of the car parking in the database
     * @param context user context for the parking
     * @param isParked represents the status of the car
     * @since this method can be related to the method saveParkingCoordinates,
     *        you should use both methods in your code
     */
    public void saveParkingStatus (String context, boolean isParked)
    {
        try
        {
            int flag = (isParked)? 1 : 0;
            ContentValues cv = new ContentValues();
            cv.put(CONTEXT_COLUMN, context);
            cv.put(IS_CAR_PARKED_COLUMN, flag);
            db.insert(CAR_LOC_TABLE, null, cv);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
    }

    /**
     * Description: get the parking status by its context
     * @param context user context for the parking
     * @return if the car under this context is parked return true
     *         Otherwise, returns false
     */
    public int getParkingStatus (String context)
    {
        Cursor c = db.rawQuery("Select " + IS_CAR_PARKED_COLUMN + " FROM " + CAR_LOC_TABLE +
                               " Where " + CONTEXT_COLUMN + " =? ", new String[]{context});
        int isParked = 0;
        if (c.moveToFirst())
            isParked =   c.getInt(c.getColumnIndex(IS_CAR_PARKED_COLUMN));
        return isParked;

        /// if (isParked == 1 )
            // return true;
        // return false;
    }

    /**
     * Description: update the car parked status
     * @param context user context for the parking
     * @param isParked represents the status of the car
     */
    public void updateParkingStatus (String context, boolean isParked)
    {
        try
        {
            int flag = (isParked)? 1 : 0;
            Log.d("DBTest ", String.valueOf(flag));
            ContentValues cv = new ContentValues();
            cv.put(IS_CAR_PARKED_COLUMN, flag);
            String[] args = new String[]{context};
            // returns the number of items updated
            db.update(CAR_LOC_TABLE, cv,  CONTEXT_COLUMN +
                    "=?", args);
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
    }








}