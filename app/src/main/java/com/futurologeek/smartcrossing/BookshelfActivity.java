package com.futurologeek.smartcrossing;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.services.books.Books;
import com.google.api.services.books.model.*;
import com.google.zxing.integration.android.IntentIntegrator;

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
    int b_id;
    private TableRow inflejtTable;
    ArrayList<Book> ksiazki = new ArrayList<Book>();
    int zmiennik = 0;
    ArrayList<Book> user_books = new ArrayList<Book>();
    int bookcount;
    public ImageView plus;
    public BookListAdapter bookListAdapter;

    public static BookshelfActivity newInstance() {
        BookshelfActivity fragment = new BookshelfActivity();
        return fragment;
    }

    void runAsync() {
        if (NetworkStatus.checkNetworkStatus(this)) {
            new GetContacts().execute();
        } else {
            Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkStatus.checkNetworkStatus(this)) { new GetContacts().execute(); } else { Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show(); }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_bookshelf);
        bookListAdapter = new BookListAdapter(BookshelfActivity.this, user_books, false, ajdi, BookshelfActivity.this, null);
        findViews();
        setListeners();

        ToolbarHandler handler = new ToolbarHandler(this, ToolbarHandler.buttonVariation.Bookshelf);
        handler.setListeners();

        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            final String nejm = przekazanedane.getString("name");
            name.setText(nejm);
            longitude = przekazanedane.getDouble("longitude");
            latitude = przekazanedane.getDouble("latitude");
            bookcount = przekazanedane.getInt("bookcount");
            ajdi = przekazanedane.getInt("id");
            tvBookCount.setText(this.getResources().getString(R.string.b_count) + " " + bookcount);

            if (NetworkStatus.checkNetworkStatus(this)) {
                new GetContacts().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }


            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    LatLng sydney = new LatLng(latitude, longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    mMap.addMarker(new MarkerOptions().position(sydney).title(nejm).icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                }
            });
        }
    }

    public void findViews() {
        name = (TextView) findViewById(R.id.name);
        tvBookCount = (TextView) findViewById(R.id.tv_book_count);
        inflejtTable = (TableRow) findViewById(R.id.table_to_inflate);
        plus = (ImageView) findViewById(R.id.add_button);
    }

    public void setListeners() {
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseBookDialog();
            }
        });
    }

    public void chooseBookDialog() {

        final Dialog dialog = new Dialog(BookshelfActivity.this);
        dialog.setContentView(R.layout.return_or_borrow_prompt);
        dialog.setTitle(BookshelfActivity.this.getResources().getString(R.string.select_src));
        RelativeLayout borrowBook = (RelativeLayout) dialog.findViewById(R.id.borrow_book);
        RelativeLayout returnBook = (RelativeLayout) dialog.findViewById(R.id.return_book);
        borrowBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatus.checkNetworkStatus(BookshelfActivity.this)) {
                    new getUserBooks(true).execute();
                } else {
                    Toast.makeText(BookshelfActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

            }
        });

        returnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatus.checkNetworkStatus(BookshelfActivity.this)) {
                    new getUserBooks(false).execute();
                } else {
                    Toast.makeText(BookshelfActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();

    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.bookshelf_url + String.valueOf(ajdi) + "/book");


            Log.e("tag", "Response from url: " + jsonStr);
            ksiazki.clear();
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
                            tvBookCount.setText(getResources().getString(R.string.b_count) + " " + ksiazki.size());
                            inflejtTable.removeAllViews();
                            if (ksiazki.size() > 0) {
                                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                                for (Book k : ksiazki) {
                                    View view = inflater.inflate(R.layout.book_list_item_in_bookshelf, null);
                                    RelativeLayout whole = (RelativeLayout) view.findViewById(R.id.whole);
                                    b_id = k.getId();
                                    TextView txt = (TextView) view.findViewById(R.id.title_textview);
                                    if (k.getTitle().length() > 18) {
                                        txt.setText(k.getTitle().substring(0, 17) + "...");
                                    } else {
                                        txt.setText(k.getTitle());
                                    }
                                    CustomOnClickListener list = new CustomOnClickListener(k, view);
                                    list.setListener();
                                    inflejtTable.addView(view);
                                }
                            } else {
                                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.no_books, null);
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
                        return;
                    }
                });

            }

            return null;
        }
    }

    class getUserBooks extends AsyncTask<Void, Void, Void> {
        Boolean isBorrow;

        public getUserBooks(Boolean isBorrow){
            super();
            this.isBorrow = isBorrow;
        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr;
            if(!isBorrow){
                jsonStr = sh.makeServiceCall(Constants.user_url + UserInfo.uid + "/book");
            } else {
                jsonStr = sh.makeServiceCall(Constants.bookshelf_url + ajdi + "/book");
            }


            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray contacts;
                        if(!isBorrow){
                           contacts = jsonObj.getJSONArray("user_borrowed_books");
                        } else {
                            contacts = jsonObj.getJSONArray("bookshelf_books");
                        }

                        user_books.clear();

                        for (int i = 0; i < contacts.length(); i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            final String title = c.getString("book_title");
                            final int id = c.getInt("book_id");
                            final String author = c.getString("book_author");
                            Book ksiazka = new Book(id, title, author);
                            user_books.add(ksiazka);
                        }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Dialog dialog = new Dialog(BookshelfActivity.this);
                            dialog.setContentView(R.layout.add_book_dialog);
                            dialog.setTitle(getResources().getString(R.string.b_add));
                            ListView lista = (ListView) dialog.findViewById(R.id.listView);
                            if(user_books.size()>0){
                                if(isBorrow){
                                    bookListAdapter = new BookListAdapter(BookshelfActivity.this, user_books, true, ajdi, BookshelfActivity.this, dialog);
                                } else {
                                    bookListAdapter = new BookListAdapter(BookshelfActivity.this, user_books, false, ajdi, BookshelfActivity.this, dialog);
                                }
                                lista.setAdapter(bookListAdapter);
                                bookListAdapter.notifyDataSetChanged();
                            } else {
                                ArrayList<String> mess = new ArrayList<String>();
                                if(!isBorrow){
                                    mess.add(getResources().getString(R.string.unfort_y_ve_no_books));
                                } else {
                                    mess.add(getResources().getString(R.string.unfort_this_bshlf_no_books));
                                }
                                MessageAdapter ad = new MessageAdapter(BookshelfActivity.this, mess);
                                lista.setAdapter(ad);
                                ad.notifyDataSetChanged();
                            }
                            dialog.show();

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

    public class CustomOnClickListener {
        Book ks;
        View v;

        public CustomOnClickListener(Book ks, View v) {
            this.ks = ks;
            this.v = v;
        }

        public void setListener(){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkStatus.checkNetworkStatus(BookshelfActivity.this)) {
                        Bundle b = new Bundle();
                        b.putInt("ajdi", ks.getId());
                        Intent i = new Intent(BookshelfActivity.this,BookActivity.class);
                        i.putExtras(b);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    } else {
                        Toast.makeText(BookshelfActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }


}