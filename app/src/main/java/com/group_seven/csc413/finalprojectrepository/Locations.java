package com.group_seven.csc413.finalprojectrepository;

import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import android.content.Context;

/**
 * Created by School on 5/4/15.
 */


public class Locations implements DBAdapter
{
    DBConfig db;
    private LatLng coordinates;
    private double latitude;
    private double longitude;
    private String streetName;
    private Date lastParkingDate;
    private boolean isInHistory;
    private boolean isInFavorites;
    private History locationHistory;
    private Favorites locationFavorites;

    public Locations (DBConfig db)
    {
       this.db = db;
        //locationHistory = new History(db);
        //locationFavorites = new Favorites(db);

    }

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
        //locationHistory = new History(db);
        //locationFavorites = new Favorites(db);


    }



    public void setCoordinates (LatLng latLng)
    {
        this.coordinates = latLng;
        setLatitude(latLng.latitude);
        setLongitude(latLng.longitude);
    }

    public void setLatitude (double lat)
    {
        this.latitude = lat;
    }

    public void setLongitude (double lng)
    {
        this.longitude = lng;
    }

    public void setStreetName (String street)
    {
        this.streetName = street;
    }

    public void setLastTimeParked (Date d)
    {
        this.lastParkingDate = d;
    }

    public void setInHistory (boolean isInHistory)
    {
        this.isInHistory = isInHistory;
    }



    public void setInFavorites (boolean isInFavorites)
    {
        this.isInFavorites = isInFavorites;
    }



    public LatLng getCoordinates ()
    {
        return this.coordinates;
    }

    public double getLatitude ()
    {
        return this.latitude;
    }

    public double getLongitude ()
    {
        return this.longitude;
    }

    public String getStreet ()
    {
        return this.streetName;
    }

    public Date getLastParkingDate ()
    {
        return this.lastParkingDate;
    }

    public String getLastParkingDateToString ()
    {
        return this.lastParkingDate.toString();
    }



    public boolean isInHistory ()
    {
        return this.isInHistory;
    }

    public boolean isInFavorites ()
    {
        return this.isInFavorites;
    }

    public void saveInDb ()
    {
        setInHistory(true);
        db.saveLocation(this, DBConfig.CAR_LOC_TABLE);

    }

    public void updateInDb ()
    {
        setInHistory(true);
        db.updateLocation(this, DBConfig.CAR_LOC_TABLE);
        // locationHistory.updateLocationInHistory(this);
    }

    public void clearFromDb ()
    {
         db.clearLocation(this, DBConfig.CAR_LOC_TABLE);

    }

    public Locations getLocationFromDb()
    {
        return db.getLocationBy("null", DBConfig.CAR_LOC_TABLE);
    }


    public void addToFavorites ()
    {
        setInFavorites(true);
        updateInDb();

    }

    public void removeFromFavorites ()
    {
        setInFavorites(false);
        updateInDb();
        
    }

    public boolean doesExistInDb ()
    {
        return db.chekIfLocationExist(this, DBConfig.CAR_LOC_TABLE);
    }

    public Date stringToDate(String aDate,String aFormat) {

        if(aDate==null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
        Date stringDate = simpledateformat.parse(aDate, pos);
        return stringDate;

    }



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


}
