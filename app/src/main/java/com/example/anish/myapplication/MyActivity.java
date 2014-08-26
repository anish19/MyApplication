package com.example.anish.myapplication;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.math.BigDecimal;


public class MyActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
    // Global variable to hold the current location
    Location mCurrentLocation;
    LocationClient mLocationClient;
    Double prevLat = 0.0, prevLng = 0.0;

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    Double lat = 0.0;
    Double lng = 0.0;

    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;
    public void getLatLng(View view){

//        mCurrentLocation = mLocationClient.getLastLocation();

        Double diffLat = lat-prevLat;
        Double diffLng = lng-prevLng;
        prevLat = lat;
        prevLng = lng;

        String latStr = lat.toString();
        String lngStr = lng.toString();
        String diffLatStr = diffLat.toString();
        String diffLngStr = diffLng.toString();

        latStr = "Latitude : "+latStr+"\n";
        lngStr = "Longitude: "+lngStr+"\n";
        diffLatStr = "Latitude Diff: "+diffLatStr+"\n";
        diffLngStr = "Longitude Diff: "+diffLngStr+"\n";

        TextView latlngView = (TextView)findViewById(R.id.textView);
        latlngView.setTextSize(20);
        latlngView.setText(latStr+lngStr+"\n"+"\n"+diffLatStr+diffLngStr);
        UserLoc userInstance = new UserLoc(1,lat,lng);
        new SendData().execute(userInstance);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationClient = new LocationClient(this, this, this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationClient.requestLocationUpdates(mLocationRequest,this);
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        BigDecimal latBD = new BigDecimal(Double.toString(lat));
        latBD = latBD.setScale(15, BigDecimal.ROUND_HALF_UP);
        lat = latBD.doubleValue();
        lng = location.getLongitude();
    }

    //
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
}
