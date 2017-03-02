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
         // sqLiteDatabase.execSQL("CREATE TABLE TEST_TABLE (ID TEXT, NAME TEXT)");
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
        String[] coloumns = {BDT_COL1,BDT_COL2,BDT_COL3,BDT_COL4};
        String test_name = "";
        Cursor c = db.query(BASIC_DETAILS_TABLE, coloumns, null, null, null, null, null);
        return c;
    }
}
