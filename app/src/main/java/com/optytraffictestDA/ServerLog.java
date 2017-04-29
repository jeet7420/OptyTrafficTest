package com.optytraffictestDA;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * Created by smarhas on 3/23/2017.
 */

public class ServerLog {

    public static HashMap<String,String> logHashMap=new HashMap<String, String>();
    //public static HashMap<String, LinkedHashMap> sendLinkedHashMap=new HashMap<String, LinkedHashMap>();
    public static int status = 0;
    public static int key = 0;

    public static void postMessage(){
        status = 1;
        String input;
        JSONArray  al=null;
        String response = null;
        try {
            URL url = new URL("http://brings.co.in/OptiTraffic/rest/MessageService/Message");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            input = getPostDataString(logHashMap);

            System.out.println("Log -> " + input);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if(conn.getResponseCode()==200)
            {
                System.out.println(" log success");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println(" Log Output from Server .... \n");
            String jsonText = readAll(br);
            JSONObject js1=null;
            JSONObject json = new JSONObject(jsonText);
            //al=(JSONArray)json.getJSONArray("response");
            response = json.get("response").toString();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(al.toString());
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        JSONObject dataAsJson = new JSONObject(params);
        return dataAsJson.toString();
    }
}
