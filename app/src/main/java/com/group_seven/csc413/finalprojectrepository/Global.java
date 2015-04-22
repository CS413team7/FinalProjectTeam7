package com.group_seven.csc413.finalprojectrepository;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.view.Gravity;

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
     *                ((Global) this.getApplication()).setDatabaseContext(DatabaseConfig.loadDbConfiguration(this));
     * @see           com.group_seven.csc413.finalprojectrepository.DatabaseConfig
     */
    public void setDatabaseContext(DatabaseConfig dbc) {
        databaseContext = dbc;
    }

    /**
     *
     * Description:  Returns a db global variable
     * usage:        In any activity of the project:
     *                DatabaseConfig db = ((Global) this.getApplication()).getDatabaseContext();
     * @return        the database as a global variable
     * @see           com.group_seven.csc413.finalprojectrepository.DatabaseConfig
     */
    public DatabaseConfig getDatabaseContext() {
        return databaseContext;
    }

    /**
     * Description:  This method starts the activity that contains the
     *               class given as a parameter from the given context
     *
     * @param c      Context from where the target class is called
     *               eg. if your activity is called myActivity, you
     *               should put in the c parameter this.getBaseContext();
     *               if you put just this, it creates memory leaks
     * @param target Class linked to the activity to be called
     *               eg. If your java file linked to your activity
     *               is myJavaFile.java, you must put in target,
     *               myJavaFile.class
     *
     *
     */
    public static void startActivity (Context c, Class target)
    {
        Intent i = new Intent (c, target);
        c.startActivity(i);
    }

    /**
     * Description: Creates a toast message to be accessible from all the
     *              activities of this application
     * @param c     Context creating the toast message
     * @param message  Message to display
     * @param duration Duration until the toast is gone
     * @param horizontalGravityPosition horizontal position eg. top or bottom
     * @param verticalGravityPosition vertical position eg. right or left
     * Example: Suppose that you are in a activity called myActivity
     *          Then, this is an example how to call this method from
     *          that activity
     *          Global.toastMessage(this, "Toast showing now", Toast.LENGTH_SHORT,
     *                              Gravity.BOTTOM, Gravity.LEFT);
     */
    public static void toastMessage(Context c, String message, int duration,
                                          int horizontalGravityPosition, int verticalGravityPosition)
    {
        Toast toast = Toast.makeText(c.getApplicationContext(), message,
                duration);
        toast.setGravity(horizontalGravityPosition|verticalGravityPosition, 0, 0);
        toast.show();
    }
} // ends
