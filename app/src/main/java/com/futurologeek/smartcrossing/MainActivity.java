package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    ListView booklist;
    BookshelfAdapter adapter;
    EditText searchEditText;
    TableRow settings;
    TableRow profile;
    ArrayList<Bookshelf> punkty = new ArrayList<Bookshelf>();
    final Activity activity = this;

    ImageView plus;
    TableRow mapview;
    SimpleLocation location;
    double latitude, longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new BookshelfAdapter(this, punkty);
        findViews();
        setListeners();
        if (isLocationPermission()) {
            location = new SimpleLocation(this);

            if (!location.hasLocationEnabled()) {
                SimpleLocation.openSettings(this);
            }
            if(location!=null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                adapter = new BookshelfAdapter(MainActivity.this, punkty);
            }
            if(NetworkStatus.checkNetworkStatus(this)){
              new GetContacts().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }

        } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
            adapter.clear();
            adapter.notifyDataSetChanged();
            requestPermission();
        }


    }

    public void getLoc(){
        if (isLocationPermission()) {
            location = new SimpleLocation(this);
            if (!location.hasLocationEnabled()) {
                SimpleLocation.openSettings(this);
            }
            if(location!=null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                adapter = new BookshelfAdapter(MainActivity.this, punkty);
            }
            location.beginUpdates();

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
            adapter.clear();
            adapter.notifyDataSetChanged();
            requestPermission();
        }
        if(latitude==0||longitude==0){
            getLoc();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isLocationPermission()) {
            location = new SimpleLocation(this);
            if (!location.hasLocationEnabled()) {
                SimpleLocation.openSettings(this);
            }
            if(location!=null){
               // latitude = location.getLatitude();
              //  longitude = location.getLongitude();
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                adapter = new BookshelfAdapter(MainActivity.this, punkty);
            }
            location.beginUpdates();

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
            adapter.clear();
            adapter.notifyDataSetChanged();
            requestPermission();
        }

    }

    @Override
    protected void onPause() {
        if (isLocationPermission()) {
            location.endUpdates();

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
            adapter.clear();
            adapter.notifyDataSetChanged();
            requestPermission();
        }

        super.onPause();
    }


    public void sortList() {
        Collections.sort(punkty, new Comparator<Bookshelf>() {
            @Override
            public int compare(Bookshelf c1, Bookshelf c2) {
                return Float.compare(c1.getDistance(), c2.getDistance());
            }
        });
    }

    public void findViews() {
        booklist = (ListView) findViewById(R.id.book_list);
        settings = (TableRow) findViewById(R.id.settings_button);
        profile = (TableRow) findViewById(R.id.profile_button);
        plus = (ImageView) findViewById(R.id.add_button);
        mapview = (TableRow) findViewById(R.id.map_button);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
    }

    public void setListeners() {
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_book_prompt);
                dialog.setTitle(MainActivity.this.getResources().getString(R.string.select_src));
                RelativeLayout takePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_a);
                RelativeLayout choosePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_m);
                RelativeLayout addStationRelative = (RelativeLayout) dialog.findViewById(R.id.add_p);
                takePhotoRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                        dialog.dismiss();
                    }
                });
                searchEditText.setFocusable(true);

                addStationRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, AddPointActivity.class);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });

                choosePhotoRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, AddBookActivity.class);
                        startActivity(i);
                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });


        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkStatus.checkNetworkStatus(MainActivity.this)){
                    Intent i = new Intent(MainActivity.this, MapActivity.class);
                    Bundle koszyk = new Bundle();
                    koszyk.putBoolean("isPoint", false);
                    i.putExtras(koszyk);
                    startActivity(i);
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

            }
        });
       /* settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ustawienia", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }); */

        ToolbarHandler handler = new ToolbarHandler(MainActivity.this, ToolbarHandler.buttonVariation.Profile);
        handler.setListeners();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {
                String url = Constants.gapi_url + result.getContents();
                Bundle koszyk = new Bundle();
                koszyk.putString("jurl", url);
                Intent cel = new Intent(this, AddBookActivity.class);
                cel.putExtras(koszyk);
                startActivity(cel);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
                        final int id = c.getInt("bookshelf_id");
                        final double latitude = c.getDouble("bookshelf_latitude");
                        final double longitude = c.getDouble("bookshelf_longitude");
                        final String name = c.getString("bookshelf_name");
                        int bookcount = 0;
                        JSONArray books = c.getJSONArray("books");
                        for (int j = 0; j < books.length(); j++) {
                            bookcount++;
                        }

                        Bookshelf plejs = new Bookshelf(id, name, latitude, longitude, bookcount);
                        punkty.add(plejs);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            booklist.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if(longitude==0||latitude==0){
                                getLoc();
                            }
                            for(Bookshelf pl:punkty){
                                    pl.setDistance(latitude, longitude);
                                     }
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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }

    void requestPermission() {
        if (!isLocationPermission()) {
            ActivityCompat.requestPermissions(MainActivity.this,
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
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            Log.d("Pointless statement", "This statement is useless but android requires it to work properly");
                        }
                        new GetContacts().execute();
                    } else {
                        adapter.clear(); if (isLocationPermission()) {
                            location = new SimpleLocation(this);
                            if (!location.hasLocationEnabled()) {
                                SimpleLocation.openSettings(this);
                            }
                            if(location!=null){
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                adapter = new BookshelfAdapter(MainActivity.this, punkty);
                            }
                            location.beginUpdates();

                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
                            adapter.clear();
                            adapter.notifyDataSetChanged();
                            requestPermission();
                        }
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, this.getResources().getString(R.string.l_permission), Toast.LENGTH_LONG).show();
                    }
                }


            }}
    }

    boolean isLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }
}
