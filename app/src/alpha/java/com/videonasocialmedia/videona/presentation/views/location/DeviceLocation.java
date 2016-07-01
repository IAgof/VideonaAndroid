/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.views.location;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by alvaro on 27/06/16.
 */
public class DeviceLocation {

    private static final String TAG = "DeviceLocation";
    private static final float ACCURATE_LOCATION_THRESHOLD_METERS = 100;
    private final int DELAY_GET_BEST_LOCATION_MS = 20000;
    Timer timer1;
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled = false;
    boolean network_enabled = false;
    private Location bestLocation;
    private boolean waitForGpsFix;

    private SharedPreferences sharedPreferences  = VideonaApplication.getAppContext().getSharedPreferences(
            ConfigPreferences.SETTINGS_SHARED_PREFERENCES_FILE_NAME,
            Context.MODE_PRIVATE);


    public static void getLocation(Context context, boolean waitForGpsFix, final LocationResult callback) {
        DeviceLocation deviceLocation = new DeviceLocation();
        deviceLocation.getLocation(context, callback, waitForGpsFix);
    }

    /**
     * Get the last known location.
     * If one is not available, fetch
     * a fresh location
     *
     * @param context
     * @param waitForGpsFix
     * @param callback
     */
    public static void getLastKnownLocation(Context context, boolean waitForGpsFix, final LocationResult callback) {
        DeviceLocation deviceLocation = new DeviceLocation();

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location last_loc;
        if (ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        last_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (last_loc == null)
            last_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (last_loc != null && callback != null) {
            callback.gotLocationLatLng(last_loc.getLatitude(), last_loc.getLongitude());
            deviceLocation.saveLocationInPreferences(last_loc.getLatitude(), last_loc.getLongitude());
        } else {
            deviceLocation.getLocation(context, callback, waitForGpsFix);
        }
    }

    public boolean getLocation(Context context, LocationResult result, boolean waitForGpsFix) {
        this.waitForGpsFix = waitForGpsFix;

        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult = result;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        //don't start listeners if no provider is enabled, get data from preferences
        if (!gps_enabled && !network_enabled) {
            double latitude = Double.parseDouble(sharedPreferences.getString(ConfigPreferences.LOCATION_LATITUDE,  "0.0"));
            double longitude = Double.parseDouble(sharedPreferences.getString(ConfigPreferences.LOCATION_LONGITUDE, "0.0"));
            locationResult.gotLocationLatLng(latitude, longitude);
            return false;
        }

        if (gps_enabled)
            if (ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                    Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return false;
            }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        if (network_enabled)
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        timer1 = new Timer();
        timer1.schedule(new GetBestLocation(), DELAY_GET_BEST_LOCATION_MS);
        return true;
    }

    public static abstract class LocationResult {
        public abstract void gotLocationLatLng(double latitude, double longitude);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.i(TAG, "got GPS loc accurate to " + String.valueOf(location.getAccuracy()) + "m");
            if (bestLocation == null || bestLocation.getAccuracy() > location.getAccuracy())
                bestLocation = location;

            if (!waitForGpsFix || bestLocation.getAccuracy() < ACCURATE_LOCATION_THRESHOLD_METERS) {
                timer1.cancel();
                locationResult.gotLocationLatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
                saveLocationInPreferences(bestLocation.getLatitude(), bestLocation.getLongitude());
                if (ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerNetwork);
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            Log.i(TAG, "got network loc accurate to " + String.valueOf(location.getAccuracy()) + "m");
            if (bestLocation == null || bestLocation.getAccuracy() > location.getAccuracy())
                bestLocation = location;

            if (!waitForGpsFix || bestLocation.getAccuracy() < ACCURATE_LOCATION_THRESHOLD_METERS) {
                timer1.cancel();
                locationResult.gotLocationLatLng(bestLocation.getLatitude(),bestLocation.getLongitude());
                saveLocationInPreferences(bestLocation.getLatitude(), bestLocation.getLongitude());
                if (ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                        Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerGps);
            }

        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    class GetBestLocation extends TimerTask {
        @Override
        public void run() {
            Log.i(TAG, "Timer expired before adequate location acquired");
            if (ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(VideonaApplication.getAppContext(),
                    Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            lm.removeUpdates(locationListenerGps);
            lm.removeUpdates(locationListenerNetwork);

            Location net_loc = null, gps_loc = null;
            if (gps_enabled)
                gps_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (network_enabled)
                net_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //if there are both values use the latest one
            if (gps_loc != null && net_loc != null) {
                if (gps_loc.getTime() > net_loc.getTime()) {
                    locationResult.gotLocationLatLng(gps_loc.getLatitude(), gps_loc.getLongitude());
                    saveLocationInPreferences(gps_loc.getLatitude(), gps_loc.getLongitude());
                }else {
                    locationResult.gotLocationLatLng(net_loc.getLatitude(), net_loc.getLongitude());
                    saveLocationInPreferences(net_loc.getLatitude(), net_loc.getLongitude());
                }
                return;
            }

            if (gps_loc != null) {
                locationResult.gotLocationLatLng(gps_loc.getLatitude(), gps_loc.getLongitude());
                saveLocationInPreferences(gps_loc.getLatitude(), gps_loc.getLongitude());
                return;
            }
            if (net_loc != null) {
                locationResult.gotLocationLatLng(net_loc.getLatitude(), net_loc.getLongitude());
                saveLocationInPreferences(net_loc.getLatitude(), net_loc.getLongitude());
                return;
            }
            double latitude = Double.parseDouble(sharedPreferences.getString(ConfigPreferences.LOCATION_LATITUDE,  "0.0"));
            double longitude = Double.parseDouble(sharedPreferences.getString(ConfigPreferences.LOCATION_LONGITUDE, "0.0"));
            locationResult.gotLocationLatLng(latitude, longitude);
        }
    }

    // TODO:(alvaro.martinez) 1/07/16 Save data position with realm, persistence. Shared preferences not goog, slow.
    public void saveLocationInPreferences(double latitude, double longitude){

        if(latitude == 0.0 && longitude == 0.0)
            return;

        SharedPreferences.Editor editor = sharedPreferences.edit();;
        editor.putString(ConfigPreferences.LOCATION_LATITUDE,String.valueOf(latitude));
        editor.putString(ConfigPreferences.LOCATION_LONGITUDE, String.valueOf(longitude));
        editor.commit();
    }
}

