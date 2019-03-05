package com.example.phoenix.securityappforgirls03;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class FetchLocation extends AsyncTask {

    public static final String TAG = "PiyushTag";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    LocationManager locationManager;
    static Location myLocation;
    Context cntx;
    int request_code;
    Location location;

    public FetchLocation(Context context, int rquest_c) {
        this.cntx = context;
        this.request_code = rquest_c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected Void doInBackground(Object... objects) {
        switch (request_code) {
            case 12:
                location = null;
                Log.i(TAG, "doInBackground START with request_code : 12");

                //building Location Request
                locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(5000);
                locationRequest.setFastestInterval(3000);
                locationRequest.setSmallestDisplacement(10);

                //Building fusedLocationProvider
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(cntx);

                //Location Callback
                locationCallback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location l : locationResult.getLocations()) {
                            if (l == null) {
                                continue;
                            } else {
                                Log.i(TAG, "Starting getting Locations");
                                //Log.i(TAG, "Location : " + l.getLatitude() + "," + l.getLongitude());
                                new MainActivity().got_loc_Toast(l);
                                fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                            }
                        }
                    }
                };

                if (ActivityCompat.checkSelfPermission(cntx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(cntx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                Looper looper = Looper.getMainLooper();
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, looper);

                break;
        }


        return null;
    }

}
