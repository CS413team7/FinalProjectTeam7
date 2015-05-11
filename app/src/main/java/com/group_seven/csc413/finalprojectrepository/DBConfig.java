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
 *  @Author:      Jose Ortiz Costa
 *  @date:        04/08/2015
 *  @file:        DBConfig.java
 *  @Description: This class is the core database management of this
 *                aplications, it contains primitive methods such
 *                as insert, update, delete... etc. Those methods
 *                will be implemented or extended by other classes in
 *                order to connect the aplication to this database and
 *                perform those operations
 */
public class DBConfig {
    // Variables and constants
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
    private static final String APPVERSION = "1.0";
    private static final String APPNAME = "HereIsMyCar";
    private static final String APPSTATUS = "debuging";
    private static final String FAVORITES_COLUMN = "favorites";

    /**
     *
     * Description: Private constructor
     * @param c     application context: eg. activity context using this class
     */
    private DBConfig(Context c)
    {
        this.context = c.getApplicationContext();
        loadDatabaseConfig();
    }

    /**
     *
     * Description: Instance method that loads configuration
     *              from this class by returning a object of
     *              it
     * @param c     Application context
     * @return      an object of this class
     */
    public static DBConfig loadDbConfiguration(Context c)
    {
        return new DBConfig(c);
    }

    /**
     * Description: If the application run and the app database does not
     *              exist, then create a new one with its correspondent
     *              tables. Otherwise, just loa the existing database and tables
     *              Also, this method insert the app configuration information
     *              if the application is run for the fist time
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
                    APP_TIMES_LAUNCHED + " INTEGER ); ");

            // Car Place
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    CAR_LOC_TABLE +
                    " ( id integer primary key, " +
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

            if (getProfilesCount(APP_INFO_TABLE) == 0)
                // app was run for the first time
                insertAppConfigInfo();


        } catch (SQLException e) {
            // Error database couldn't be created or loaded
            Log.d("DbConfiguration: ", "Error while creating " + DATABASE_NAME +
                    " Detailed error: " + e.getMessage());
        }
    }


    /**
     *  Description: close the database instance if it was opened
     */
    private void closeDatabase ()
    {
        db.close();
    }

    /**
     * Description:   Sets the user context of this application
     * @param context user context
     */
    public void setUserContext(String context) {
        this.userContext = context;
    }

    /**
     * Description: gets the user context of this application
     * @return      the user application context
     */
    public String getUserContext()
    {
        return userContext;
    }

    /**
     * Description: Count the existing rows in the table car
     * @return      the number of rows of that table
     */
    public int getProfilesCount()
    {

        String countQuery = "SELECT  * FROM " + CAR_LOC_TABLE;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    /**
     * Description: Inserts the application configuration information
     */
    private void insertAppConfigInfo()
    {
        ContentValues cv = new ContentValues();
        cv.put(APP_NAME_COLUMN, APPNAME);
        cv.put(APP_VERSION_COLUMN, APPVERSION);
        cv.put(APP_STATUS, APPSTATUS);
        db.insert(APP_INFO_TABLE, null, cv);

    }

    /**
     * Description: Gets the number of rows in the table
     *              given as an argument
     * @param table table to get the number of rows
     * @return      number of rows in table
     */
    public int getProfilesCount(String table)
    {

        String countQuery = "SELECT  * FROM " + table;
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();

        return cnt;
    }

    /**
     * Description:        Delete a database
     * @param databaseName name of the database
     */
    public void deleteDatabase(String databaseName)
    {
        try {

            if (context.deleteDatabase(databaseName))
                Log.d("DbConfiguration: ", "database deleted");

        } catch (SQLException e) {
            Log.d("DbConfiguration: ", "Error deleting " +
                    databaseName + ". Detailed Error: " + e.getMessage());
        }
    }


    /**
     * Description: Checks database integrity
     * @return      True if the database integrity is ok.
     *              Otherwise, returns false.
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
     * Description:    Rebuild the whole database
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

    /**
     *  Description: Saves location in database
     *  @param loc   object locations to be saved
     *  @param table table where the object locations will be saved
     *  @see:        Locations class
     */
    public void saveLocation(Locations loc, String table)
    {
        // Convert from boolean to Integer because SQLite just
        // accept integers instead of boolean
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

    /**
     * Description: Clears a row in database
     * @param loc   Locations object to be cleared
     * @param table Table where the items to be cleared are stored
     * @see:        Locations class
     */
    public void clearLocation(Locations loc, String table) {
        try
        {

            String column = "id";
            String context = "1";
            if (!table.equals(CAR_LOC_TABLE)) {
                column = CONTEXT_COLUMN;
                context = loc.getStreet();
            }

            db.delete(table, column + "=? ", new String[]{context});

        } catch (SQLException e) {
            Log.d("DbException: ", e.getMessage());
        }
    }

    /**
     * Description:      Gets a location by street name
     * @param streetName Name of the street
     * @param table      Table where location is stored
     * @return           A Locations object
     * @see              Locations class
     */
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

    /**
     * Description: Update a location in database
     * @param loc   Locations object to be updated
     * @param table Table storing items to be updated
     * @see:        Locations class
     */
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

    /**
     * Description: Checks if a location exist in the database
     * @param loc   Locations object to be found
     * @param table Table storing items to be found
     * @return      True if the location was found in the database.
     *              Otherwise, returns false
     */
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

    /**
     * Description:   Clear a location by index
     * @param loc     Locations object to be cleared
     * @param table   Table storing the items to be cleared
     * @param atIndex Index to be cleared
     * @see           Locations class
     */
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

    /**
     * Description: Gets a location by index
     * @param index Index to get the location
     * @param table Table where location is stored
     * @return      Locations object found at the given index
     * @see         Locations class
     */
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

    /**
     * Description: Gets all locations in a given table
     * @param table Table to look for locations
     * @return      An arrayList of Locations objects found
     * @see         Locations class
     */
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

    /**
     * Description: Deletes all items from a given table
     * @param table Table where items to be deleted are stored
     */
    public void deleteAllItems (String table)
    {
        db.execSQL("delete from "+ table);
    }

    /**
     * Description: Closes database instance
     */
    public void closeDb ()
    {
        db.close();
    }
} // ends DBConfig class

