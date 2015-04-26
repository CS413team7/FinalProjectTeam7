package com.group_seven.csc413.finalprojectrepository;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapsActivity extends ActionBarActivity implements GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private String provider, result;
    private Marker mPin;
    private Circle markerCircle;
    private boolean pinExists = false;
    private boolean circleExists = false;
    private boolean isParked = false;
    private DBConfig db;

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
        LatLng testStart = new LatLng(37.774933, -122.433823);
        LatLng testEnd = new LatLng(37.756933, -122.433823);
        drawOverlays(testStart, testEnd, OverlayType.PRICE, OverlayWeight.HIGH);
        park();

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

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                //Create a criteria object to retrieve provider
                Criteria criteria = new Criteria();

                //Get the name of the best provider
                provider = locationManager.getBestProvider(criteria, true);

                //Get current location
                Location myLocation = locationManager.getLastKnownLocation(provider);
                //Avoid error when GPS OFF
                if(myLocation != null) {
                LatLng currentLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLocation)
                        .zoom(14).build();

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    mPin = mMap.addMarker(new MarkerOptions().position(currentLocation).draggable(true));
                }

                mMap.setOnMapLongClickListener(this);
                // setUpap is not being used, i left it there so it may be useful at some point
                //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").draggable(true).flat(true));
            }
        }
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

        String url = "http://api.sfpark.org/sfpark/rest/availabilityservice?lat=" + lat + "&long=" + lon + "&radius=0.25&uom=mile&response=XML";

        AsyncTask task = new HTTP_request(this).execute(url);

        try {
            result = task.get().toString();
        } catch (Exception e) { e.printStackTrace(); }

        Log.d("mytag", result);
        //TextView t = (TextView) findViewById(R.id.textView);
        //t.setText(result);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void park(){
        isParked = true;
         // put all code for parking here!!!
         /*
             Uncomment the lines below, and put the correct context and
             coordinates to save parking coordinates in database.
             Note: context parameter can be any any string you want like
             for example myParkingSite. Context parameter is used as a
             index to update the database if needed
         */

        // db.saveParkingCoordinates("myContext", new LatLng(37.774933, -122.433823));
        // LatLng latlng = db.getParkingCoordinates("myContext'");
        // db.updateParkingCoordinates("myContext", new LatLng(90.774, -180.433823))



    }

    public void unPark(){
        isParked = false;
        // put all code for returning to car here!!!

    }


}
