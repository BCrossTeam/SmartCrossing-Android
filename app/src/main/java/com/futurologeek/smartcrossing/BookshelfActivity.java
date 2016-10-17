package com.futurologeek.smartcrossing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookshelfActivity extends FragmentActivity {
    private GoogleMap mMap;
    TextView name, tvBookCount;
    double longitude;
    double latitude;
    int ajdi;
    private TableRow inflejtTable;
    ArrayList<Book> ksiazki = new ArrayList<Book>();
    int bookcount;

    public static BookshelfActivity newInstance() {
        BookshelfActivity fragment = new BookshelfActivity();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        findViews();
        new GetContacts().execute();
        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            name.setText(przekazanedane.getString("name"));
            longitude = przekazanedane.getDouble("longitude");
            latitude = przekazanedane.getDouble("latitude");
            bookcount = przekazanedane.getInt("bookcount");
            ajdi = przekazanedane.getInt("id");
            tvBookCount.setText(this.getResources().getString(R.string.b_count) + " " + bookcount);


            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    LatLng sydney = new LatLng(latitude, longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                }
            });
        }
    }

    public void findViews() {
        name = (TextView) findViewById(R.id.name);
        tvBookCount = (TextView) findViewById(R.id.tv_book_count);
        inflejtTable = (TableRow) findViewById(R.id.table_to_inflate);
    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall("https://api.smartcrossing.pl/bookshelf/"+String.valueOf(ajdi)+"/book");


            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("bookshelf_books");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        final String title = c.getString("book_title");
                        final int id = c.getInt("book_id");
                        final String author = c.getString("book_author");
                        Book ksiazka = new Book(id, title, author);
                        ksiazki.add(ksiazka);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(ksiazki.size()>0){
                                for(Book k: ksiazki){
                                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                                            (Context.LAYOUT_INFLATER_SERVICE);
                                    View view = inflater.inflate(R.layout.book_list_item_in_bookshelf,null);
                                    TextView txt = (TextView) view.findViewById(R.id.title_textview);
                                    if(k.getTitle().length()>18){
                                        txt.setText(k.getTitle().substring(0, 17)+"...");
                                    } else {
                                        txt.setText(k.getTitle());
                                    }

                                    inflejtTable.addView(view);
                                }
                            } else {
                                LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.no_books,null);
                                inflejtTable.addView(view);
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