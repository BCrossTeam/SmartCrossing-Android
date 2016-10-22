package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoadingActivity extends AppCompatActivity {
    private Button signInButton, signUpButton;
    String tok;
    JSONObject jsonObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        findViews();
        setListeners();
        DBHandler db = new DBHandler(LoadingActivity.this);
        if(db.giveArray(true).size()>0){
            tok = db.getToken(1);
            new checkToken().execute();
        }

        if(!(db.giveArray(false).size()>0)) {
            final Dialog dialog = new Dialog(LoadingActivity.this);
            dialog.setContentView(R.layout.first_run_popup);
            Button dismissButton = (Button) dialog.findViewById(R.id.dismiss_but);
            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

    public void findViews() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
    }

    public void setListeners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            goToSignIn();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    public void goToSignIn() {
        Intent i = new Intent(this, SignInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

            //finish();
}

    public void goToSignUp(){
        Intent i = new Intent(this, SignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        //finish();
    }

    class checkToken extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.auth_url+tok);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    jsonObj = new JSONObject(jsonStr);

                    if(jsonObj.has("success")){
                        LoadingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DBHandler db = new DBHandler(LoadingActivity.this);
                                try {
                                    db.deleteAll();
                                    int id = jsonObj.getInt("user_id");
                                    UserInfo.uid = id;
                                    UserInfo.token = tok;
                                    db.addUserData(String.valueOf(id),tok);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(LoadingActivity.this, getResources().getString(R.string.logged_in), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(LoadingActivity.this, MainActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                                finish();
                            }
                        });
                    } else {
                        LoadingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoadingActivity.this, getResources().getString(R.string.ERROR_USER_NOT_SIGNED_IN), Toast.LENGTH_SHORT).show();
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
    }
}


