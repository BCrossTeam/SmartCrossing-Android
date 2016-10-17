package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView booklist;
    PlacesAdapter adapter;
    EditText searchEditText;
    TableRow settings;
    TableRow profile;
    ArrayList<Place> punkty = new ArrayList<Place>();
    final Activity activity = this;
    ImageView plus;
    TableRow mapview;
    String url = "http://app.smartcrossing.amirecki.com/bookshelf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetContacts().execute();
        findViews();
        setListeners();
    }

    public void findViews(){
        booklist = (ListView) findViewById(R.id.book_list);
        settings = (TableRow) findViewById(R.id.settings_button);
        profile = (TableRow) findViewById(R.id.profile_button);
        plus = (ImageView) findViewById(R.id.add_button);
        mapview = (TableRow) findViewById(R.id.map_button);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
    }
    public void setListeners(){
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_book_prompt);
                dialog.setTitle(MainActivity.this.getResources().getString(R.string.select_src));
                RelativeLayout takePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_a);
                RelativeLayout choosePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_m);
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
                Intent i = new Intent(MainActivity.this, MapActivity.class);
                Bundle koszyk = new Bundle();
                koszyk.putBoolean("isPoint",false);
                i.putExtras(koszyk);
                startActivity(i);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ustawienia", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                String url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:"+result.getContents();
                Bundle koszyk = new Bundle();
                koszyk.putString("jurl", url);
                // Definiujemy cel
                Intent cel = new Intent(this, AddBookActivity.class);
                cel.putExtras(koszyk);
                // Wysyłamy
                startActivity(cel);
            }

        }
        else {
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
            String jsonStr = sh.makeServiceCall(url);

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
                        Log.d("Leeel", latitude+ " " + longitude+ " " + name + " " + String.valueOf(id));
                        Place plejs = new Place(id, name, latitude, longitude, -1);
                        punkty.add(plejs);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter=new PlacesAdapter(MainActivity.this,punkty);
                                booklist.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
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
