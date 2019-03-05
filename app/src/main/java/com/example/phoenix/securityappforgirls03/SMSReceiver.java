package com.example.phoenix.securityappforgirls03;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class SMSReceiver extends BroadcastReceiver {

    public static final String TAG = "PiyushTag";
    public static int is_Empty=1;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    LocationManager locationManager;
    static Location myLocation;
    static Location location;
    public static AppDataBase appDataBase;
    public static Context cntx;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.cntx = context;
        if(intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)){
            appDataBase = Room.databaseBuilder(context,AppDataBase.class,"contact_db").build();
            new asyncClass(21).execute();
        }




    }
    class asyncClass extends AsyncTask<Void,Void,Void>{
        int request_Code;
        //appDataBase = Room.databaseBuilder(getApplicationContext(),AppDataBase.class,"contact_db").build();
        public asyncClass(int requet_Code) {
            this.request_Code = requet_Code;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (request_Code){
                case 21:
                    if(appDataBase.contactDao().getAllWord().size()==0){
                        //do nothing
                    }else{
                        location = null;
                        Log.i(TAG, "doInBackground START with request_code : 21");

                        //building Location Request
                        locationRequest = new LocationRequest();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(5000);
                        locationRequest.setFastestInterval(3000);
                        locationRequest.setSmallestDisplacement(10);

                        //Building fusedLocationProvider
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(cntx);

                        //Location Callback
                        locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                for (Location l : locationResult.getLocations()) {
                                    if (l == null) {
                                        continue;
                                    } else {
                                        Log.i(TAG, "Starting getting Locations");
                                        location = l;
                                        //Log.i(TAG, "Location : " + l.getLatitude() + "," + l.getLongitude());
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


                        String contact_str = appDataBase.contactDao().getAllWord().get(0).getContact();
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contact_str,null,"https://www.google.com/maps/search/?api=1&query="+location.getLatitude()+","+location.getLongitude(),null,null);
                    }
                    break;
            }



            return null;
        }
    }
}
