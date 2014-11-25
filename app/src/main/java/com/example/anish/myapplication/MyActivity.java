package com.example.anish.myapplication;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.util.Date;


public class MyActivity extends Activity implements
        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    Double prevLat = 0.0, prevLng = 0.0, lat=0.0, lng=0.0;
    Float accuracy=0f, speed=0f, lastSpeed=0f, minSpeedInterval=5f;

    int userId=1, num=0, activateFlag=0;
    long curTime=0, prevTime=0, lastCheckedInterval=15000;

    LocationClient mLocationClient;
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 2;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    LocationRequest mLocationRequest;
    public GoogleMap mMap;
    public Marker myPrevMarker, OSVPrevMarker[];
    public MediaPlayer mp;
    public boolean alarmAvailability = true, activated = false;
    public void removeWarning(){
        View warn = findViewById(R.id.alertLayer);
        warn.setVisibility(View.INVISIBLE);
    }

    public void displayWarning(){
        View warn = findViewById(R.id.alertLayer);
        warn.setBackgroundColor(Color.parseColor("#F0B2B2"));
        warn.setAlpha(0.4f);
        warn.setVisibility(View.VISIBLE);
    }

    public void turnOnAlarm(){
        if(alarmAvailability==true) {
            mp.start();
        }
    }
    public void turnOffAlarm(){
        mp.pause();

    }
    public void disableAlarm(View view){
        Button button = (Button )view;
        String text = button.getText().toString();
        if(text=="Disable Alarm"){
            button.setText("Enable Alarm");
            alarmAvailability = false;
        }
        else {
            alarmAvailability = true;
            button.setText("Disable Alarm");
        }
    }

    public void notifyUser(String OSV){
        if(OSVPrevMarker!=null) {
            for (int i = 0; i < OSVPrevMarker.length; i++) {
                if (OSVPrevMarker[i] != null && OSVPrevMarker[i].isVisible())
                    OSVPrevMarker[i].remove();
            }
        }
        if(activated==true) {
            if (OSV.length() != 0 && OSV != "noOSV") {
                displayWarning();
                if (!mp.isPlaying()){
                    turnOnAlarm();
                }
                Button button = (Button) findViewById(R.id.alarm);
                button.setVisibility(View.VISIBLE);
                String[] responses = OSV.split(";");
                int green = Color.parseColor("#5659d620"), red = Color.parseColor("#7fff0010");
                int color = red;
                OSV = "Overspeeding Vehicles\n\n";
                int OSVcount = 0;
                for (int i = 0; i < OSV.length(); i++) {
                    if (OSV.charAt(i) == ';') {
                        OSVcount++;
                    }
                }
                OSVPrevMarker = new Marker[OSVcount + 1];
                int count = 0;
//            for(int i=0; i<count; i++){
//                if(OSVPrevMarker[i]!=null&&OSVPrevMarker[i].isVisible())
//                    OSVPrevMarker[i].remove();
//            }
                for (String str : responses) {
                    String[] arr = str.split(",");
                    int id = Integer.parseInt(arr[0]);
                    Double OSVspeed = Double.parseDouble(arr[1]), OSVlat = Double.parseDouble(arr[2]), OSVlng = Double.parseDouble(arr[3]);
                    mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

                    if (OSVlat != null && OSVlng != null && OSVspeed != null)
                        OSVPrevMarker[count] = mMap.addMarker(new MarkerOptions().position(new LatLng(OSVlat, OSVlng)).title("Speed: " + OSVspeed.toString()).icon(BitmapDescriptorFactory.fromResource(R.drawable.v4)));
                    count++;
                }

                final int newColor = color;

            } else {
                removeWarning();
                if (mp.isPlaying()) {
                    turnOffAlarm();
                }
                Button button = (Button) findViewById(R.id.alarm);
                button.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void updateMyLocationonMap(LatLng mylocation){
        if(myPrevMarker!=null&&myPrevMarker.isVisible())
            myPrevMarker.remove();
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        myPrevMarker = mMap.addMarker(new MarkerOptions().position(mylocation).title("My vehicle").icon(BitmapDescriptorFactory.fromResource(R.drawable.myvehicle)));
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(mylocation, 15.0f) );
    }

    private void checkOverspeed() {
        new checkOverspeed().execute(this);
    }

    private void checkNotify() {
        final MyActivity obj = this ;
        Thread t = new Thread(){
            public void run() {
                Looper.prepare();
                ThreatChecker checker = new ThreatChecker(obj);
                checker.startChecking() ;
                Looper.loop();
            }
        };
        t.start() ;
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();
        accuracy = location.getAccuracy();
        speed = location.getSpeed();
        curTime = new Date().getTime();

        updateMyLocationonMap(new LatLng(lat, lng));
        if(prevTime==0)
            prevTime = curTime ;

        if( (speed>40&&(speed>=lastSpeed+minSpeedInterval||speed<=lastSpeed-minSpeedInterval)) || (curTime-prevTime>lastCheckedInterval) ){
            lastSpeed = speed;
            prevTime = curTime;
            checkOverspeed();
        }
        String latStr = "Latitude : "+lat.toString()+"\n";
        String lngStr = "Longitude: "+lng.toString()+"\n";
        String speedStr = "Speed: "+speed.toString()+"\n";

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        updateUpperBox("");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationClient = new LocationClient(this, this, this);
        mp = MediaPlayer.create(getApplicationContext(), R.raw.alert);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
//        mLocationClient.connect();
    }
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
//        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationClient.requestLocationUpdates(mLocationRequest, this);
        checkNotify();

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public UserLoc getUserLoc() {
        return new UserLoc(userId, lat, lng, speed) ;
    }

    public void setMinSpeedInterval(Float speed){
        minSpeedInterval = speed;
    }

    public void updateUpperBox(String str) {
        TextView upperBox = (TextView)findViewById(R.id.upperBox);
        upperBox.setTextSize(20);
        upperBox.setText(str);
    }

    public void updateLowerBox(String str, int color) {
//        TextView lowerBox = (TextView)findViewById(R.id.lowerBox);
//        lowerBox.setTextSize(20);
//        lowerBox.setText(str);
//        lowerBox.setBackgroundColor(color);
    }
    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
//            turnOnAlarm();
            activated = true;
            mLocationClient.connect();
//            mLocationClient.requestLocationUpdates(mLocationRequest, this);
//            checkNotify();
        }
        else {
            activated = false;
            Button button = (Button) findViewById(R.id.alarm);
            button.setVisibility(View.INVISIBLE);
            updateUpperBox("Activate to start");
            if(mp.isPlaying())
                turnOffAlarm();
            View warn = findViewById(R.id.alertLayer);
            warn.setVisibility(View.INVISIBLE);
            mLocationClient.disconnect();
            prevLat = 0.0; prevLng = 0.0; lat=0.0; lng=0.0;
        }
    }
}
