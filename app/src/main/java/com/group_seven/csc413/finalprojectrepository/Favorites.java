package com.group_seven.csc413.finalprojectrepository;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import java.util.*;

/**
 *  Created by Jose Ortiz Costa
 */
public class Favorites
{
    DBConfig db;


    public Favorites (DBConfig database)
    {
       this.db = database;
    }

    public boolean isLocationInFavorites (LatLng loc)
    {
        LatLng [] ltn = getAllFavoriteLocations();
        for (LatLng l : ltn)
        {
            if (l.equals(loc))
                return true;
        }
        return false;
    }

    public LatLng [] getMatchedLocationsFromFavorites (LatLng [] location)
    {
        ArrayList <LatLng> locations = new ArrayList<>();
        for (LatLng l : location)
        {
            if (isLocationInFavorites(l))
               locations.add(l);
        }
        return locations.toArray(new LatLng[locations.size()]);
    }

    public boolean addLocationToFavorites (LatLng location){
        return db.saveLocationInFavorites(location);
    }

    public boolean addUniqueToFavorites(LatLng location){
        if(!isLocationInFavorites(location))
            return addLocationToFavorites(location);
        return false;
    }

    public int isFavoritesFull(){
        /*if(db.getProfilesCount("favorites") > 10)
            return true;
        return false;*/
        return db.getProfilesCount("favorites");
    }

    public int removeLocationFromFavorites (LatLng location)
    {
        return db.deleteLocationFromFavorites(location);
    }

    public int removeLocationsFromFavorites(LatLng [] locations)
    {
        return db.deleteLocationsFromFavorites(locations);
    }

    public LatLng [] getAllFavoriteLocations ()
    {
        return db.getLocationsFromFavorites();
    }


    // Rafael, do your stuff here to have the code more organized
    // So we can have all the favorites stuff just in one place
}
