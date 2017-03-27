package com.optytraffictestDA;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;

import com.optytraffictest.DatabaseHelper;

/**
 * Created by smarhas on 3/25/2017.
 */

public class DatabaseOperation  extends AppCompatActivity{

    DatabaseHelper myDb;

    public void insertMarkerData(String markerId, String latitude, String longitude, String locationId, String description){
        myDb = new DatabaseHelper(this);
        myDb.insertMarkerData(markerId, latitude, longitude, locationId, description);
    }

    public Cursor showMarkerData(){
        myDb = new DatabaseHelper(this);
        Cursor c = myDb.showMarkerData();
        return c;
    }
}
