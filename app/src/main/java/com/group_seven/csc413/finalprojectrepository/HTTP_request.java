package com.group_seven.csc413.finalprojectrepository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rafael on 4/19/2015.
 */
public class HTTP_request extends AsyncTask{
    private Context context;

    public HTTP_request(Context context){
        this.context = context;
    }

    private void checkInternetConnection(){
        ConnectivityManager check = (ConnectivityManager) this.context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        if (check != null)
        {
            NetworkInfo[] info = check.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i <info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        Toast.makeText(context, "Internet is connected",
                                Toast.LENGTH_SHORT).show();
                    }

        }
        else{
            Toast.makeText(context, "not connected to internet",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onPreExecute(){
        checkInternetConnection();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            String link = (String)objects[0];
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String data = null;
            String webPage = "";
            while((data = reader.readLine()) != null){
                webPage += data + "\n";
            }
            return webPage;
        }catch (Exception e) { return new String("Exception: " + e.getMessage()); }
    }

    @Override
    protected void onPostExecute(Object result){
        //Modify result
    }
}
