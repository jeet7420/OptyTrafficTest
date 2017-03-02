package com.optytraffictest;

/**
 * Created by smarhas on 1/7/2017.
 */

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import javax.net.ssl.HttpsURLConnection;

/**
 * Created by admin on 9/9/2015.
 */
public class HTTPURLConnection {
    String response="";
    URL url;
    JSONObject jsonObj;
    String dataAsString=null;
    String input;

    public String ServerData(String path,HashMap<String, String> params) {
        try {
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            //String input = "{\"personId\":2,\"firstName\":\"Shekhar\",\"lastName\":\"Suman\",\"DOB\":\"1992-05-03\",\"contactNumber\":\"7044080726\",\"sex\":\"m\",\"email\":\"jeet7420@gmail.com\",\"city\":\"hyd\",\"averageSpeed\":\"70\",\"nationality\":\"indian\"}";

            input = getPostDataString(params);
            System.out.println("JEET : "+input);

            OutputStream os = conn.getOutputStream();
            //BufferedWriter writer = new BufferedWriter(
            //        new OutputStreamWriter(os, "UTF-8"));
            //dataAsString=getPostDataString(params);
            //jsonObj=new JSONObject(dataAsString);
            os.write(input.getBytes());
            os.flush();
            //os.close();
            //int responseCode = conn.getResponseCode();

            if (conn.getResponseCode()==200) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                //Log.d("Output",br.toString());
                while ((line = br.readLine()) != null) {
                    response += line;
                    Log.d("output lines", line);
                }
            } else {
                response = "Data Not Send";
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        /*for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append(",");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append(":");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }*/
        JSONObject dataAsJson = new JSONObject(params);
        return dataAsJson.toString();
    }
}