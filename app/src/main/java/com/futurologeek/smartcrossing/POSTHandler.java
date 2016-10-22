package com.futurologeek.smartcrossing;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class POSTHandler {

    public JSONObject handlePOSTmethod(String url, JSONObject obj, Boolean isPOST) throws ProtocolException {
        HttpURLConnection conn = null;
        JSONObject output = null;
        conn = getConnectionFromUrl(url);
        if(!isPOST){
        conn.setRequestMethod("DELETE");
        }
        sendJson(conn, obj);
        output = handleOutput(conn);
        Log.d("--xooutputxo--", output.toString());
        conn.disconnect();
        return output;
    }

    public void sendJson(HttpURLConnection conn, JSONObject json){
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(conn.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            out.write(json.toString().getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HttpURLConnection getConnectionFromUrl(String parturl){
        URL url = null;
        HttpURLConnection connection = null;
        try {
            url = new URL(Constants.api_url+parturl);
            //Connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(Constants.C_TIMEOUT);
            connection.setReadTimeout(Constants.R_TIMEOUT);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoInput(true);
            connection.setDoOutput(true);
        } catch (MalformedURLException e) {
            try {
                throw e;
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public JSONObject handleOutput(HttpURLConnection conn){
        JSONObject output = null;
        InputStream in = null;
        try {
            in = new DataInputStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            String input = decodeInputStream(in);
            output = new JSONObject(input);
            Log.d("--output--", output.toString());


        } catch (JSONException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return output;
    }

    public static String decodeInputStream(InputStream stream) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        int temp;
        String output = "";
        while ((temp = reader.read()) != -1) {
            output += (char) temp;
        }
        return output;
    }


}
