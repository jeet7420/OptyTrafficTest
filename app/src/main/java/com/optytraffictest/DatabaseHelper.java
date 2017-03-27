package com.optytraffictest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by smarhas on 12/19/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserDetails.db";
    public static final String BASIC_DETAILS_TABLE = "hr_details";
    public static final String EXTRA_DETAILS_TABLE = "extra_details";
    public static final String BDT_COL1 = "ID";
    public static final String BDT_COL2 = "NAME";
    public static final String BDT_COL3 = "EMAIL";
    public static final String BDT_COL4 = "MOBILENO";
    public static final String EDT_COL1 = "ID";
    public static final String EDT_COL2 = "EMAIL";
    public static final String EDT_COL3 = "EMAIL";




    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + BASIC_DETAILS_TABLE + " (" + BDT_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + BDT_COL2 + " TEXT," + BDT_COL3 + " TEXT," + BDT_COL4 + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE TRIP (ID INTEGER PRIMARY KEY AUTOINCREMENT, STARTLAT TEXT, STARTLONG TEXT, ENDLAT TEXT, ENDLONG TEXT, STARTTIME TEXT, ENDTIME TEXT, VEHICLE TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE MARKER (MARKER_ID TEXT, LATITUDE TEXT, LONGITUDE TEXT, LOCATION_ID TEXT, DESCRIPTION TEXT)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("DROP TABLE IF EXITS " + BASIC_DETAILS_TABLE);
        //onCreate(sqLiteDatabase);
    }

    public boolean insertHrData(String name, String email, String mobileno)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BDT_COL2, name);
        contentValues.put(BDT_COL3, email);
        contentValues.put(BDT_COL4, mobileno);
        long result = db.insert(BASIC_DETAILS_TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor showData() {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] coloumns = {"STARTLAT", "STARTLONG", "ENDLAT", "ENDLONG", "STARTTIME", "ENDTIME", "VEHICLE"};
            String test_name = "";
            Cursor c = db.query("TRIP", coloumns, null, null, null, null, null);
            return c;
    }

    public Cursor showMarkerData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {"MARKER_ID", "LATITUDE", "LONGITUDE", "LOCATION_ID", "DESCRIPTION"};
        Cursor c = db.query("MARKER", coloumns, null, null, null, null, null);
        return c;
    }

    public boolean insertMarkerData(String markerId, String latitude, String longitude, String locationId, String description)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("MARKER_ID", markerId);
        contentValues.put("LATITUDE", latitude);
        contentValues.put("LONGITUDE", longitude);
        contentValues.put("LOCATION_ID", locationId);
        contentValues.put("DESCRIPTION", description);
        long result = db.insert("MARKER", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public String compareCurrentLocationWithDbMarkers(String currentLat, String currentLng){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] coloumns = {"MARKER_ID"};
        String whereClause = "LATITUDE=? AND LONGITUDE=?";
        String[] selectionArgs = {currentLat,currentLng};
        Cursor c = db.query("MARKER",coloumns,whereClause,selectionArgs,null,null,null,null);
        if(c.getCount()!=0){
            c.moveToFirst();
            return c.getString(0);
        }
        return "no data";
    }
}
