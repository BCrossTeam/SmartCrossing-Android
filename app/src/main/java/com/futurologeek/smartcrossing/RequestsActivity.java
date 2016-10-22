package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class RequestsActivity extends AppCompatActivity {
    ListView requestList;
    BookshelfRequestsAdapter adapter;
    ArrayList<Bookshelf> lista = new ArrayList<>();
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        findViews();

        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            latitude = przekazanedane.getDouble("latitude");
            longitude = przekazanedane.getDouble("longitude");
        }
        new GetContacts().execute();
    }

    public void findViews() {
        adapter = new BookshelfRequestsAdapter(this, lista);
        requestList = (ListView) findViewById(R.id.request_list);
    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.request_url+UserInfo.token);

            Log.e("tag", "Response from url: " + jsonStr);
            lista.clear();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("bookshelf_requests");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        final int id = c.getInt("bookshelf_request_id");
                        final double latitude = c.getDouble("bookshelf_latitude");
                        final double longitude = c.getDouble("bookshelf_longitude");
                        final String name = c.getString("bookshelf_name");

                        Bookshelf plejs = new Bookshelf(id, name, latitude, longitude);
                        lista.add(plejs);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new BookshelfRequestsAdapter(RequestsActivity.this, lista);
                            requestList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            for (Bookshelf pl : lista) {
                                pl.setDistance(latitude, longitude, RequestsActivity.this);
                            }
                            SharedPreferences preferences = getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);
                            sortList();
                        }

                    });

                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

    }
    public void sortList() {
        Collections.sort(lista, new Comparator<Bookshelf>() {
            @Override
            public int compare(Bookshelf c1, Bookshelf c2) {
                return Float.compare(c1.getDistance(), c2.getDistance());
            }
        });
    }
}
