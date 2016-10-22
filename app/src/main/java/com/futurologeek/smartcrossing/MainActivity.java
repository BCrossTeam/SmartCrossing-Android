package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import im.delight.android.location.SimpleLocation;

public class MainActivity extends AppCompatActivity {
    ListView booklist;
    BookshelfAdapter adapter;
    EditText searchEditText;
    TableRow settings;
    TextView bookshelfPrepTV;
    LinearLayout bookshelfPrepLL;
    TableRow profile;
    ArrayList<Bookshelf> punkty = new ArrayList<Bookshelf>();
    final Activity activity = this;
    Boolean nextResume = false;
    ImageView plus;
    TableRow mapview;
    SimpleLocation location;
    double latitude, longitude;
    JSONObject ob;
    String querySt;
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new BookshelfAdapter(this, punkty);
        findViews();
        setListeners();
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter("INTERNET_READY"));
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getLoc();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void getLoc(){
        location = new SimpleLocation(this);
        if(!location.hasLocationEnabled()){
            Toast.makeText(activity, getResources().getString(R.string.gps_turned_off), Toast.LENGTH_SHORT).show();
            Log.d("Location_status", "off");
        }
        if (isLocationPermission()) {
            Log.d("Location_permission", "granted");
            if(location!=null){
                Log.d("Location!=null", "yes");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                adapter = new BookshelfAdapter(MainActivity.this, punkty);
            }
            Log.d("Location_beginupdates", "true");
            location.beginUpdates();

        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
            adapter.clear();
            adapter.notifyDataSetChanged();
            requestPermission();
            return;
        }

        if((latitude==0||longitude==0)&&(location.hasLocationEnabled()&&isLocationPermission())){
            getLoc();
        } else if(location.hasLocationEnabled()&&isLocationPermission()&&((latitude!=0||longitude!=0))) {
            if(NetworkStatus.checkNetworkStatus(this)){
                new GetBookshelves().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetRequests().execute();
        getLoc();
    }

    @Override
    protected void onPause() {
    location.endUpdates();
    bookshelfPrepLL.setVisibility(View.GONE);
    adapter.clear();
    adapter.notifyDataSetChanged();
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
        mapview = (TableRow) findViewById(R.id.map_button);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        bookshelfPrepLL = (LinearLayout) findViewById(R.id.bookshelf_prep_ll);
        bookshelfPrepTV = (TextView) findViewById(R.id.bookshelf_prep_tv);
    }

    public void setListeners() {

        bookshelfPrepLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RequestsActivity.class);
                Bundle b = new Bundle();
                b.putDouble("latitude",latitude);
                b.putDouble("longitude",longitude);
                i.putExtras(b);
                startActivity(i);
            }
        });

        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchBookActivity.class);
                startActivity(i);
            }
        });

        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!location.hasLocationEnabled()){
                    SimpleLocation.openSettings(MainActivity.this);
                    return;
                } else {
                    if (NetworkStatus.checkNetworkStatus(MainActivity.this)) {
                        Intent i = new Intent(MainActivity.this, MapActivity.class);
                        Bundle koszyk = new Bundle();
                        koszyk.putBoolean("isPoint", false);
                        i.putExtras(koszyk);
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });


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

    class GetBookshelves extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.bookshelf_url);

            Log.e("tag", "Response from url: " + jsonStr);
            punkty.clear();
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
                            adapter = new BookshelfAdapter(MainActivity.this, punkty);
                            booklist.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            if(longitude==0||latitude==0){
                                getLoc();
                                return;
                            } else {
                                for(Bookshelf pl:punkty){
                                    pl.setDistance(latitude, longitude, MainActivity.this);
                                }
                                SharedPreferences preferences = getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);


                                for (Iterator<Bookshelf> iter = punkty.listIterator(); iter.hasNext(); ) {
                                    Bookshelf a = iter.next();
                                    if (a.getDistance()>preferences.getInt("radius",30)) {
                                       // Toast.makeText(MainActivity.this, "Za duzy dist"+a.getDistance()+" max "+preferences.getInt("radius",30), Toast.LENGTH_SHORT).show();
                                        iter.remove();
                                    }
                                }
                                sortList();
                            }
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
                        getLoc();
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

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                    mobile != null && mobile.isConnectedOrConnecting();
            if (isConnected) {
                Log.d("Network Available ", "YES");
                context.sendBroadcast(new Intent("INTERNET_READY"));
            } else {
                Log.d("Network Available ", "NO");
            }
        }}

    class GetRequests extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            counter = 0;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.request_url+UserInfo.token);

            Log.e("tag", "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("bookshelf_requests");
                    for (int i = 0; i < contacts.length(); i++) {
                        counter++;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(counter==1){
                                bookshelfPrepLL.setVisibility(View.VISIBLE);
                                bookshelfPrepTV.setText(getResources().getString(R.string.NEW_REQUEST));
                            } else if(counter>1){
                                bookshelfPrepLL.setVisibility(View.VISIBLE);
                                bookshelfPrepTV.setText(getResources().getString(R.string.NEW_REQUESTS));
                            }
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
}
