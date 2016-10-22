package com.futurologeek.smartcrossing;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private TextView usernametv, scoretv;
    String id;
    ArrayList<Book> user_books = new ArrayList<Book>();
    TableRow tableToInflejt;
    RelativeLayout rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_profile);
        findViews();
        setListeners();
        ToolbarHandler handler = new ToolbarHandler(ProfileActivity.this, ToolbarHandler.buttonVariation.Main);
        handler.setListeners();

        if(getIntent().getExtras()!=null){
            Bundle przekazanedane = getIntent().getExtras();
            id  = przekazanedane.getString("u_id");
            if(NetworkStatus.checkNetworkStatus(this)){
                new GetContacts().execute();
                new getUserBooks().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void findViews(){
        usernametv = (TextView) findViewById(R.id.username_tv);
        scoretv = (TextView) findViewById(R.id.points_tv);
        tableToInflejt = (TableRow) findViewById(R.id.table_to_inflate);
        rank = (RelativeLayout) findViewById(R.id.go_to_ranking);
    }

    public void setListeners(){
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, RankingActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle b = new Bundle();
                b.putInt("u_id", Integer.parseInt(id));
                i.putExtras(b);
                startActivity(i);
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
            String jsonStr = sh.makeServiceCall(Constants.user_url+id);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    final String uname = jsonObj.getString("user_name");
                    final int points = jsonObj.getInt("user_score");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            usernametv.setText(uname);
                            scoretv.setText(String.valueOf(points));
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

    class getUserBooks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.user_url+id+"/book");

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("user_borrowed_books");
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

                            if(user_books.size()>0){
                                for(Book k: user_books) {
                                    View child = View.inflate(ProfileActivity.this, R.layout.book_list_item_in_bookshelf, null);
                                    TextView title = (TextView) child.findViewById(R.id.title_textview);
                                    if (k.getTitle().length() > 18) {
                                        title.setText(k.getTitle().substring(0, 17) + "...");
                                    } else {
                                        title.setText(k.getTitle());
                                    }
                                    tableToInflejt.addView(child);
                                    CustomOnClickListener list = new CustomOnClickListener(k, child);
                                    list.setListener();
                                }
                            } else {
                                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.no_books, null);
                                tableToInflejt.addView(view);
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
                    if (NetworkStatus.checkNetworkStatus(ProfileActivity.this)) {
                        Bundle b = new Bundle();
                        b.putInt("ajdi", ks.getId());
                        Intent i = new Intent(ProfileActivity.this,BookActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtras(b);
                        startActivity(i);
                    } else {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }
}
