package com.futurologeek.smartcrossing;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    private TextView usernametv, scoretv;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_profile);
        findViews();
        ToolbarHandler handler = new ToolbarHandler(ProfileActivity.this, ToolbarHandler.buttonVariation.Main);
        handler.setListeners();
        if(getIntent().getExtras()!=null){
            Bundle przekazanedane = getIntent().getExtras();
            id  = przekazanedane.getString("u_id");
            new GetContacts().execute();
        }


    }

    public void findViews(){
        usernametv = (TextView) findViewById(R.id.username_tv);
        scoretv = (TextView) findViewById(R.id.points_tv);
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
}
