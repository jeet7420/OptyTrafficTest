package com.optytraffictest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.optytraffictestDA.MainActivityDA;
import com.optytraffictestDA.StaticValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import static android.R.attr.description;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

        String name, firstname, lastname, email, dob, gender;
        private SignInButton login;
        private GoogleApiClient googleApiClient;
        private GoogleSignInOptions googleSignInOptions;
        private static final int REQUEST_CODE = 10;
        private HTTPURLConnection service;
        HashMap<String, String> postDataParams;
        private ProgressDialog pDialog;
        private String path = "http://brings.co.in/OptiTraffic/rest/validate/email";
        String response;
        JSONObject json;


        //private Button btn_reg;
        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            service=new HTTPURLConnection();
            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
            googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                    .addApi(Plus.API).build();

            login = (SignInButton) findViewById(R.id.login);
            login.setSize(SignInButton.SIZE_WIDE);
            login.setScopes(googleSignInOptions.getScopeArray());

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                    startActivityForResult(signInIntent,REQUEST_CODE);
                }
            });
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                GoogleSignInAccount account = result.getSignInAccount();
                Person profile = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                try{

                    name = account.getDisplayName();
                    String str[] = name.split(" ");
                    firstname = str[0];
                    lastname = str[1];
                    dob = profile.getBirthday();
                    if(profile.getGender() == 0){
                        gender = "m";
                    }
                    if(profile.getGender() == 1){
                        gender = "f";
                    }
                    email = account.getEmail();
                    //Toast.makeText(MainActivity.this, currentLocation, Toast.LENGTH_LONG).show();
                    //dpurl = account.getPhotoUrl().toString();
                    new MyAsyncTask().execute();
                }
                catch (Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

        }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(Void... arg0) {

            postDataParams = new HashMap<String, String>();
            postDataParams.put("email", email);
            response = service.ServerData(path, postDataParams);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            System.out.println(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                    json = new JSONObject(result);
                    if(json.get("response").toString().equals("211")){
                        Intent sendData = new Intent(MainActivity.this, UserDetails.class);
                        startActivity(sendData);
                    }
                    else{
                            Intent sendData = new Intent(MainActivity.this, TripDetails.class);
                            sendData.putExtra("p_firstname", firstname);
                            sendData.putExtra("p_lastname", lastname);
                            sendData.putExtra("p_gender", gender);
                            sendData.putExtra("p_email", email);
                            sendData.putExtra("p_gender", gender);
                            startActivity(sendData);
                    }
            } catch (JSONException e) {
                    e.printStackTrace();
            }

        }
    }
}
