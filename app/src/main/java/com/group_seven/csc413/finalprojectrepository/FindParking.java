package com.group_seven.csc413.finalprojectrepository;

/**
 * Created by Hamoon on 4/23/2015.
 */

//For using Jsoup you need to import Jsoup.jar file
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.jsoup.*;
import org.jsoup.nodes.Document;

/**
 * Class to parse the output String from SF parking API
 */
public class FindParking {

    // It collects all parking locations
    private LatLng[] latlng;
    private String[] names;

    // number records
    private int recordsNumber;

    /**
     * @param data is output String from SF parking API
     * Calls parsString to parse the string
     */
    FindParking(String data){

        Log.d("Hamoon_Cons", data);
        parsString(data);

    }

    /**
     * @param len is the number of records
     * Set the length of latlng
     */
    private void setLength(int len){
        latlng = new LatLng[len];
        names = new String[len];
    }

    /**
     * @return the number of records
     */
    public int getNumOfRecord (){
        return recordsNumber;
    }

    /**
     * @param rec is the number of records
     */
    public void setNumOfRecord (int rec){
        recordsNumber = rec;
    }

    /**
     * @return the double[] latlng to use in other classes
     * other classes can call this method to have all points
     * from SF parking API
     */
    public LatLng[] getGarageLocations () {
        return latlng;
    }


    public String[] getGarageName(){
        return names;
    }


    /**
     *
     * @param data is Output string of SF Parking API
     */
    private void parsString(String data){

        String rec;
        String locations;

        Document html =  Jsoup.parse(data);
        rec = html.body().getElementsByTag("NUM_RECORDS").text();
        locations = html.body().getElementsByTag("LOC").text();
        String[] locCord = locations.split(",");


    //    Log.d("HAmoon", rec);
      //  int numOfRec = Integer.parseInt(rec);
     //   setNumOfRecord(numOfRec);
        setLength(10);

        int i = 0;

        while( i < 10){

            Log.d("Hamoon", locCord[i]);
     //       latlng[i] = new LatLng(Double.parseDouble(loc[0]), Double.parseDouble(loc[1]));

        }
    }


}
