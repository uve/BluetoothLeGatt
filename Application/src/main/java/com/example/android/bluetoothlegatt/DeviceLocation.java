package com.example.android.bluetoothlegatt;

//import android.content.pm.PackageManager;
import android.os.Bundle;
//import android.os.Handler;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.content.Context;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class DeviceLocation {


    private static final String TAG = "DeviceLocation";

    private static final Integer NETWORK_DELAY = 20000;

    private static final Integer GPS_DELAY = 10000;


    private Context mContext;


    private WirelessService mWirelessService;


    public DeviceLocation(Context mContext){

        mWirelessService = new WirelessService(mContext);

        this.mContext = mContext;

        //Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, NETWORK_DELAY, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_DELAY, 0, locationListener);
    }

    /**
     * This method modify the last know good location according to the arguments.
     *
     * @param location The possible new location.
     */
    void makeUseOfNewLocation(Location location) {
        //Log.d(TAG, "location = " + location.toString());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        nameValuePairs.add(new BasicNameValuePair("latitude", Double.toString(location.getLatitude()) ));
        nameValuePairs.add(new BasicNameValuePair("longitude", Double.toString(location.getLongitude()) ));

        nameValuePairs.add(new BasicNameValuePair("altitude", Double.toString(location.getAltitude()) ));


        nameValuePairs.add(new BasicNameValuePair("time", Long.toString(location.getTime()) ));

        nameValuePairs.add(new BasicNameValuePair("accuracy", Float.toString(location.getAccuracy()) ));

        //Log.d(TAG, nameValuePairs.toString());

        mWirelessService.Send(mWirelessService.URL_LOCATION, nameValuePairs);
    }


}
