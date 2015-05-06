package com.group_seven.csc413.finalprojectrepository;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;

/**
 * Created by School on 5/5/15.
 */
public class Favorites extends History
{

    private final int MAX_LOCATIONS_IN_FAVORITES = 10;
    private DBConfig db;
    public Favorites (DBConfig db)
    {
        super (db);
        this.db = db;

    }

    public ArrayList<Locations> getAllFavorites ()
    {
        ArrayList <Locations> favoritesList = new ArrayList<>();
        ArrayList <Locations> historyList = getAllLocationsFromHistory();
        for (Locations location : historyList)
        {
            if (location.isInFavorites() )
                favoritesList.add(location);

        }
        return favoritesList;
    }

    public void removeLocationFromFavorites (Locations loc, boolean wantsToRemoveFromHistory)
    {
        if (loc.isInFavorites())
        {
            loc.setInFavorites(false);
            loc.updateInDb();
        }

        if (wantsToRemoveFromHistory)
            deleteLocationFromHistory(loc);
        updateLocationInHistory(loc);

    }

    public boolean willNotBeAdded (Locations loc)
    {
        ArrayList <Locations> locations = getAllFavorites();
        for (Locations l : locations)
        {
            if (loc.getCoordinates().toString().equals(l.getCoordinates().toString())) {
                Log.d("Li", "true");
                return true;
            }
        }
        return false;
    }

    public boolean addLocationToFavorites (Locations loc)
    {



        if (!willNotBeAdded(loc) && getNumberOfFavoritesLocationsInHistory() < MAX_LOCATIONS_IN_FAVORITES)
        {
            loc.setInFavorites(true);
            Log.d("DbTest", String.valueOf(loc.isInFavorites()));
            loc.updateInDb();
            updateLocationInHistory(loc);
            return true;
        }
        return false;
    }

    public boolean isFavoritesFull ()
    {
        ArrayList <Locations> favorites = getAllFavorites();
        if (favorites.size() == MAX_LOCATIONS_IN_FAVORITES)
            return true;
        return false;
    }

    public boolean isLocationInFavorites (Locations loc)
    {
        ArrayList <Locations> favorites = getAllFavorites();
        for (Locations location : favorites)
        {
            if (location.getCoordinates().toString().equals(loc.getCoordinates().toString()))
                return true;

        }
        return false;
    }


}
