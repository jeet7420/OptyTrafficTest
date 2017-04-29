package com.optytraffictest;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.optytraffictestDA.MainActivityDA;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

//import Modules.DirectionFinder;
//import Modules.DirectionFinderListener;
//import Modules.Route;
/**
 * Created by smarhas on 2/20/2017.
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private TextView tv_time;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    public String origin;
    public String destination;
    public String endlat;
    public String endlong;
    public String suggested_time;
    public String expected_time_of_journey;
    public String start_time;
    private Context context;
    String hour, min;
    String time[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        this.context = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tv_time = (TextView) findViewById(R.id.tv_time);

        //btnFindPath = (Button) findViewById(R.id.btnFindPath);
        //etOrigin = (EditText) findViewById(R.id.etOrigin);
        //etDestination = (EditText) findViewById(R.id.etDestination);

        Intent i = getIntent();
        origin = i.getStringExtra("p_source");
        destination = i.getStringExtra("p_destination");
        suggested_time = i.getStringExtra("p_suggested_time");
        expected_time_of_journey = i.getStringExtra("p_expected_time_of_journey");
        endlat = i.getStringExtra("p_endlat");
        endlong = i.getStringExtra("p_endlong");
        start_time = i.getStringExtra("p_start_time");

        time = start_time.split(":");
        hour = time[0];
        min = time[1];
        if(hour.length()==1)
            hour = "0" + hour;
        if(min.length()==1)
            min = "0" + min;

        System.out.println("HOUR -> " + hour);
        System.out.println("MIN -> " + min);

        System.out.println("INSIDE MAPS ACTIVITY");
        System.out.println(origin);
        System.out.println(destination);
        System.out.println(suggested_time);
        System.out.println(expected_time_of_journey);
        System.out.println(start_time);
        tv_time.setText(suggested_time);
        sendRequest();
        alarmManager();
    }

    private void alarmManager() {
        Calendar cur_cal = new GregorianCalendar();
        cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
        //System.out.println("TIME : " + Integer.parseInt(suggested_time.substring(0,2)));
        //System.out.println("TIME : " + Integer.parseInt(suggested_time.substring(3,5)));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        cal.set(Calendar.MINUTE, Integer.parseInt(min));
        //cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
        //cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
        //cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
        //cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
        Intent triggerDAModule = new Intent(this.context, AlarmReceiver.class);
        triggerDAModule.putExtra("p_endlat", endlat);
        triggerDAModule.putExtra("p_endlong", endlong);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, triggerDAModule, 0);
        AlarmManager alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);


        /*Calendar calender = Calendar.getInstance();

        String sDate = calender.get(Calendar.YEAR) + "-"
                + calender.get(Calendar.MONTH)
                + "-" + calender.get(Calendar.DAY_OF_MONTH)
                + " at " + calender.get(Calendar.HOUR_OF_DAY)
                + ":" + calender.get(Calendar.MINUTE);

        calender.set(Calendar.SECOND, Calendar.SECOND);
        calender.set(Calendar.MINUTE, Calendar.MINUTE);
        calender.set(Calendar.HOUR, Calendar.HOUR_OF_DAY);
        //calender.set(Calendar.AM_PM, Calendar.AM);

        System.out.println("JEETTIME : " + String.valueOf(calender.getTime()));

        System.out.println("JEETTIME : " + sDate);*/

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }


    private void sendRequest() {
        //origin = etOrigin.getText().toString();
        //destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng hcmus = new LatLng(10.762963, 106.682394);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 18));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 5.0f ) );
        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title("Ä�áº¡i há»�c Khoa há»�c tá»± nhiÃªn")
                .position(hcmus)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 12));
            ((TextView) findViewById(R.id.tvDuration)).setText(expected_time_of_journey);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}

