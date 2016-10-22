package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SearchBookActivity extends AppCompatActivity {
    ListView bookList;
    EditText searchEditText;
    ArrayList<Book> ksiazki = new ArrayList<Book>();
    SearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        findViews();
        setListeners();
    }
    public void findViews(){
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        bookList = (ListView) findViewById(R.id.book_list_listview);
        searchEditText.post(new Runnable() {
            @Override
            public void run() {
                searchEditText.requestFocusFromTouch();
                searchEditText.requestFocus();
                InputMethodManager lManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                lManager.showSoftInput(searchEditText, 0);
            }
        });
    }

    public void setListeners(){
        ArrayList<String> mess = new ArrayList<String>();
        mess.add(getResources().getString(R.string.enter_search_phrase));
        MessageAdapter ad = new MessageAdapter(SearchBookActivity.this, mess);
        bookList.setAdapter(ad);
        ad.notifyDataSetChanged();


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    new getBooksFromQuery(s.toString()).execute();
                } else {
                    ArrayList<String> mess = new ArrayList<String>();
                    mess.add(getResources().getString(R.string.enter_search_phrase));
                    MessageAdapter ad = new MessageAdapter(SearchBookActivity.this, mess);
                    bookList.setAdapter(ad);
                    ad.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    class getBooksFromQuery extends AsyncTask<Void, Void, Void> {
        String st;
        public getBooksFromQuery(String st){
            super();
            this.st = st;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.search_url+st);

            Log.e("tag", "Response from url: " + jsonStr);
            ksiazki.clear();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("books");
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        final int id = c.getInt("book_id");
                        final String title = c.getString("book_title");
                        int bookcount = 0;
                        Book ksiazka = new Book(id, title, "");
                        ksiazki.add(ksiazka);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(ksiazki.size()>0){
                                adapter = new SearchAdapter(SearchBookActivity.this, ksiazki);
                                bookList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            } else {
                                ArrayList<String> mess = new ArrayList<String>();
                                mess.add(getResources().getString(R.string.book_not_found));
                                MessageAdapter ad = new MessageAdapter(SearchBookActivity.this, mess);
                                bookList.setAdapter(ad);
                                ad.notifyDataSetChanged();
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
}

