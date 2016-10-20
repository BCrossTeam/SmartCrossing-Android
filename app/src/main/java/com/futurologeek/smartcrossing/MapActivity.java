package com.futurologeek.smartcrossing;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import im.delight.android.location.SimpleLocation;

import static com.futurologeek.smartcrossing.R.id.bookcount;
import static com.futurologeek.smartcrossing.R.id.map;
import static com.futurologeek.smartcrossing.R.id.tvTitle;

public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;
    ArrayList<Bookshelf> punkty;
    boolean isPoint;
    double longitude;
    LatLng loc;
    String pointname;
    boolean firstFit;
    double latitude;

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;
        double pos = 0;
        double lat, longi;
        SimpleLocation location;
        NumberFormat formatter = new DecimalFormat("#0.00");

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_marker_info, null);
            if (isLocationPermission()) {
                location = new SimpleLocation(MapActivity.this);

                if (!location.hasLocationEnabled()) {
                    SimpleLocation.openSettings(MapActivity.this);
                }
                if (location != null) {
                    lat = location.getLatitude();
                    longi = location.getLongitude();
                }
            }
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            SharedPreferences preferences = getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);
            SimpleLocation loc = new SimpleLocation(MapActivity.this);
            pos =  loc.calculateDistance(marker.getPosition().latitude, marker.getPosition().longitude, lat, longi);
            // Toast.makeText(MapActivity.this, lat + " " + longi, Toast.LENGTH_SHORT).show();
            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvDistance = ((TextView)myContentsView.findViewById(R.id.distance));
            if(preferences.getBoolean("isKM",false)){
                tvDistance.setText(getResources().getString(R.string.distance) +  formatter.format(pos/1000)+" km");
            } else {
                tvDistance.setText(getResources().getString(R.string.distance) +  formatter.format((pos/1000)/1.6) +getResources().getString(R.string.mile));
            }

            TextView bookCount = ((TextView)myContentsView.findViewById(R.id.bookcount));
            return myContentsView;

        }
    }

    public static MapActivity newInstance() {
        MapActivity fragment = new MapActivity();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        punkty = new ArrayList<Bookshelf>();
        if(NetworkStatus.checkNetworkStatus(this)){
            new GetContacts().execute();

            if (isLocationPermission()) {
                loadMap();
            } else {
                Toast.makeText(MapActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }

        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            isPoint = przekazanedane.getBoolean("isPoint");
            if (isPoint) {
                longitude = przekazanedane.getDouble("longitude");
                latitude = przekazanedane.getDouble("latitude");
                pointname = przekazanedane.getString("name");
            }
        }


    }

    void loadMap() {
        ((MapFragment) getFragmentManager().findFragmentById(map)).getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                if (!isPoint) {
                    firstFit = false;
                    mMap = googleMap;
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                return;
                            }
                            mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                            mMap.setMyLocationEnabled(true);
                            GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                                @Override
                                public void onMyLocationChange (Location location) {
                                    double lat, longi;
                                    if(!firstFit){
                                        SimpleLocation simplelocation = new SimpleLocation(MapActivity.this);

                                        if (!simplelocation.hasLocationEnabled()) {
                                            SimpleLocation.openSettings(MapActivity.this);
                                        }
                                        if (location != null) {
                                            lat = location.getLatitude();
                                            longi = location.getLongitude();
                                            loc = new LatLng (lat, longi);

                                            for(Bookshelf pkt : punkty){
                                                pkt.setDistance(lat, longi, MapActivity.this);
                                            }

                                            SharedPreferences preferences = getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);

                                            for (Iterator<Bookshelf> iter = punkty.listIterator(); iter.hasNext(); ) {
                                                Bookshelf a = iter.next();
                                                if (a.getDistance()>preferences.getInt("radius",30)) {
                                                    // Toast.makeText(MainActivity.this, "Za duzy dist"+a.getDistance()+" max "+preferences.getInt("radius",30), Toast.LENGTH_SHORT).show();
                                                    iter.remove();
                                                }
                                            }

                                            if(preferences.getBoolean("isPoint",false)){
                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                for(Bookshelf pkt : punkty){
                                                    builder.include(new LatLng(pkt.getLatitude(), pkt.getLongitude()));
                                                }
                                                builder.include(new LatLng(loc.latitude, loc.longitude));
                                                LatLngBounds bounds = builder.build();
                                                int padding = 200; // offset from edges of the map in pixels
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                mMap.animateCamera(cu);
                                            } else {
                                               sortList();
                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                builder.include(new LatLng(punkty.get(0).getLatitude(), punkty.get(0).getLongitude()));
                                                builder.include(new LatLng(loc.latitude, loc.longitude));
                                                LatLngBounds bounds = builder.build();
                                                int padding = 200; // offset from edges of the map in pixels
                                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                mMap.animateCamera(cu);
                                            }

                                        }
                                        firstFit = true;
                                    }
                                }
                            };
                            mMap.setOnMyLocationChangeListener(myLocationChangeListener);

                            for (Bookshelf l : punkty) {
                                mMap.addMarker(new MarkerOptions().position(new LatLng(l.getLatitude(), l.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(l.getNamePlace()));
                            }
                        }
                    });
                } else {
                    mMap = googleMap;
                    mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                    final LatLng cordinates = new LatLng(latitude, longitude);
                    mMap.addMarker(new MarkerOptions().position(cordinates).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(pointname));
                    if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                        @Override
                        public void onMyLocationChange (Location location) {
                            LatLng loc = new LatLng (location.getLatitude(), location.getLongitude());
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(new LatLng(loc.latitude, loc.longitude));
                            builder.include(new LatLng(latitude, longitude));
                            LatLngBounds bounds = builder.build();
                            int padding = 200;
                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                            mMap.animateCamera(cu);
                        }
                    };
                    mMap.setOnMyLocationChangeListener(myLocationChangeListener);
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {


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


    void requestPermission() {
        if (!isLocationPermission()) {
            ActivityCompat.requestPermissions(MapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    69);
        }
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 69: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isLocationPermission()) {
                        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        loadMap();
                        //Todo: pobieranie lokalizacji
                    } else {
                        Toast.makeText(MapActivity.this, this.getResources().getString(R.string.l_permission), Toast.LENGTH_LONG).show();
                    }
                }


            }}
    }

    boolean isLocationPermission() {
        if (ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    public void sortList(){
        Collections.sort(punkty, new Comparator<Bookshelf>() {
            @Override
            public int compare(Bookshelf c1, Bookshelf c2) {
                return Float.compare(c1.getDistance(), c2.getDistance());
            }
        });
    }

}