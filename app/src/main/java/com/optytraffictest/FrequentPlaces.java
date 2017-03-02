package com.optytraffictest;

import android.content.Intent;
import android.location.Address;import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class FrequentPlaces extends AppCompatActivity {

    private Geocoder gc;
    private List<android.location.Address> list;
    private EditText et1, et2, et3;
    private Button btn1, btn2, btn3, btn4;
    private int FL1 = 1;
    private int FL2 = 2;
    private int FL3 = 3;
    private LatLng latlng1 = null;
    private LatLng latlng2 = null;
    private LatLng latlng3 = null;
    private String add1, add2, add3;
    private String address1, address2, address3;
    private String str_latlng1, str_latlng2, str_latlng3;
    private double latitude1, latitude2, latitude3;
    private double longitude1, longitude2, longitude3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequent_places);

        gc = new Geocoder(this);
        //et1 = (EditText) findViewById(R.id.address1);
        //et2 = (EditText) findViewById(R.id.address2);
        et3 = (EditText) findViewById(R.id.address3);
        //btn1 = (Button) findViewById(R.id.button2);
        ///btn2 = (Button) findViewById(R.id.button2);
        btn3 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(FrequentPlaces.this);
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
                    intent = builder.build(FrequentPlaces.this);
                    startActivityForResult(intent, FL2);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(FrequentPlaces.this);
                    startActivityForResult(intent, FL3);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address1 = et1.getText().toString();
                address2 = et2.getText().toString();
                address3 = et3.getText().toString();

                Toast.makeText(FrequentPlaces.this, address1, Toast.LENGTH_LONG).show();
                /*
                try {
                    list = gc.getFromLocationName(address1, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Address add = list.get(0);
                double lat = add.getLatitude();
                double lon = add.getLongitude();

                String lat1 = String.valueOf(lat);
                String lon1 = String.valueOf(lon);

                Toast.makeText(FrequentPlaces.this, lat1, Toast.LENGTH_LONG).show();
                Toast.makeText(FrequentPlaces.this, lon1, Toast.LENGTH_LONG).show();
                */
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == FL1)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                add1 = String.format("Place: %s", place.getAddress());
                et1.setText(add1);
                latlng1 = place.getLatLng();
                str_latlng1 = latlng1.toString();
                latitude1 = place.getLatLng().latitude;
                longitude1 = place.getLatLng().longitude;
            }
        }
        if(requestCode == FL2)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                add2 = String.format("Place: %s", place.getAddress());
                latlng2 = place.getLatLng();
                et2.setText(add2);
                str_latlng2 = latlng2.toString();
                latitude2 = place.getLatLng().latitude;
                longitude2 = place.getLatLng().longitude;
            }
        }
        if(requestCode == FL3)
        {
            if(resultCode == RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, this);
                add3 = String.format("Place: %s", place.getAddress());
                latlng3 = place.getLatLng();
                et3.setText(add3);
                str_latlng3 = latlng3.toString();
                latitude3 = place.getLatLng().latitude;
                longitude3 = place.getLatLng().longitude;
            }
        }
    }
}
