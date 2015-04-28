package com.group_seven.csc413.finalprojectrepository;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private String provider, resultOn, resultOff;
    private Marker mPin; //Single and multiple marker origin
    private Circle markerCircle;
    private boolean pinExists = false;
    private boolean circleExists = false;
    private boolean isParked = false;
    private DBConfig db;
    private JSONObject jObject;
    LatLng lastParkedLocation;
    LatLng parkedLocation;
    Date timeParked;
    Marker currentParkedMarker; //Use to draw car icon

    JSONArray jArray;

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
        setUpMapIfNeeded();
        // gets the database context
        db = ((Global) this.getApplication()).getDatabaseContext();
        lastParkedLocation = loadParkedLocation();
        if (lastParkedLocation != null) {
            park();
        }
        /*
             Uncomment to delete database and rebuild the database at runtime
             Warning: All the data stored before of the rebuild will be lost
             Remember to comment it again, after the database is rebuilt.
         */
        // db.reBuildDatabase(this.getBaseContext(), "appDatabase.db");

        loadParkingInfo();


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true); //Enable Location Button
                mMap.getUiSettings().setZoomControlsEnabled(true); //Enable Zoom Button

                LatLng currentLocation = getCurrentLocation();
                animateCamera(currentLocation);


                mMap.setOnMapLongClickListener(this);
                // setUpap is not being used, i left it there so it may be useful at some point
                //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").draggable(true).flat(true));
            }
        }
    }

    void animateCamera(LatLng currentLocation){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation)
                .zoom(14).build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mPin = mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true));
    }

    LatLng getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        //Get the name of the best provider
        provider = locationManager.getBestProvider(criteria, true);

        //Get current location
        Location myLocation = locationManager.getLastKnownLocation(provider);
        //Avoid error when GPS OFF

        LatLng currentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        if (currentLocation != null)
            return currentLocation;
        else
            return (new LatLng(37.723357, -122.480698)); //it fix the problem of null location but we may have to change coordinates
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(){
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if(pinExists){
            mPin.remove();
        } else {
            pinExists = true;
        }

        if(circleExists){
            markerCircle.remove();
        } else {
            circleExists = true;
        }

        mPin = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false));

        CircleOptions markerRadius = new CircleOptions().center(latLng).radius(402.336).strokeWidth(5);
        markerCircle = mMap.addCircle(markerRadius);


        //} public void download(View view){
        String lat =  String.valueOf(latLng.latitude);
        String lon = String.valueOf(latLng.longitude);
    }


    public void drawParkedCar() {


        currentParkedMarker = mMap.addMarker(new MarkerOptions()
                .position(parkedLocation)
                .draggable(false)
                .title("Here's my Car!")
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_little_car))
        );

        currentParkedMarker.showInfoWindow();
    }

    void removeParkedCar(){
        currentParkedMarker.remove();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(isParked){
            menu.findItem(R.id.cancel_button).setEnabled(true);
            menu.findItem(R.id.cancel_button).setVisible(true);
            menu.findItem(R.id.park_button).setTitle("Return");
        }else{
            menu.findItem(R.id.cancel_button).setEnabled(false);
            menu.findItem(R.id.cancel_button).setVisible(false);
            menu.findItem(R.id.park_button).setTitle("Park");
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.park_button:
                if(isParked == true){
                    unPark();
                }else{
                    park();
                }
                return true;
            case R.id.cancel_button:
                cancel();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void park(){
        isParked = true;
        parkedLocation = getCurrentLocation();
        timeParked = new Date();
        animateCamera(parkedLocation);
        invalidateOptionsMenu();
        drawParkedCar();


    }

    //THESE METHODS ARE FOR DATABASE STUFF!!!!!!!!
    void saveParkedLocation(){
        // also save timeParked;
        if (db.getProfilesCount() == 0)
            db.saveParkingCoordinates("My Current Parked Location", parkedLocation);
        else
            db.updateParkingCoordinates("My Current Parked Location", parkedLocation);
    }
    void addLocationToFavorites (LatLng location)
    {
        // working on this method
        // db.putInFavorites(location)
    }
    LatLng loadParkedLocation(){
        // returns the last parking location.
        // it returns null the first time the app runs
        // because there is not parking location saved yet
        if (db.getProfilesCount() > 0)
           return db.getParkingCoordinates("My Current Parked Location");
        return null;
    }

    //ERASE LAST PARKED LOCATION FROM DATABASE
    void clearParkedLocation(){

        parkedLocation = null;

    }


    void cancel(){
        isParked = false;
        invalidateOptionsMenu();
        clearParkedLocation();
        timeParked = null;
        removeParkedCar();

        //This method should clear the current parked location in database and any current parked variables
    }

    public void unPark(){
        //This method will navigate you back to your car, cancel() on the other hand will just reset the location
        cancel();
        // put all code for returning to car here!!!

    }

    void loadParkingInfo(){
        String tempCoordinates;
        String[] coordinates;
        AsyncTask taskOn = new HTTP_request(this).execute(urlOn);
        AsyncTask taskOff = new HTTP_request(this).execute(urlOff);

        try {
            resultOn = taskOn.get().toString();
            resultOff = taskOff.get().toString();

        } catch (Exception e) { e.printStackTrace(); }
        try {
            jObject = new JSONObject(resultOn);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Hamoon", resultOff);

        try {
            jArray = jObject.getJSONArray("AVL");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=0; i < jArray.length(); i++)
        {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
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


    }







}
