package com.group_seven.csc413.finalprojectrepository;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends ActionBarActivity implements OnMapLongClickListener, OnMarkerClickListener, View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng currentLocation;
    private LatLng pinLocation;
    private CameraPosition cameraPosition;
    private String resultOn, resultOff;
    private Marker mPin; //Single and multiple marker origin
    private boolean pinExists = false;
    private boolean isParked = false;
    private boolean isNavigating = false;
    private DBConfig db;
    private JSONObject jObjectOn;
    private JSONObject jObjectOff;
    LatLng lastParkedLocation;
    LatLng parkedLocation;
    Date timeParked;
    Marker currentParkedMarker; //Use to draw car icon
    RelativeLayout overlay;
    RelativeLayout navigationOverlay;
    private Locations myLocation;
    private History myHistory;
    private Favorites myFavorites;
    private TextView textProgress;
    private int historyIndex;
    private ArrayList <Locations> historyLocations;
    private ArrayList <Locations> favoriteLocations;
    private Button endNavigation;
    private Button drivingNav;
    private Button walkingNav;
    private GoogleDirection gd;

    private Menu myMenu;

    JSONArray jArrayOn, jArrayOff;

    String urlOn = "http://api.sfpark.org/sfpark/rest/availabilityservice?radius=5.0&response=json&pricing=yes&version=1.0&type=on";
    String urlOff = "http://api.sfpark.org/sfpark/rest/availabilityservice?radius=5.0&response=json&pricing=yes&version=1.0&type=off";



    //Enum for easily marking price overlays
    public enum OverlayType {
        PRICE, AVILABILITY
    }

    //Enum for showing weight of drawn path
    public enum OverlayWeight {
        HIGH,MEDIUM,LOW
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        overlay = (RelativeLayout) findViewById (R.id.overlay);
        navigationOverlay = (RelativeLayout) findViewById (R.id.navigationOverlay);

        db = ((Global) this.getApplication()).getDatabaseContext();
        overlay.setVisibility(View.GONE);
        navigationOverlay.setVisibility(View.GONE);
        myLocation = new Locations(db);
        myHistory = new History(db);
        myFavorites = new Favorites(db);
        endNavigation = (Button) findViewById(R.id.endNavigation);
        drivingNav = (Button) findViewById(R.id.drive_nav);
        walkingNav = (Button) findViewById(R.id.walking_nav);
        endNavigation.setOnClickListener(this);
        drivingNav.setOnClickListener(this);
        walkingNav.setOnClickListener(this);


        /*
             Uncomment to delete database and rebuild the database at runtime
             Warning: All the data stored before of the rebuild will be lost
             Remember to comment it again, after the database is rebuilt.
         */

        //db.reBuildDatabase(this, "appDatabase.db");
        setUpMapIfNeeded(); // setUpMapIfNeeded must be called after db is being loaded/created
        loadParkingInfo();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.endNavigation: {
                clearNavigation();
                break;
            }
            case  R.id.drive_nav: {
                unPark();
                break;
            }
            case  R.id.walking_nav: {
                unPark();
                break;
            }
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call  once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true); //Enable Location Button
                mMap.getUiSettings().setZoomControlsEnabled(true); //Enable Zoom Button

                currentLocation = getCurrentLocation();
                animateCamera(currentLocation);
                Locations myLocation = new Locations (db, currentLocation, getStreetName(currentLocation));
                // now 0 is empty table, and 1 is location in table
                if(db.getProfilesCount() == 1)
                {

                    isParked = true;
                    parkedLocation =  loadParkedLocation();
                    animateCamera(parkedLocation);
                    invalidateOptionsMenu();
                    drawParkedCar(parkedLocation);
                    overlay.setVisibility(View.VISIBLE);

                }

                mMap.setOnMarkerClickListener(this);
                mMap.setOnMapLongClickListener(this);
            }
        }
    }

    void animateCamera(LatLng thisLocation){
        cameraPosition = new CameraPosition.Builder()
                .target(thisLocation)
                .zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    LatLng getCurrentLocation() {
        Location myLocation = mMap.getMyLocation();

        if (myLocation == null) {
            LocationManager myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            myLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = myLocationManager.getBestProvider(criteria, true);
            myLocation = myLocationManager.getLastKnownLocation(provider);
            if(myLocation != null)
                return (new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            return  (new LatLng(37.721895, -122.4797));
        }
        else
            return (new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        drawPin(latLng);

        /*
        if(pinExists && !mPin.equals(currentParkedMarker))
            mPin.remove();
        pinExists = true;

        if(circleExists)
            markerCircle.remove();
        circleExists = true;

        currentLocation = latLng;
        String address = getStreetName(latLng);
        mPin = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false).title(address));
        animateCamera(currentLocation);
        CircleOptions markerRadius = new CircleOptions().center(currentLocation).radius(402.336).strokeWidth(5);
        markerCircle = mMap.addCircle(markerRadius);
        */




        /**int b = db.getProfilesCount(); //This mess is mine(Rafael), ill leave just in case - ill delete it later
        //db.saveParkingCoordinates("My Current Parked Location", new LatLng(11,22));
        LatLng y = db.getParkingCoordinates("My Current Parked Location");

        //String x = String.valueOf(y.latitude);
        //String a = String.valueOf(parkedLocation.latitude);
        //String y = String.valueOf(parkedLocation.longitude);
        //x = x + ", " + a;
        String x = "Count: " + b + "  C-Result: " +  y  + "  C-Real: "  + parkedLocation;// String.valueOf(b);**/

        /*String x = "Results: " + getCurrentLocation() + "  PIN: " + latLng;
        Log.d("mytag", x);
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(x);*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mPin = marker;
        mPin.showInfoWindow();
        pinLocation = mPin.getPosition();
        animateCamera(pinLocation);

            /*if(isParked) {
                myMenu.findItem(R.id.park_button).setVisible(false);
                myMenu.findItem(R.id.cancel_button).setVisible(false);
                myMenu.findItem(R.id.deleteMarker_button).setVisible(!parkedLocation.equals(currentLocation));
                myMenu.findItem(R.id.saveHistory_button).setVisible(true);
            }
            else if(!isParked){
                myMenu.findItem(R.id.park_button).setVisible(false);
                myMenu.findItem(R.id.cancel_button).setVisible(false);
            myMenu.findItem(R.id.deleteMarker_button).setVisible(true);
            myMenu.findItem(R.id.saveHistory_button).setVisible(true);
        }*/
        /*
        if(isParked && parkedLocation.equals(currentLocation))
            return true;*/

        myMenu.findItem(R.id.park_button).setVisible(false);
        myMenu.findItem(R.id.cancel_button).setVisible(isParked && parkedLocation.equals(pinLocation));
        myMenu.findItem(R.id.deleteMarker_button).setVisible((isParked && !parkedLocation.equals(pinLocation)) || !isParked);
        myMenu.findItem(R.id.save_button).setVisible(true);
        myMenu.findItem(R.id.navigate_button).setVisible(true);

        return true;
    }



    public void drawParkedCar(LatLng drawLocation) {

        String address = getStreetName(drawLocation);
        currentParkedMarker = mMap.addMarker(new MarkerOptions()
                .position(drawLocation)
                .draggable(false)
                .title(address)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_little_car)));

        currentParkedMarker.showInfoWindow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.myMenu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        myMenu.findItem(R.id.navigate_button).setVisible(false);
        if (isParked) {
                menu.findItem(R.id.deleteMarker_button).setVisible(false);
                menu.findItem(R.id.cancel_button).setVisible(true);
                menu.findItem(R.id.park_button).setVisible(false);
                menu.findItem(R.id.save_button).setVisible(false);
        } else {
                menu.findItem(R.id.deleteMarker_button).setVisible(false);
                menu.findItem(R.id.cancel_button).setVisible(false);
                menu.findItem(R.id.park_button).setVisible(true);
                menu.findItem(R.id.save_button).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.park_button:
                park();
                return true;
            case R.id.cancel_button:
                unPark();
                return true;
            case R.id.about:
                showAbout();
                return true;
            case R.id.history:
                showHistory();
                return true;
            case R.id.show_favorites:
                showFavorites();
                return true;
            case R.id.deleteMarker_button:
                myMenu.findItem(R.id.navigate_button).setVisible(false);
                markerRemove();
                return true;
            case R.id.navigate_button:
                //myMenu.findItem(R.id.navigate_button).setVisible(false);
                navigate(pinLocation, "driving");
                return true;
            case R.id.save_button:
                Locations loc = new Locations(db, currentLocation, getStreetName(currentLocation));

                /*  <Locations> locs = myFavorites.getAllFavorites();
                for (Locations l: locs)
                {
                    Log.d("ListFav", l.toString());
                }*/
                myHistory.saveLocationInHistory(loc);

                if( myFavorites.addLocationToFavorites(loc))
                    Toast.makeText(getApplicationContext(), "Saved to Favorites", Toast.LENGTH_SHORT).show();
                else if(myFavorites.isFavoritesFull())
                    Toast.makeText(getApplicationContext(), "Favorites is Full", Toast.LENGTH_SHORT).show();
                else if(myFavorites.isLocationInFavorites(loc))
                    Toast.makeText(getApplicationContext(), "Already Saved", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showHistory(){
        historyLocations = myHistory.getAllLocationsFromHistory();
        String[] myStreetNames;

        if (myHistory.isHistoryEmpty() == true)
        {
            myStreetNames = new  String[]{"History is Empty"};

        }
        else
            {
                myStreetNames = new String[historyLocations.size()];
                historyLocations.get(0).getStreet();
                for (int i = 0; i < historyLocations.size(); i++) {
                myStreetNames[i] = historyLocations.get(i).getStreet();
            }
        }


        DialogFragment history = HistoryOverlay.newInstance(myStreetNames);
        history.show(getFragmentManager(), "history");

        //if (!myHistory.isHistoryEmpty())
          //drawPin(historyLocations.get(historyIndex).getCoordinates());

    }

    void onHistorySelectValue(int value){
        historyIndex = value;
    }

    void showFavorites(){
        //getAllFavorites doesnt work :( Halp
        favoriteLocations = myFavorites.getAllFavorites();
        String[] myStreetNames = new String[favoriteLocations.size()];
        favoriteLocations.get(0).getStreet();

        for (int i = 0; i < favoriteLocations.size(); i++) {
            myStreetNames[i] = favoriteLocations.get(i).getStreet();
        }



        DialogFragment favorites = FavoritesOverlay.newInstance(myStreetNames);
        favorites.show(getFragmentManager(), "favorites");


    }


    void drawPin(LatLng latLng){
        if(pinExists && !mPin.equals(currentParkedMarker))
            mPin.remove();
        pinExists = true;

        currentLocation = latLng;
        String address = getStreetName(latLng);
        mPin = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false).title(address));
        animateCamera(currentLocation);
    }


    void clearAllHistory(){
         myHistory.clearHistory();
    }



    void myFavorites(){

        boolean a = myFavorites.isFavoritesFull();
        myLocation = new Locations(db, currentLocation, getStreetName(currentLocation));
        String x = "A: " + a  + ", CL: " + currentLocation; // + " : " + parkedLocation;
        boolean b = myFavorites.addLocationToFavorites(myLocation);
        boolean aa = myFavorites.isLocationInFavorites(myLocation);
        boolean c = myFavorites.isFavoritesFull();
        x = x + "\n, B: " + b + ", C: " + c + ", isThere: " + aa;
        Log.d("mytag", x);
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(x);

    }


    void markerRemove(){
        if(!mPin.equals(currentParkedMarker)) {
            mPin.remove();
            invalidateOptionsMenu();
        }
    }

    public void park(){
        isParked = true;
        parkedLocation = getCurrentLocation();
        myLocation = new Locations(db, parkedLocation, getStreetName(parkedLocation));
        timeParked = new Date();
        animateCamera(parkedLocation);
        saveParkedLocation();
        invalidateOptionsMenu();
        drawParkedCar(parkedLocation);
        overlay.setVisibility(View.VISIBLE);
    }

    /**
     * This method accpets 2 LatLng objects as arguments, and a Layer type, and layer weight.
     * The Layer type decides which type of overlay will be displayed, for example, price, or parking availability.
     * layer weight will select a color based on a low to high selector.
     */

    void drawOverlays(LatLng start, LatLng stop, OverlayType layerType, OverlayWeight layerWeight ){
        int drawColor;
        int drawWidth;

        switch (layerWeight) {
            case HIGH:
                drawColor = Color.RED;
                drawWidth = 12;
                break;

            case MEDIUM:
                drawColor = Color.YELLOW;
                drawWidth = 11;
                break;

            default:
                drawColor = Color.GREEN;
                drawWidth = 10;
                break;
        }

        PolylineOptions rectOptions = new PolylineOptions()
                .add(start)
                .add(stop)
                .width(drawWidth)
                .color(drawColor);

        Polyline polyline = mMap.addPolyline(rectOptions);
    }

    public void showAbout(){
        Intent i = new Intent(this, About.class);
        startActivity(i);
    }

    //THESE METHODS ARE FOR DATABASE STUFF!!!!!!!!!
    void saveParkedLocation()
    {
        // Parking car table incrementing was disabled so we don't need the
        // update method for the main table anymore
        myLocation.saveInDb();
        myHistory.saveLocationInHistory(myLocation);



    }

    LatLng loadParkedLocation(){
        // May change, depending in how is being called (sure LOC is not null or otherwise)
        // returns the last parking location.
        // it returns null the first time the app runs
        // because there is not parking location saved yet
        //if (db.getProfilesCount() != 0)
           Log.d("DBTESTINSIDE", new Locations(db).getLocationFromDb().getCoordinates().toString());
           return new Locations(db).getLocationFromDb().getCoordinates();
        //return null;
    }

    //ERASE LAST PARKED LOCATION FROM DATABASE
    void clearParkedLocation()
    {
        myLocation.clearFromDb();
    }


    void unPark(){
        parkedLocation = null;
        isParked = false;
        invalidateOptionsMenu();
        clearParkedLocation();
        timeParked = null;
        overlay.setVisibility(View.GONE);
        currentParkedMarker.remove();
        //navigate(myLocation.getCoordinates(), "driving");

        //This method should clear the current parked location in database and any current parked variables
    }

    void navigate(LatLng end, String navigationType){
        isNavigating = true;
        navigationOverlay.setVisibility(View.VISIBLE);
        LatLng start = getCurrentLocation();
        gd = new GoogleDirection(this);
        gd.setOnDirectionResponseListener(new GoogleDirection.OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();

                gd.animateDirection(mMap, gd.getDirection(doc), GoogleDirection.SPEED_NORMAL
                        , true, true, true, false, null, false, true, new PolylineOptions().width(7));
            }
        });
        gd.setLogging(true);
        if(navigationType.equals("driving")){
            gd.request(start, end, GoogleDirection.MODE_DRIVING);
        }else{
            gd.request(start, end, GoogleDirection.MODE_BICYCLING);
        }





    }

    void clearNavigation(){
        isNavigating = false;
        navigationOverlay.setVisibility(View.GONE);
        gd.cancelNavigation();
    }




    void loadParkingInfo(){
        String tempCoordinates;
        String[] coordinates;

        AsyncTask taskOn = new HTTP_request(this).execute(urlOn);
        AsyncTask taskOff = new HTTP_request(this).execute(urlOff);

        try {
            resultOn = taskOn.get().toString();
            resultOff = taskOff.get().toString();
            Log.d("Hamoon",resultOff);

        } catch (Exception e) { e.printStackTrace(); }
        try {
            jObjectOn = new JSONObject(resultOn);
            jObjectOff = new JSONObject(resultOff);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jArrayOn = jObjectOn.getJSONArray("AVL");
            jArrayOff = jObjectOff.getJSONArray("AVL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0; i < jArrayOn.length(); i++)
        {
            try {
                JSONObject oneObject = jArrayOn.getJSONObject(i);
                // Pulling items from the array
                tempCoordinates = oneObject.getString("LOC");
                coordinates = tempCoordinates.split(",");
                LatLng startCoordinate = new LatLng(Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[0]));
                LatLng endCoordinate = new LatLng(Double.parseDouble(coordinates[3]), Double.parseDouble(coordinates[2]));
                drawOverlays(startCoordinate, endCoordinate, OverlayType.PRICE, OverlayWeight.LOW);

            } catch (JSONException e) {
                // Oops
            }
        }

        for (int i=0; i < jArrayOff.length(); i++)
        {
            try {
                JSONObject oneObject = jArrayOff.getJSONObject(i);
                // Pulling items from the array
                String temp = oneObject.getString("LOC");
                String name = oneObject.getString("NAME");
                String address = oneObject.getString("DESC");
                String occ = oneObject.getString("OCC");
                String oper = oneObject.getString("OPER");
                String[] garageLoc = temp.split(",");
                LatLng garage = new LatLng(Double.parseDouble(garageLoc[1]), Double.parseDouble(garageLoc[0]));

                int parkingAvailable = Integer.parseInt(oper) - Integer.parseInt(occ);

                String gTitle = name + " - " + address + " - Available Parking :" + parkingAvailable;

                currentParkedMarker = mMap.addMarker(new MarkerOptions()
                        .position(garage)
                        .draggable(false)
                        .title(gTitle)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.garagesmall)));

                currentParkedMarker.showInfoWindow();



                //mMap.addMarker(new MarkerOptions().position(garage).title(name + " - " + address).draggable(false).flat(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            } catch (JSONException e) {
                // Oops
            }
        }
    }

    /**
     *
     * @param lat
     * @return
     */

    String getStreetName(LatLng lat) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String result = "";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(lat.latitude, lat.longitude, 1);
            if(null!=listAddresses&&listAddresses.size()>0){
                result = listAddresses.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
