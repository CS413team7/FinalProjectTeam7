package com.group_seven.csc413.finalprojectrepository;

/**
 * @Author:      Jose Ortiz Costa
 * @File:        DBAdapter.java
 * @Date:        05/01/2015
 * @Description: This interface acts as a bridge between the DBConfig class
 *               and the objects that need to implement the necessary methods
 *               to provide functionality to the database and this application
 *
 *
 */
public interface DBAdapter
{

    public void saveInDb (); // save in database
    public void updateInDb (); // updates in database
    public void clearFromDb (); // clear from database
    public Locations getLocationFromDb (); // gets Locations object from database

}
