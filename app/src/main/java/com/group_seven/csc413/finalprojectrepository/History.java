package com.group_seven.csc413.finalprojectrepository;
import com.google.android.gms.maps.model.LatLng;
import java.util.*;
import android.content.Context;
/**
 *  Created by Jose Ortiz Costa
 */
public class History extends Locations
{

    private final int MAX_LOCATIONS_IN_HISTORY_TABLE = 20;
    public History (DBConfig db)
    {
        super(db);
    }

    public void saveLocationInHistory (Locations loc)
    {
        if (db.getProfilesCount(DBConfig.HISTORY_TABLE) ==  MAX_LOCATIONS_IN_HISTORY_TABLE)
           db.clearLocationAtIndex(loc, DBConfig.HISTORY_TABLE, MAX_LOCATIONS_IN_HISTORY_TABLE);
        loc.setInHistory(true);
        db.saveLocation(loc, DBConfig.HISTORY_TABLE);
    }

    public boolean updateLocationInHistory (Locations loc)
    {
        if (db.chekIfLocationExist(loc, DBConfig.HISTORY_TABLE))
        {
            db.updateLocation(loc, DBConfig.HISTORY_TABLE);
            return true;
        }
        return false;
    }

    public void deleteLocationFromHistory ( Locations loc)
    {
        if (db.chekIfLocationExist(loc, DBConfig.HISTORY_TABLE))
            db.clearLocation(loc, DBConfig.HISTORY_TABLE);
    }

    public int getNumberOfLocationsInHistory ()
    {
        return db.getProfilesCount(DBConfig.HISTORY_TABLE);
    }

    public int getNumberOfFavoritesLocationsInHistory ()
    {

        return 0;
    }
    public boolean doesLocationExistInHistory (Locations loc)
    {
        return db.chekIfLocationExist(loc, DBConfig.HISTORY_TABLE);
    }
    public ArrayList <Locations> getAllLocationsFromHistory ()
    {
        return db.getAllLocations(DBConfig.HISTORY_TABLE);

    }

    public void clearHistory()
    {
        db.deleteAllItems(DBConfig.HISTORY_TABLE);
    }

    public boolean isHistoryEmpty()
    {
        return getAllLocationsFromHistory().isEmpty();
    }









}