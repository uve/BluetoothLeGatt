package com.example.android.bluetoothlegatt;

//import android.content.pm.PackageManager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

//import android.os.Handler;


public class DeviceSensor implements SensorEventListener {


    private static final String TAG = "Sensor";

    private Context mContext;

    private final SensorManager mSensorManager;
    //private final Sensor mAccelerometer;

    private float[] aValues = new float[3];
    private float[] mValues = new float[3];

    //private SensorManager sensorManager;
    private int rotation;


    private WirelessService mWirelessService;


    private static final int SENSOR_DELAY = 50*1000*1000; //SensorManager.SENSOR_DELAY_NORMAL

    public DeviceSensor(Context mContext){

        mWirelessService = new WirelessService(mContext);

        this.mContext = mContext;


        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);


        //mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  SENSOR_DELAY);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SENSOR_DELAY);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),       SensorManager.SENSOR_DELAY_NORMAL);


        /*
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
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        */
    }


    protected void onResume() {
        //super.onResume();
        //mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        //super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


    public void onSensorChanged(SensorEvent event) {


        /*
        // If the sensor data is unreliable return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            //Toast.makeText(main.this, "Sensor Status Unreliable",Toast.LENGTH_SHORT).show();
            return;
        }
        */


        // Gets the value of the sensor that has been changed
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                aValues = event.values;
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mValues = event.values;

                float[] values = calculateOrientation();
                updatePosition(values);

                break;

            case Sensor.TYPE_PRESSURE:

                float  currentPressure  =  event.values[0];
                //  Вычислите  высоту  над  уровнем  моря.
                float  altitude  =  SensorManager.getAltitude(
                        SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                        currentPressure);

                updateHeight(currentPressure, altitude);

                break;

            default:
                break;

        }


    }


    private float[] calculateOrientation() {
        float[] values = new float[3];
        float[] inR = new float[9];
        float[] outR = new float[9];

        // Determine the rotation matrix
        mSensorManager.getRotationMatrix(inR, null, aValues, mValues);

        // Remap the coordinates based on the natural device orientation.
        int x_axis = mSensorManager.AXIS_X;
        int y_axis = mSensorManager.AXIS_Y;

        mSensorManager.remapCoordinateSystem(inR, x_axis, y_axis, outR);

        // Obtain the current, corrected orientation.
        mSensorManager.getOrientation(outR, values);

        // Convert from Radians to Degrees.
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        return values;
    }


    /**
     * This method modify the last know good location according to the arguments.
     *
     * @param location The possible new location.
     */
    void updatePosition(float[] values) {
        //Log.d(TAG, "location = " + location.toString());

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        nameValuePairs.add(new BasicNameValuePair("azimut", Float.toString(values[0]) ));
        nameValuePairs.add(new BasicNameValuePair("pitch",  Float.toString(values[1]) ));
        nameValuePairs.add(new BasicNameValuePair("roll",   Float.toString(values[2]) ));

        //Log.d(TAG, nameValuePairs.toString());

        mWirelessService.Send(mWirelessService.URL_POSITION, nameValuePairs);
    }


    void updateHeight(float pressure, float altitude) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

        nameValuePairs.add(new BasicNameValuePair("pressure", Float.toString(pressure) ));
        nameValuePairs.add(new BasicNameValuePair("altitude", Float.toString(altitude) ));

        mWirelessService.Send(mWirelessService.URL_HEIGHT, nameValuePairs);
    }

}
