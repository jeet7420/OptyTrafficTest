package com.optytraffictest;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

        String name, firstname, lastname, email, dob, gender;
        private SignInButton login;
        private GoogleApiClient googleApiClient;
        private GoogleSignInOptions googleSignInOptions;
        private static final int REQUEST_CODE = 10;


        //private Button btn_reg;
        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



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
                    Intent sendData = new Intent(MainActivity.this, UserDetails.class);
                    name = account.getDisplayName();
                    String str[] = name.split(" ");
                    firstname = str[0];
                    lastname = str[1];
                    dob = profile.getBirthday();
                    if(profile.getGender() == 0){
                        gender = "m";
                        sendData.putExtra("p_gender", gender);
                    }
                    if(profile.getGender() == 1){
                        gender = "f";
                        sendData.putExtra("p_gender", gender);
                    }
                    email = account.getEmail();
                    //Toast.makeText(MainActivity.this, currentLocation, Toast.LENGTH_LONG).show();
                    //dpurl = account.getPhotoUrl().toString();
                    //sendData.putExtra("p_source", "GoogleSignIn");
                    sendData.putExtra("p_firstname", firstname);
                    sendData.putExtra("p_lastname", lastname);
                    sendData.putExtra("p_gender", gender);
                    sendData.putExtra("p_email", email);
                    //sendData.putExtra("p_dob", dob);
                    //sendData.putExtra("p_url", dpurl);
                    startActivity(sendData);
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
}
