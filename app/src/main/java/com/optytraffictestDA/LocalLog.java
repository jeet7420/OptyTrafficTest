package com.optytraffictestDA;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by smarhas on 4/4/2017.
 */

public class LocalLog {

    public static HashMap<String,String> logHashMapLocal=new HashMap<String, String>();

    public static void postMessageToLocal(){
        try {
                logHashMapLocal = ServerLog.logHashMap;
                FileOutputStream fileOutputStream = new FileOutputStream("OptiTrafficLogs.txt");
                ObjectOutputStream objectOutputStream= new ObjectOutputStream(fileOutputStream);
                objectOutputStream.writeObject(logHashMapLocal);
                objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
