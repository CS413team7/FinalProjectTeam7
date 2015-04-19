package com.group_seven.csc413.finalprojectrepository;
import android.app.Application;
/**
 *  Author:       Jose Ortiz
 *  File:         Global.java
 *  Description:  This class sets the database as
 *                global to be accessible in the whole
 *                project. The global instance referred to
 *                this class was included in the app manifest
 *                as android:name = "Global"
 *  Usage:        * To set the database as global use this code
 *                ((Global) this.getApplication()).setDatabaseContext(DatabaseConfig.getDatabaseConfiguration);
 *                * To get the global variable of the database in any of the activities of this project:
 *                DatabaseConfig db = ((MyApplication) this.getApplication()).getDatabaseContext();
 */
public class Global extends Application {
    // Instance to DatabaseConfig class
    private DatabaseConfig databaseContext;

    /**
     * Description:  Sets the app database as global for all the project
     * @param dbc     represents a object of the DatabaseConfig class
     * usage:        On the onCreate method of the launcher activity:
     *                ((Global) this.getApplication()).setDatabaseContext(DatabaseConfig.getDatabaseConfiguration);
     * @see           com.group_seven.csc413.finalprojectrepository.DatabaseConfig
     */
    public void setDatabaseContext(DatabaseConfig dbc) {
        databaseContext = dbc;
    }

    /**
     *
     * Description:  Returns a db global variable
     * usage:        In any activity of the project:
     *                DatabaseConfig db = ((MyApplication) this.getApplication()).getDatabaseContext();
     * @return        the database as a global variable
     * @see           com.group_seven.csc413.finalprojectrepository.DatabaseConfig
     */
    public DatabaseConfig getDatabaseContext() {
        return databaseContext;
    }
} // ends
