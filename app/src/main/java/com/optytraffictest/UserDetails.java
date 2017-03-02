package com.optytraffictest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserDetails extends AppCompatActivity {
    DatabaseHelper myDb;
    private ImageView dp;
    private EditText et_firstname, et_lastname, et_dob, et_cno, et_gender, et_email, et_city, et_nationality;
    private String cityName, stateName, nationalityName;
    private Button btn_submit, btn_date;
    String s1,s2,s3,s4;
    private HTTPURLConnection service;
    private ProgressDialog pDialog;
    private JSONObject json;
    private int success = 0;
    private String path = "http://brings.co.in/OptiTraffic/rest/register/user";
    String strname, stremail, strcno;
    GPSTracker gps;
    double latitude, longitude;
    RadioButton rb_gender_male, rb_gender_female;
    int day, month, year;
    static final int dialog_id = 0;
    String response = "";
    //Create hashmap Object to send parameters to web service
    HashMap<String, String> postDataParams;
    String  i_firstname, i_lastname, i_dob, i_cno, i_gender, i_email, i_city, i_nationality;
    String  firstname, lastname, dob, cno, gender, email, city, nationality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        myDb = new DatabaseHelper(this);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.mytoolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Basic Details");

        final Calendar calender = Calendar.getInstance();
        year = calender.get(Calendar.YEAR);
        month = calender.get(Calendar.MONTH);
        day = calender.get(Calendar.DAY_OF_MONTH);
        gps = new GPSTracker(UserDetails.this);
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            //Toast.makeText(getApplicationContext(), "LATITUDE : " + latitude, Toast.LENGTH_LONG).show();
            //Toast.makeText(getApplicationContext(), "LONGITUDE : " + longitude, Toast.LENGTH_LONG).show();
        }
        else{
            gps.showSettingsAlert();
        }
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            cityName = addresses.get(0).getLocality();
            stateName = addresses.get(0).getAdminArea();
            nationalityName = addresses.get(0).getCountryName();

            //Toast.makeText(getApplicationContext(), addresses.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        et_firstname = (EditText) findViewById(R.id.et_firstname);
        et_lastname = (EditText) findViewById(R.id.et_lastname);
        et_dob = (EditText) findViewById(R.id.et_dob);
        //et_gender = (EditText) findViewById(R.id.et_gender);
        et_cno = (EditText) findViewById(R.id.et_cno);
        et_email = (EditText) findViewById(R.id.et_email);
        et_city = (EditText) findViewById(R.id.et_city);
        et_nationality = (EditText) findViewById(R.id.et_nationality);
        rb_gender_male = (RadioButton) findViewById(R.id.rb_gender_male);
        rb_gender_female = (RadioButton) findViewById(R.id.rb_gender_female);
        btn_date = (Button) findViewById(R.id.btn_date);


        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(dialog_id);
            }
        });

        Intent i = getIntent();
      /*  String source = i.getStringExtra("p_source");
        if(source.equals("EmailSignUp")){
            username.setEnabled(true);
            password.setEnabled(true);
        }
        if(source.equals("GoogleSignIn")){
            username.setEnabled(false);
            password.setEnabled(false);
            username.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
        } */
        i_firstname = i.getStringExtra("p_firstname");
        i_lastname = i.getStringExtra("p_lastname");
        i_dob = i.getStringExtra("p_dob");
        i_gender = i.getStringExtra("p_gender");
        i_email = i.getStringExtra("p_email");
        //i_city = i.getStringExtra("p_city");
        //i_nationality = i.getStringExtra("p_nationality");

        Toast.makeText(getApplicationContext(), i_gender, Toast.LENGTH_LONG).show();
        if(i_gender.equals("m"))
            rb_gender_male.setChecked(true);
        if(i_gender.equals("f"))
            rb_gender_female.setChecked(true);
        et_firstname.setText(i_firstname);
        et_lastname.setText(i_lastname);
        //et_dob.setText(i_dob);
        //et_gender.setText(i_gender);
        et_email.setText(i_email);
        et_city.setText(cityName);
        et_nationality.setText(nationalityName);

        /*strname=name.getText().toString();
        stremail=email.getText().toString();
        strcno=cno.getText().toString();*/

        service=new HTTPURLConnection();

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname = et_firstname.getText().toString();
                lastname = et_lastname.getText().toString();
                dob = et_dob.getText().toString();
                cno = et_cno.getText().toString();
                gender = i_gender.toUpperCase();
                email = et_email.getText().toString();
                city = et_city.getText().toString();
                nationality = et_nationality.getText().toString();
                /*boolean isInserted = myDb.insertHrData(name.getText().toString(),
                                  email.getText().toString(),
                                  cno.getText().toString());
                if(isInserted == true)
                    Toast.makeText(UserDetails.this, "Data Inserted", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(UserDetails.this, "Data Not Inserted", Toast.LENGTH_LONG).show();
                /*Cursor cursor = myDb.showData();
                cursor.moveToFirst();
                do{
                    s1 = cursor.getString(0);
                    s2 = cursor.getString(1);
                    s3 = cursor.getString(2);
                    s4 = cursor.getString(3);
                }while(cursor.moveToNext());
                Toast.makeText(UserDetails.this, s1, Toast.LENGTH_LONG).show();
                Toast.makeText(UserDetails.this, s2, Toast.LENGTH_LONG).show();
                Toast.makeText(UserDetails.this, s3, Toast.LENGTH_LONG).show();
                Toast.makeText(UserDetails.this, s4, Toast.LENGTH_LONG).show();
                Intent frequentlocations = new Intent(getApplicationContext(), FrequentPlaces.class);
                startActivity(frequentlocations);*/
                //new MyAsyncTask().execute();
                Intent intent = new Intent(UserDetails.this, TripDetails.class);
                startActivity(intent);
            }
        });
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(i_url);
                    InputStream is = url.openConnection().getInputStream();
                    final Bitmap bmp = BitmapFactory.decodeStream(is);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dp.setImageBitmap(bmp);
                        }
                    });


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }


    protected Dialog onCreateDialog(int id){
        if(id == dialog_id)
            return new DatePickerDialog(this, datepickerlistener, year, month, day);
        return null;
    }

    private DatePickerDialog.OnDateSetListener datepickerlistener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            year = i;
            month = i1 + 1;
            day = i2;
            et_dob.setText(year + "-" + month + "-" + day);
            System.out.println("JEET" + et_dob.getText().toString());
        }
    };

private class MyAsyncTask extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pDialog = new ProgressDialog(UserDetails.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Void... arg0) {
        postDataParams = new HashMap<String, String>();
        postDataParams.put("firstName", firstname);
        postDataParams.put("lastName", lastname);
        postDataParams.put("dob", dob);
        postDataParams.put("contactNumber", cno);
        postDataParams.put("sex", gender);
        postDataParams.put("email", email);
        postDataParams.put("city", city);
        postDataParams.put("nationality", nationality);
        System.out.println("JEET : " + dob);
        System.out.println("JEET : " + cno);
        //Call ServerData() method to call webservice and store result in response
        response = service.ServerData(path, postDataParams);
/*        try {
            json = new JSONObject(response);
            //Get Values from JSONobject
            System.out.println("success=" + json.get("success"));
            success = json.getInt("success");

        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        return response;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        if (pDialog.isShowing())
            pDialog.dismiss();
        /*if(success==1) {
            Toast.makeText(getApplicationContext(), "User Added Successfully..!", Toast.LENGTH_LONG).show();
        }*/
    }
    }
}


