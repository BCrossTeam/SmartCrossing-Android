package com.futurologeek.smartcrossing;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;


public class POSTHandler {
    StringBuilder sb;

    public JSONObject handlePOSTmethod(String url, JSONObject obj, Boolean isPOST) throws ProtocolException {
        HttpURLConnection conn = null;
        JSONObject output = null;
        conn = getConnectionFromUrl(url);
        if(!isPOST){
        conn.setRequestMethod("DELETE");
        }
        sendJson(conn, obj);
        output = handleOutput(conn);
        Log.d("--output--", output.toString());
        conn.disconnect();
        return output;
    }

    public JSONObject handlePOSTmethodAddBook(String filepath, JSONObject json) throws IOException, JSONException {
        try {
            String charset = "UTF-8";
            File uploadFile1 = new File(filepath);
            String requestURL = Constants.api_url + "/book/";

            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFormField("json", json.toString());
            multipart.addFilePart("uploaded", uploadFile1);

            List<String> response = multipart.finish();

            Log.v("rht", "SERVER REPLIED:");
            sb = new StringBuilder();
            for (String line : response) {
                Log.v("rht", "Line : " + line);
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
    }

    public JSONObject handlePOSTmethodAddBook(JSONObject json) throws IOException, JSONException {
        try {
            String charset = "UTF-8";
            String requestURL = Constants.api_url + "/book/";
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);
            multipart.addFormField("json", json.toString());


            List<String> response = multipart.finish();

            Log.v("rht", "SERVER REPLIED:");
            sb = new StringBuilder();
            for (String line : response) {
                Log.v("rht", "Line : " + line);
                sb.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
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
