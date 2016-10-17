package com.futurologeek.smartcrossing;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;
    ArrayList<Bookshelf> punkty;
    boolean isPoint;
    double longitude;
    GoogleApiClient mGoogleApiClient;
    double latitude;

    public static MapActivity newInstance() {
        MapActivity fragment = new MapActivity();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        punkty = new ArrayList<Bookshelf>();
        new GetContacts().execute();
        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            isPoint = przekazanedane.getBoolean("isPoint");
            if (isPoint) {
                longitude = przekazanedane.getDouble("longitude");
                latitude = przekazanedane.getDouble("latitude");
            }
        }

        ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                if (!isPoint) {
                    mMap = googleMap;
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            for (Bookshelf l : punkty) {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(l.getNamePlace()));
                            }
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            for (Bookshelf marker : punkty) {
                                builder.include(new LatLng(marker.getLatitude(), marker.getLongitude()));
                            }
                            LatLngBounds bounds = builder.build();
                            int padding = 200; // offset from edges of the map in pixels
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                        }
                    });
                } else {
                    mMap = googleMap;
                    final LatLng cordinates = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(cordinates).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(String.valueOf(latitude) + " " + String.valueOf(longitude)));
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cordinates, 15));
                            // Zoom in, animating the camera.
                            //mMap.animateCamera(CameraUpdateFactory.zoomIn());
                            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                        //     float[] results = new float[1];
                             //   Location.distanceBetween(51.0844578, 17.0234934, l.latitude, l.longitude);
                                //Toast.makeText(MapActivity.this, String.valueOf(results[0]), Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                }
            });
    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.bookshelf_url);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("bookshelves");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        //JSONObject titleobject = c.getJSONObject("volumeInfo");
                        final int id = c.getInt("bookshelf_id");
                        final double latitude = c.getDouble("bookshelf_latitude");
                        final double longitude = c.getDouble("bookshelf_longitude");
                        final String name = c.getString("bookshelf_name");
                        Bookshelf plejs = new Bookshelf(id, name, latitude, longitude);
                        punkty.add(plejs);
                    }
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }

}