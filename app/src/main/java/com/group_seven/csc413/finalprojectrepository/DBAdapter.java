package com.group_seven.csc413.finalprojectrepository;

/**
 * Created by School on 5/4/15.
 */
public interface DBAdapter
{
    public void saveInDb ();
    public void updateInDb ();
    public void clearFromDb ();
    public Locations getLocationFromDb ();

}
