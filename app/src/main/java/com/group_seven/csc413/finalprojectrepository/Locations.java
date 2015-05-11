package com.group_seven.csc413.finalprojectrepository;
import com.google.android.gms.maps.model.LatLng;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
/**
 * @Author:      Jose Ortiz Costa
 * @File:        Locations.java
 * @Extends:     DBAdapter.java
 * @Date:        04/29/2015
 * @Description: This class implements methods from the DBAdapter interface
 *               which acts as an adapter between objects and the database
 *               This class represents a Location object which once
 *               created and set correctly will contain all the information
 *               related to a location such as coordinates, street name, is
 *               in history, is in favorites, and date of the last parking
 * @see DBAdapter interface
 *
 *
 */


public class Locations implements DBAdapter
{
    DBConfig db; // database instance
    private LatLng coordinates;
    private double latitude;
    private double longitude;
    private String streetName;
    private Date lastParkingDate;
    private boolean isInHistory;
    private boolean isInFavorites;
    private History locationHistory;
    private Favorites locationFavorites;

    /**
     * Description: Constructor
     * @param db database instance
     */
    public Locations (DBConfig db)
    {
       this.db = db;
    }

    /**
     * Description: Constructor
     * @param db database instance
     * @param latLng Coordinates object
     * @param streetName Streen name
     */
    public Locations (DBConfig db, LatLng latLng, String streetName)
    {
        this.db = db;
        setCoordinates(latLng);
        setStreetName(streetName);
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
        setInHistory(false);
        setInFavorites(false);
        setLastTimeParked(new Date());

    }

    /**
     * Description: Sets location coordinates
     * @param latLng coordinates object
     */
    public void setCoordinates (LatLng latLng)
    {
        this.coordinates = latLng;
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
    }

    /**
     * Description: Sets latitude
     * @param lat latitude value
     */
    public void setLatitude (double lat)
    {
        this.latitude = lat;
    }

    /**
     * Description: Sets Longitude
     * @param lng longitude value
     */
    public void setLongitude (double lng)
    {
        this.longitude = lng;
    }

    /**
     * Description: Sets the name of the street of this location
     * @param street the street name
     */
    public void setStreetName (String street)
    {
        this.streetName = street;
    }

    /**
     * Description: Sets the last time a car parked in this location
     * @param d time and date parked
     */
    public void setLastTimeParked (Date d)
    {
        this.lastParkingDate = d;
    }

    /**
     * Description:       Determines if a location will be saved in history
     * @param isInHistory if true, this location will be saved in history
     *                    Otherwise, it won't be saved
     */
    public void setInHistory (boolean isInHistory)
    {
        this.isInHistory = isInHistory;
    }

    /**
     * Description:         Determines if a location will be saved in favorites
     * @param isInFavorites if true, this location will be saved in favorites
     *                      Otherwise, it won't be saved
     *
     */
    public void setInFavorites (boolean isInFavorites)
    {
        this.isInFavorites = isInFavorites;
    }

    /**
     * Description: get coordinates of this location
     * @return a LatLng containing the coordinates
     * @see LatLng class
     */
    public LatLng getCoordinates ()
    {
        return this.coordinates;
    }

    /**
     * Description: Gets the latitude of this location
     * @return the latitude value
     */
    public double getLatitude ()
    {
        return this.latitude;
    }

    /**
     * Description: Gets the longitude of this location
     * @return the longitude value
     */
    public double getLongitude ()
    {
        return this.longitude;
    }

    /**
     * Description: Gets the name of the street for this location
     * @return the name of the street
     */
    public String getStreet ()
    {
        return this.streetName;
    }

    /**
     * Description: Gets the last date and time parked in this location
     * @return a Date object
     */
    public Date getLastParkingDate ()
    {
        return this.lastParkingDate;
    }

    /**
     * Description: Gets the last date and time parked in this location
     *              as a string object
     * @return a string representation of the Date object
     */
    public String getLastParkingDateToString ()
    {
        return this.lastParkingDate.toString();
    }

    /**
     * Description: Determines if the location was saved in history
     * @return true if the location is in history. Otherwise, returns false
     */
    public boolean isInHistory ()
    {
        return this.isInHistory;
    }

    /**
     * Description: Determines if the location was saved in favorites
     * @return true if the location is in favorites. Otherwise, returns false
     */
    public boolean isInFavorites ()
    {
        return this.isInFavorites;
    }

    /**
     * Description: saves this location in the database
     */
    public void saveInDb ()
    {
        setInHistory(true); // saves in history too
        db.saveLocation(this, DBConfig.CAR_LOC_TABLE);

    }
    /**
     *  Description: Updates a this location in the database if it exist
     */
    public void updateInDb ()
    {
        setInHistory(true); // updates in history too
        db.updateLocation(this, DBConfig.CAR_LOC_TABLE);

    }

    /**
     *  Description: Clears location from the database
     */
    public void clearFromDb ()
    {
         db.clearLocation(this, DBConfig.CAR_LOC_TABLE);

    }

    /**
     * Description: gets a location from the database
     * @return a Locations object
     */
    public Locations getLocationFromDb()
    {
        return db.getLocationBy("null", DBConfig.CAR_LOC_TABLE);
    }

    /**
     * Description: Adds a location to favorites
     */
    public void addToFavorites ()
    {
        setInFavorites(true);
        updateInDb();

    }

    /**
     *  Description: Remove location from favorites
     */
    public void removeFromFavorites ()
    {
        setInFavorites(false);
        updateInDb();
        
    }

    /**
     * Description: Checks if this location already exist in the database
     * @return true if location already exist in database. Otherwise, returns false
     */
    public boolean doesExistInDb ()
    {
        return db.chekIfLocationExist(this, DBConfig.CAR_LOC_TABLE);
    }

    /**
     * Description: Convert a valid data in string format to a Date object
     * @param aDate String representation of the date
     * @param aFormat format to convert
     * @return a Date object
     */
    public Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }

    /**
     * Description: Overrides the method toString from the Object class to Provide
     *              a complete description of the attributes found in this object
     * @return      a descriptive string containing the attributes of
     *              this object
     */
    @Override
    public String toString()
    {
        super.toString();
        return "Location Description \n" +
                "Street Name: " + getStreet() + "\n" +
                "Coordinates: " + getCoordinates().toString() + "\n" +
                "Last Date Parked: " + getLastParkingDateToString() + "\n" +
                "Is this Location in Favorites: " + isInFavorites();
    }
} // ends Locations class
