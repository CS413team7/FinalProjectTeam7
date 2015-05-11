package com.group_seven.csc413.finalprojectrepository;
import java.util.*;
/**
 * @Author:      Jose Ortiz Costa
 * @File:        History.java
 * @Extends:     Locations.java
 * @Date:        04/29/2015
 * @Description: This class, which extends Locations class,
 *               manages the functionality of a location when it is added
 *               or already exist in history.
 *               So,it provides useful methods with all the functions
 *               to manage locations saved in favorites
 * @see Locations class
 *
 *
 */
public class History extends Locations
{

    private final int MAX_LOCATIONS_IN_HISTORY_TABLE = 20;

    /**
     * Description: Class contructor
     * @param db    database instance
     */
    public History (DBConfig db)
    {
        super(db);
    }

    /**
     * Description: Saves a location in history
     * @param loc   Locations object to be saved
     * @see         Locations class
     */
    public void saveLocationInHistory (Locations loc)
    {
        if (db.getProfilesCount(DBConfig.HISTORY_TABLE) ==  MAX_LOCATIONS_IN_HISTORY_TABLE)
           db.clearLocationAtIndex(loc, DBConfig.HISTORY_TABLE, MAX_LOCATIONS_IN_HISTORY_TABLE);
        loc.setInHistory(true);
        db.saveLocation(loc, DBConfig.HISTORY_TABLE);
    }

    /**
     * Description: Updates a existing location in history
     * @param loc   Locations object to be updated
     * @return      True if the object was updated.
     *              Otherwise returns false.
     * @see Locations class
     */
    public boolean updateLocationInHistory (Locations loc)
    {
        if (db.chekIfLocationExist(loc, DBConfig.HISTORY_TABLE))
        {
            db.updateLocation(loc, DBConfig.HISTORY_TABLE);
            return true;
        }
        return false;
    }

    /**
     * Description: Deletes a location from history
     * @param loc   Locations object to be deleted
     * @see Locations class
     */
    public void deleteLocationFromHistory ( Locations loc)
    {
        if (db.chekIfLocationExist(loc, DBConfig.HISTORY_TABLE))
            db.clearLocation(loc, DBConfig.HISTORY_TABLE);
    }

    /**
     * Description: Gets the number of locations existing in history
     * @return      number of locations in history
     */
    public int getNumberOfLocationsInHistory ()
    {
        return db.getProfilesCount(DBConfig.HISTORY_TABLE);
    }

    /**
     * Description: Checks if a location exist in history
     * @param loc   Locations object to be checked
     * @return      True if the location exist in history
     *              Otherwise, returns false
     * @see         Locations class
     */
    public boolean isLocationInHistory (Locations loc)
    {
        return db.chekIfLocationExist(loc, DBConfig.HISTORY_TABLE);
    }

    /**
     * Description: Gets all the locations from history
     * @return      An array list of Locations objects
     * @see Locations class
     */
    public ArrayList <Locations> getAllLocationsFromHistory ()
    {
        return db.getAllLocations(DBConfig.HISTORY_TABLE);

    }

    /**
     *  Description: Clears all the locations in history
     */
    public void clearHistory()
    {
        db.deleteAllItems(DBConfig.HISTORY_TABLE);
    }

    /**
     * Description: Checks is history is empty
     * @return      True if history is empty. Otherwise, returns false
    */
    public boolean isHistoryEmpty()
    {
        return getAllLocationsFromHistory().isEmpty();
    }

    /**
     * Description: Gets the number of favorites in history
     * @return      Number of favorites in history
     */
    public int getNumberOfFavoritesInHistory ()
    {
        int favorites = 0;
        ArrayList <Locations> historyLocations = getAllLocationsFromHistory();
        for (Locations l : historyLocations)
            if (l.isInFavorites()) favorites++;
        return favorites;
    }
} // ends History class