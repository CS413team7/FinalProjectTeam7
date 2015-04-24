package com.group_seven.csc413.finalprojectrepository;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.database.Cursor;
/**
 * Created by Jose Ortiz Costa on 4/18/15.
 * Just a base class to start working on the database
 *
 */
public class DatabaseConfig
{
    private Context context;
    private SQLiteDatabase db;
    private static final String DATABASE_NAME = "appDatabase.db";
    private static final String APP_INFO_TABLE = "appInfo";
    private static final String MAPS_DATA_TABLE = "mapsData";
    private static final String APP_NAME_COLUMN = "appName";
    private static final String APP_VERSION_COLUMN = "appVersion";
    private static final String APP_STATUS = "appStatus";
    private static final String USER_LOCATION_COLUMN = "userLocation";
    private static final String CAR_PARKED_COLUMN = "parkingLocation";
    private static final String USERNAME_COLUMN = "username";
    private long itemsInserted;

    /**
     * Description: Private constructor only can be invoked
     *              by a instance method
     * @param c Context of the application
     */
    private DatabaseConfig (Context c)
    {
        context = c.getApplicationContext();
        itemsInserted = 0;
        loadDatabaseConfig();
    }

    /**
     * Description: Instance method that creates an object of this class
     *              by calling the private constructor
     * @param c Context of the application
     * @return a instance object of this class
     */
    public static DatabaseConfig loadDbConfiguration (Context c)
    {
        return new DatabaseConfig(c);
    }

    /**
     * Description: Opens or creates a database and its tables
     *
     */
    private void loadDatabaseConfig ()
    {

        try {

            // Open the database if exists. otherwise, creates a new one
            db = context.openOrCreateDatabase(DATABASE_NAME, context.MODE_PRIVATE, null);
            // Creates tables if not exist
            // App Info table
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    APP_INFO_TABLE +
                    " ( id integer primary key autoincrement, " +
                    APP_NAME_COLUMN + " Text," +
                    APP_VERSION_COLUMN + " Text, " +
                    APP_STATUS + " integer) ");
            // Maps data table
            db.execSQL("CREATE TABLE IF NOT EXISTS " +
                    MAPS_DATA_TABLE +
                    " ( id integer primary key autoincrement, " +
                    USERNAME_COLUMN + " Text, " +
                    USER_LOCATION_COLUMN + " Text, " +
                    CAR_PARKED_COLUMN + " Text)");


        }
        catch (SQLException e)
        {
            // Error database couldn't be created or loaded
            Log.d("DbConfiguration: ", "Error while creating " + DATABASE_NAME +
                  " Detailed error: " + e.getMessage());
        }
    }

    public void deleteDatabase (String databaseName)
    {
        try
        {
            if (context.deleteDatabase(databaseName))
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

    public long addParkingLocationToDB (String username, String location)
    {

        try
        {
            ContentValues cv = new ContentValues();
            cv.put(USERNAME_COLUMN, username);
            cv.put(USER_LOCATION_COLUMN, location);
            itemsInserted = db.insert(MAPS_DATA_TABLE, null, cv);
            return itemsInserted;
        }
        catch (SQLException e)
        {
            Log.d("DbException: ", e.getMessage());
        }
        return itemsInserted;
    }

    public String  getParkingLocationFromDB(String username)
    {
        String location = null;
        Cursor c = db.rawQuery("SELECT " + USER_LOCATION_COLUMN + " FROM " + MAPS_DATA_TABLE +
                               " WHERE " + USERNAME_COLUMN + " = ? ", new String[]{username});
        if (c.moveToFirst())
            location = c.getString(c.getColumnIndex(USER_LOCATION_COLUMN));
        return location;
    }


}


