package com.optytraffictest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.optytraffictest.R.id.rb_car;
import static com.optytraffictest.UserDetails.dialog_id;
import com.optytraffictestDA.MainActivityDA;
import com.optytraffictestDA.ServerLog;
import com.optytraffictestDA.StaticValues;

public class TripDetails extends AppCompatActivity {

    DatabaseHelper myDb;
    SQLiteDatabase db;
    Button btn1, btn2, btn_submit;
    EditText et1, et2, et3, et4;
    String source_address, destination_address;
    private int FL1 = 1;
    private int FL2 = 2;
    private LatLng source_latlng = null;
    private LatLng destination_latlng = null;
    private double source_latitude, destination_latitude;
    private double source_longitude, destination_longitude;
    static final int dialog_id1 = 0;
    static final int dialog_id2 = 1;
    private HTTPURLConnection service;
    private com.optytraffictestDA.HTTPURLConnection getAllMarkers;
    HashMap<String, String> postDataParams;
    private ProgressDialog pDialog;
    private String path = "http://brings.co.in/OptiTraffic/rest/Calc/CalcRes";
    JSONArray response;
    String source, destination, vehicle, starttime, endtime;
    RadioButton rb_car, rb_bike;
    String T_STARTLAT, T_STARTLONG, T_ENDLAT, T_ENDLONG, T_STARTTIME, T_ENDTIME, T_VEHICLE;
    String location_id = "1";
    String markerId, latitude, longitude, description, locationId;
    String dbMarkerId, dbLatitude, dbLongitude, dbLocationId, dbDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        myDb = new DatabaseHelper(this);
        //just a change
        vehicle = "bike";

        Toolbar myToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Trip Details");

        btn1 = (Button) findViewById(R.id.button1);
        btn2 = (Button) findViewById(R.id.button2);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        et1 = (EditText) findViewById(R.id.et_source);
        et2 = (EditText) findViewById(R.id.et_destination);
        et3 = (EditText) findViewById(R.id.et_start_time);
        et4 = (EditText) findViewById(R.id.et_end_time);
        rb_car = (RadioButton) findViewById(R.id.rb_car);
        rb_bike = (RadioButton) findViewById(R.id.rb_bike);

        SetTime startTime = new SetTime(et3, this);
        SetTime endTime = new SetTime(et4, this);

        source = et1.getText().toString();
        destination = et2.getText().toString();
        if(rb_car.isChecked())
            vehicle = "car";
        if(rb_bike.isChecked())
            vehicle = "bike";

        starttime = et3.getText().toString();
        endtime = et4.getText().toString();



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(TripDetails.this);
                    startActivityForResult(intent, FL1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(TripDetails.this);
                    startActivityForResult(intent, FL2);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        service=new HTTPURLConnection();
        getAllMarkers=new com.optytraffictestDA.HTTPURLConnection();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = myDb.getWritableDatabase();

                db.execSQL("DELETE FROM TRIP");

                db.execSQL("DELETE FROM MARKER");

                System.out.println("JEET : " + et1.getText().toString() + " : " + et2.getText().toString() + " : " + et3.getText().toString() + " : " + et4.getText().toString() + " : " + vehicle);

                ContentValues values = new ContentValues();

                values.put("STARTLAT", source_latitude);
                values.put("STARTLONG", source_longitude);
                values.put("ENDLAT", destination_latitude);
                values.put("ENDLONG", destination_longitude);
                values.put("STARTTIME", et3.getText().toString());
                values.put("ENDTIME", et4.getText().toString());
                values.put("VEHICLE", vehicle);

                db.insert("TRIP", null, values);

                Cursor c = myDb.showData();

                c.moveToFirst();
                do{
                    T_STARTLAT = c.getString(0);
                    T_STARTLONG = c.getString(1);
                    T_ENDLAT = c.getString(2);
                    T_ENDLONG = c.getString(3);
                    T_STARTTIME = c.getString(4);
                    T_ENDTIME = c.getString(5);
                    T_VEHICLE = c.getString(6);
                }while(c.moveToNext());

                System.out.println("Trip Details Data : " + " STARTLAT - " + T_STARTLAT + " STARTLONG - " + T_STARTLONG + " ENDLAT - " + T_ENDLAT +
                        " ENDLONG - " + T_ENDLONG + " STARTTIME - " +  T_STARTTIME + " ENDTIME - " +  T_ENDTIME + " VEHICLE - " + T_VEHICLE);

                //Toast.makeText(getApplicationContext(), "Main Module Data", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), T_STARTLAT, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), T_STARTLONG, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), T_ENDLAT, Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), T_ENDLONG, Toast.LENGTH_SHORT).show();


                new MyAsyncTask().execute();
            }
        });

        /*et3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TripDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et3.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        et4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(TripDetails.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        et4.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });*/
    }

    class SetTime implements View.OnFocusChangeListener, TimePickerDialog.OnTimeSetListener {

        private EditText editText;
        private Calendar myCalendar;
        private Context ctx;

        public SetTime(EditText editText, Context ctx){
            this.ctx = ctx;
            this.editText = editText;
            this.editText.setOnFocusChangeListener(this);
            this.myCalendar = Calendar.getInstance();

        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if(hasFocus){
                int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = myCalendar.get(Calendar.MINUTE);
                new TimePickerDialog(ctx, this, hour, minute, true).show();
            }
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            this.editText.setText( hourOfDay + ":" + minute);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == FL1)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                source_address = String.format("Place: %s", place.getAddress());
                et1.setText(source_address.substring(7));
                source_latlng = place.getLatLng();
                source_latitude = place.getLatLng().latitude;
                source_longitude = place.getLatLng().longitude;
            }
        }
        if(requestCode == FL2)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                destination_address = String.format("Place: %s", place.getAddress());
                et2.setText(destination_address.substring(7));
                destination_latlng = place.getLatLng();
                destination_latitude = place.getLatLng().latitude;
                destination_longitude = place.getLatLng().longitude;
            }
        }
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(TripDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            /*postDataParams = new HashMap<String, String>();
            postDataParams.put("startAddress", source);
            postDataParams.put("startLat", String.valueOf(source_latitude));
            postDataParams.put("startLng", String.valueOf(source_longitude));
            postDataParams.put("destAddress", destination);
            postDataParams.put("destLat", String.valueOf(destination_latitude));
            postDataParams.put("destLng", String.valueOf(destination_longitude));
            postDataParams.put("vehicleType", vehicle);
            postDataParams.put("preStartTime", starttime);
            postDataParams.put("preEndTime", endtime);*/
            //Call ServerData() method to call webservice and store result in response
            postDataParams = new HashMap<String, String>();
            /*postDataParams.put("startLng", String.valueOf("18.78"));
            postDataParams.put("startAddress", "Dummy Start Address");
            postDataParams.put("vehicleType", "car");
            postDataParams.put("destLat", String.valueOf("20.45"));
            postDataParams.put("destLng", String.valueOf("21.78"));
            postDataParams.put("startLat", String.valueOf("17.45"));
            postDataParams.put("preStartTime", "9:40");
            postDataParams.put("destAddress", "Dummy Destination Address");
            postDataParams.put("preEndTime", "10.40");*/
            postDataParams.put("locationId", location_id);
            response = getAllMarkers.getAllMarkers(postDataParams);
            JSONObject js1;
            for(int i=0;i<response.length();i++)
            {
                try {
                    js1=(JSONObject) response.get(i);
                    markerId = js1.get("markerId").toString();
                    latitude = js1.get("lat").toString();
                    longitude = js1.get("lng").toString();
                    locationId = js1.get("locationId").toString();
                    description = js1.get("description").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myDb.insertMarkerData(markerId, latitude, longitude, locationId, description);
            }
            Cursor c = myDb.showMarkerData();

            c.moveToFirst();
            do{
                dbMarkerId = c.getString(0);
                dbLatitude = c.getString(1);
                dbLongitude = c.getString(2);
                dbLocationId = c.getString(3);
                dbDescription = c.getString(4);

                System.out.println("Marker Data [Trip Details] : " + " MarkerId - " + dbMarkerId + " Latitude - " + dbLatitude + " Longitude - " + dbLongitude +
                        " LocationId - " + dbLocationId + " Description - " +  dbDescription);
                ServerLog.logHashMap.put(String.valueOf(ServerLog.key), dbMarkerId);
                ServerLog.key++;
            }while(c.moveToNext());
            StaticValues.listOfAllMarkers = response;
/*        try {
            json = new JSONObject(response);
            //Get Values from JSONobject
            System.out.println("success=" + json.get("success"));
            success = json.getInt("success");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
            return "success";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            JSONObject jsonResult = null;
            /*try {
                //jsonResult = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            //Intent testDA = new Intent(TripDetails.this, MapsActivity.class);
            //startActivity(testDA);
            Intent sendData = new Intent(TripDetails.this, MapsActivity.class);
            sendData.putExtra("p_source", et1.getText().toString());
            sendData.putExtra("p_destination", et2.getText().toString());
            sendData.putExtra("p_endlat", String.valueOf(destination_latitude));
            sendData.putExtra("p_endlong", String.valueOf(destination_longitude));
            try {
                    //sendData.putExtra("p_suggested_time", jsonResult.get("EXACT").toString());
                    //sendData.putExtra("p_expected_time_of_journey", jsonResult.get("EXEPECTED").toString());

                    sendData.putExtra("p_suggested_time", "01:43");
                    sendData.putExtra("p_expected_time_of_journey", "20 mins");
                    sendData.putExtra("p_start_time", et3.getText().toString());

                //sendData.putExtra("p_suggested_time", "10:00");
                //sendData.putExtra("p_expected_time_of_journey", "20 mins");
            } catch (Exception e) {
                e.printStackTrace();
            }
            startActivity(sendData);
        //if(success==1) {
          //  Toast.makeText(getApplicationContext(), "User Added Successfully..!", Toast.LENGTH_LONG).show();
        //}
        }
    }

}
