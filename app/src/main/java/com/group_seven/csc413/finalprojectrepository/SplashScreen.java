package com.group_seven.csc413.finalprojectrepository;


/**
 * Created by Michael Arimas on 4/24/2015.
 */
import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Window;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.content.DialogInterface;
import android.view.Menu;
import android.widget.Toast;
import android.app.AlertDialog;




public class SplashScreen extends Activity {
    private DBConfig db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        ConnectivityManager con_manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // If database exist, load it. Otherwise creates a new one
        ((Global) this.getApplication()).setDatabaseContext(DBConfig.loadDbConfiguration(this));



        //adding wifi manager so that it may be changed within the app if wifi is off
       // WifiManager wifi;
        //wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);


       if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
           Toast.makeText(this, "GPS is On", Toast.LENGTH_SHORT).show();
       }else{
           AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
           alertDialogBuilder.setMessage("GPS is off. Would you like to turn it on?")
                   .setCancelable(false)
                   .setPositiveButton("Turn on GPS",
                           new DialogInterface.OnClickListener(){
                               public void onClick(DialogInterface dialog, int id){
                                   Intent callGPSSettingIntent = new Intent(
                                           android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                   startActivity(callGPSSettingIntent);
                               }
                           });
           alertDialogBuilder.setNegativeButton("Cancel",
                   new DialogInterface.OnClickListener(){
                       public void onClick(DialogInterface dialog, int id){
                           dialog.cancel();
                       }
                   });
           AlertDialog alert = alertDialogBuilder.create();
           alert.show();

               }


        if(con_manager.getActiveNetworkInfo() != null &&
                con_manager.getActiveNetworkInfo().isAvailable() &&
                con_manager.getActiveNetworkInfo().isConnected()){
           Toast.makeText(this,"Internet connection found",Toast.LENGTH_SHORT).show();
        } else{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Connection not found. Turn on WiFi?")
                    .setCancelable(false)
                    .setPositiveButton("Turn on WiFi",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int id){
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id){
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
        }



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

