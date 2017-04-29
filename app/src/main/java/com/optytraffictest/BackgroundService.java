package com.optytraffictest;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.optytraffictestDA.*;
import com.optytraffictestDA.GPSTracker;
import com.optytraffictestDA.HTTPURLConnection;
import com.google.android.gms.location.Geofence;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Intent.getIntent;

/**
 * Created by smarhas on 3/27/2017.
 */
public class BackgroundService extends Service {

    private Context context;
    private Thread backgroundThread;
    GPSTracker gps;
    private List<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private GoogleApiClient mGoogleApiClient;
    PendingIntent mGeofencePendingIntent;
    LocationRequest mLocationRequest;
    double currentLatitude = 8.5565795, currentLongitude = 76.8810227;
    Boolean locationFound;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    public static final String TAG = "Activity";
    String destLat, destLong;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        System.out.println("BACKGROUND THREAD ONCREATE");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("ARRAYLISTDATA");
        for(int i=0; i<StaticValues.listOfMarkerWithTime.size(); i++){
            System.out.println(StaticValues.listOfMarkerWithTime.get(i));
        }
        ServerLog.logHashMap.put(String.valueOf(ServerLog.key), "DA Module Stopped");
        ServerLog.key++;
        ServerLog.postMessage();
        //LocalLog.postMessageToLocal();
        Toast.makeText(getApplicationContext(), "Logs Posted to Server", Toast.LENGTH_SHORT).show();
        System.out.println("BACKGROUND THREAD STOPPED");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("check1");
        ServerLog.logHashMap.put(String.valueOf(ServerLog.key), "DA Module Triggered");
        Toast.makeText(getApplicationContext(), "DA Module Triggered", Toast.LENGTH_SHORT).show();
        ServerLog.key++;
        destLat = intent.getStringExtra("p_endlat");
        destLong = intent.getStringExtra("p_endlong");
        System.out.println("Inside BackgroundService onStartCommand : " + "Destination Latitude -> " + destLat + " Destination Longitude -> " + destLong);
        gps = new com.optytraffictestDA.GPSTracker(context, destLat, destLong);
        System.out.println("check2");
        if(gps.canGetLocation()){
            System.out.println("check3");
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            System.out.println("check4");
            System.out.println("Your Current Location is - \nLat: "
                    + latitude + "\nLong: " + longitude);
            DecimalFormat f = new DecimalFormat("#.000");
            StaticValues.sourceLat = f.format(latitude);
            StaticValues.sourceLong = f.format(longitude);
            System.out.println("MATCHLOCATION : " + StaticValues.sourceLat);
            System.out.println("MATCHLOCATION : " + StaticValues.sourceLong);
            ServerLog.logHashMap.put(String.valueOf(ServerLog.key),
                    "First Location : " + "Latitude -> " + String.valueOf(latitude)
                    + " Longitude -> " + String.valueOf(longitude));
            ServerLog.key++;
        }else{
            System.out.println("check5");
            gps.showSettingsAlert();
        }
        System.out.println("check6");
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            System.out.println("check7");
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        System.out.println("check8");
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
