package com.futurologeek.smartcrossing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {
    private WebView webview;
    private EditText number_edittext;
    private Button setwebviewbutton;
    private Button parseButton;
    private String creators = "";
    private TextView titleTextView, authorTextView, dateTextView, isbnTextView, addedByTextView, categoryTextView;
    private RelativeLayout visitProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        authorTextView= (TextView) findViewById(R.id.author_textview);

        dateTextView = (TextView) findViewById(R.id.year_textview);
        categoryTextView = (TextView) findViewById(R.id.category_textview);
        addedByTextView= (TextView) findViewById(R.id.added_by_textview);
        isbnTextView= (TextView) findViewById(R.id.isbn_textview);
        new getUserBooks().execute();
    }

    class getUserBooks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.book_url+5);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    final String title = jsonObj.getString("book_title");
                    final int id = jsonObj.getInt("book_id");
                    final String author = jsonObj.getString("book_author");
                    final String ISBN = jsonObj.getString("book_isbn");
                    final String pub_date = jsonObj.getString("book_publication_date");
                    final String cat = jsonObj.getString("book_category");
                    final int creator_id = jsonObj.getInt("book_user_author");

                        Book ksiazka = new Book(id, title, author);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titleTextView.setText(title);
                            authorTextView.setText(author);
                            categoryTextView.setText(getResources().getString(R.string.cat)+" "+cat);
                            dateTextView.setText(getResources().getString(R.string.year)+" "+pub_date);
                            isbnTextView.setText(getResources().getString(R.string.isbn)+" "+ISBN);
                            addedByTextView.setText(getResources().getString(R.string.added_by)+" "+creator_id);
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