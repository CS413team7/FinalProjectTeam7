package com.group_seven.csc413.finalprojectrepository;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;
    private String provider, result;
    private Marker mPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

        mPin.remove();
        mPin = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false));

        //} public void download(View view){
        String lat =  String.valueOf(latLng.latitude);
        String lon = String.valueOf(latLng.longitude);

        String url = "http://api.sfpark.org/sfpark/rest/availabilityservice?lat=" + lat + "&long=" + lon + "&radius=0.25&uom=mile&response=XML";

        AsyncTask task = new HTTP_request(this).execute(url);

        try {
            result = task.get().toString();
        } catch (Exception e) { e.printStackTrace(); }

        Log.d("mytag", result);
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(result);
    }
}
