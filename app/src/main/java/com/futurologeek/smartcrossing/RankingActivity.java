package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    TextView name1, name2, name3;
    TextView points1, points2, points3;
    TextView place;
    int currentuserindex = -1;
    int everyuserindex = -1;
    ListView rank;
    RankAdapter adapter;
    int urank_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_ranking);
        findViews();
        if(getIntent().getExtras()!=null) {
            Bundle przekazanedane = getIntent().getExtras();
            urank_id = przekazanedane.getInt("u_id");
            new GetContacts().execute();
        }
    }

    void findViews(){
        name1 = (TextView) findViewById(R.id.nick_1);
        name2 = (TextView) findViewById(R.id.nick_2);
        name3 = (TextView) findViewById(R.id.nick_3);
        place = (TextView) findViewById(R.id.place);
        points1 = (TextView) findViewById(R.id.points_1);
        points2 = (TextView) findViewById(R.id.points_2);
        points3 = (TextView) findViewById(R.id.points_3);
        rank = (ListView) findViewById(R.id.rank_list);
    }

    class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            final int currentindex;
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.user_rank);
            Log.e("tag", "Response from url: " + jsonStr);
            final ArrayList<User> users = new ArrayList<>();
            if (jsonStr != null) {
                try {
                    JSONArray jsonObj = new JSONArray(jsonStr);

                    for (int i = 0; i < jsonObj.length(); i++) {
                        JSONObject c = jsonObj.getJSONObject(i);
                        final String uname = c.getString("user_name");
                        final int points = c.getInt("user_score");
                        final int id = c.getInt("user_id");
                        everyuserindex = i;
                        if(id==urank_id){
                            currentuserindex = i;
                        }
                        users.add(new User(id, uname, points, everyuserindex+1));
                    }
                    final ArrayList<User> finalUsers = new ArrayList<>();

                    final int pomocnicza = (currentuserindex+1);
                    for(int i = currentuserindex-Constants.rank_places_before; i<=(currentuserindex-Constants.rank_places_before)+(Constants.rank_places_before*2); i++){
                        if(i>=0&&i<users.size()){
                          finalUsers.add(users.get(i));
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new RankAdapter(RankingActivity.this, finalUsers, urank_id);
                            rank.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            name1.setText(users.get(0).name);
                            points1.setText(String.valueOf(users.get(0).points));
                            name2.setText(users.get(1).name);
                            points2.setText(String.valueOf(users.get(1).points));
                            name3.setText(users.get(2).name);
                            points3.setText(String.valueOf(users.get(2).points));
                            place.setText(String.valueOf(pomocnicza));
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
