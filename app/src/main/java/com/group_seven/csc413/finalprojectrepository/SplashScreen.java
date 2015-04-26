package com.group_seven.csc413.finalprojectrepository;


/**
 * Created by Michael Arimas on 4/24/2015.
 */
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;



public class SplashScreen extends Activity {
    private DBConfig db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);
        // If database exist, load it. Otherwise creates a new one
        ((Global) this.getApplication()).setDatabaseContext(DBConfig.loadDbConfiguration(this));


        //adding wifi manager so that it may be changed within the app if wifi is off
        WifiManager wifi;
        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);


        Thread startTimer = new Thread(){
            public void run(){
                try {
                    sleep(5000);

                    Intent i = new Intent(SplashScreen.this, MapsActivity.class);
                    startActivity(i);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        startTimer.start();

    }


}
