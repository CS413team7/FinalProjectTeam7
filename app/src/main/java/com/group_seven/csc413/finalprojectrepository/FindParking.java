package com.group_seven.csc413.finalprojectrepository;

/**
 * Created by Hamoon on 4/23/2015.
 */

//For using Jsoup you need to import Jsoup.jar file
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Class to parse the output String from SF parking API
 */
public class FindParking {

    // Double XY, Even indexes are latitude and odd indexes longitude
    // So first point is P(XY[0], XY[1])
    private double[] XY;

    // number records
    private int records;

    /**
     * @param data is output String from SF parking API
     * Calls parsString to parse the string
     */
    FindParking(String data){

        parsString(data);
    }

    /**
     * @param len is the number of records
     * Set the length of XY
     */
    private void setLengthOfXY(int len){
        XY = new double[len];
    }

    /**
     * @return the number of records
     */
    public int getRecord (){
        return records;
    }

    /**
     * @param rec is the number of records
     */
    public void setRecord (int rec){
        records = rec;
    }

    /**
     * @return the double[] XY to use in other classes
     * other classes can call this method to have all points
     * from SF parking API
     */
    public double[] getPoints () {
        return XY;
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
        String[] locCord = locations.split(" ");

        int numOfRec = Integer.parseInt(rec);
        setRecord(numOfRec);
        setLengthOfXY(numOfRec);

        int i = 0;
        int j = i + 1;

        while( i < (locCord.length) - 1 & (numOfRec != 0)){

            String[] loc = locCord[i].split(",");

            XY[i] = Double.parseDouble(loc[0]);
            XY[j] = Double.parseDouble(loc[1]);

            i = i + 2;
            j = j + 2;
        }
    }
}
