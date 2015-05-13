package com.group_seven.csc413.finalprojectrepository;
import java.util.ArrayList;
import android.util.Log;
/**
 * @Author:      Jose Ortiz Costa
 * @File:        Favorites.java
 * @Extends:     History.java
 * @Date:        04/29/2015
 * @Description: This class, which extends History class,
 *               manages the functionality of a location when its exist in favorites.
 *               So,it provides useful methods with all the functions
 *               to manage locations saved in favorites
 * @see History class
 *
 *
 */
public class Favorites extends History
{


    private final int MAX_LOCATIONS_IN_FAVORITES = 10;
    private DBConfig db; // database instance

    /**
     * Description: Class constructor
     * @param db: database instance
     */
    public Favorites (DBConfig db)
    {
        super (db);
        this.db = db;
    }

    /**
     * Description: Gets all locations stored in favorites
     * @return an arrayList of Locations objects in favorites
     */
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

    /**
     * Description: Deletes a existing location in favorites
     * @param loc   Locations object to be deleted
     * @return      True if the location was found and deleted.
     *              Otherwise, returns false
     * @see Locations class
     */
    public boolean deleteLocationFromFavorites (Locations loc)
    {
        if (isLocationInFavorites(loc))
        {
            Log.d("ISlocation", "true");
            loc.setInFavorites(false);
            updateLocationInHistory(loc);
            return true;
        }
        return false;
    }

    /**
     * Description: Add a location to favorites
     * @param loc   Locations object to be added
     * @return      True if the location object given was successfully
     *              added to favorites. Otherwise, returns false
     */
    public boolean addLocationToFavorites (Locations loc)
    {

        if (isLocationInFavorites(loc) == false  && getNumberOfFavoritesInHistory() < MAX_LOCATIONS_IN_FAVORITES)
        {
            loc.setInFavorites(true);
            loc.updateInDb(); // Updates added to favorites in main database
            updateLocationInHistory(loc);
            return true;
        }
        return false;
    }

    /**
     * Description: Checks if favorites is full
     * @return      True is favorites is full. Otherwise, returns false
     */
    public boolean isFavoritesFull ()
    {
        ArrayList <Locations> favorites = getAllFavorites();
        if (favorites.size() == MAX_LOCATIONS_IN_FAVORITES)
            return true;
        return false;
    }

    /**
     * Description: Checks if a location exists in favorites
     * @param loc   Locations object to be checked
     * @return      True is the location exists in favorites.
     *              Otherwise, returns false
     */
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

    /**
     *  Description: Clears all locations objects in favorites
     */
    public void clearFavorites ()
    {
        ArrayList <Locations> fav = getAllFavorites();
        for (Locations f : fav)
        {
            deleteLocationFromFavorites(f);
        }
    }
} // ends Favorites class
