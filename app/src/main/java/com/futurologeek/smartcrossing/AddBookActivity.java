package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


public class AddBookActivity extends AppCompatActivity {
    String url = "";
    String creators = "";
    EditText addTitle, addAuthor;
    NumberPicker year;
    ImageView arrow;
    int cyear = Calendar.getInstance().get(Calendar.YEAR);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        findViews();
        setListeners();
        if(getIntent().getExtras()!=null){
            Bundle przekazanedane = getIntent().getExtras();
            url  = przekazanedane.getString("jurl");
            new GetContacts().execute();
        }

    }

    public void findViews(){
        addTitle =   (EditText) findViewById(R.id.add_title);
        addAuthor = (EditText) findViewById(R.id.add_author);
        arrow = (ImageView) findViewById(R.id.arrow);
        year =(NumberPicker) findViewById(R.id.numberPicker);
        year.setMinValue(1800);
        year.setMaxValue(cyear);
        year.setWrapSelectorWheel(false);
        year.setValue(cyear);
    }

    public void setListeners(){
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Intent i = new Intent(AddBookActivity.this,AddCoverActivity.class);
              //  startActivity(i);
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
            String jsonStr = sh.makeServiceCall(url);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("items");
                    // for (int i = 0; i < contacts.length(); i++) {
                    JSONObject c = contacts.getJSONObject(0);
                    JSONObject titleobject = c.getJSONObject("volumeInfo");
                    final String title = titleobject.getString("title");
                    final String year = titleobject.getString("publishedDate");
                    JSONArray arr = titleobject.getJSONArray("authors");
                    for (int j = 0; j < arr.length(); j++) {
                        creators = creators + arr.getString(j);
                        if (j != arr.length() - 1) {
                            creators = creators + ", ";
                        }

                        Book ksiazka = new Book(title, creators, year);
                        AddBookActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                addTitle.setText(title);
                                addAuthor.setText(creators);
                            }
                        });


                        // }
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
