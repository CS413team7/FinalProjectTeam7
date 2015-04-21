package com.group_seven.csc413.finalprojectrepository;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteCursor;
/**
 * Created by Jose Ortiz Costa on 4/18/15.
 * Just a base class to start working on the database
 *
 */
public class DatabaseConfig
{
    private Context context;


    // Private Constructor
    private DatabaseConfig (Context c)
    {
        context = c;
        loadDatabaseConfig();
    }

    // Instance method
    public static DatabaseConfig getDatabaseConfiguration (Context c)
    {
        return new DatabaseConfig(c);
    }

    private void loadDatabaseConfig ()
    {
        // implements database configuration
    }
}
