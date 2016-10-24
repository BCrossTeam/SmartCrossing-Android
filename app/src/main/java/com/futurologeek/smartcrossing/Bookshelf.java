package com.futurologeek.smartcrossing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import im.delight.android.location.SimpleLocation;

public class Bookshelf {
    private String name;
    private int bookcount;
    double latitude;
    double longitude;
    private int id;
    Context context;
    float distance;
    Activity activity;
    BookshelfAdapter.ViewHolder holder;
    BookshelfAdapter adapter;
    boolean isKM;
    JSONObject ob;
    BookshelfRequestsAdapter.ViewHolder rholder;
    BookshelfRequestsAdapter radapter;


    public Bookshelf(int id, String name, double latitude, double longitude, int bookcount) {
        this.id = id;
        this.name = name;
        this.bookcount = bookcount;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Bookshelf(int id, String name, double latitude, double longitude) {
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    float getDistance() {
        return this.distance;
    }

    double getLatitude() {
        return this.latitude;
    }

    int getId() {
        return this.id;
    }

    double getLongitude() {
        return this.longitude;
    }

    String getNamePlace() {
        return this.name;
    }

    int getBookcount() {
        return this.bookcount;
    }

    void setBookName(String newname) {
        this.name = newname;
    }

    void setBookcount(int newcount) {
        this.bookcount = newcount;
    }

    void setDistance(double latitude, double longitude, Context context) {
        float[] results = new float[1];
        SharedPreferences preferences = context.getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);

        isKM = preferences.getBoolean("isKM", true);

        Location.distanceBetween(latitude, longitude, getLatitude(), getLongitude(), results);
        if (isKM) {
            distance = (results[0] / 1000);
        } else {
            distance = (float) ((results[0]) / 1000 / 1.6);
        }
    }

    float getDistance(double latitude, double longitude, Context context) {
        float[] results = new float[1];
        SharedPreferences preferences = context.getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);

        isKM = preferences.getBoolean("isKM", true);

        if (isKM) {
            return (results[0] / 1000);
        } else {
            return (float) ((results[0]) / 1000 / 1.6);
        }
    }



    public void setListenersRequests(final BookshelfRequestsAdapter.ViewHolder rholder, BookshelfRequestsAdapter radapter, final Context context, Activity activity) {
        this.radapter = radapter;
        this.rholder = rholder;
        this.context = context;
        this.activity = activity;

        SharedPreferences preferences = context.getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);

        isKM = preferences.getBoolean("isKM", true);

        NumberFormat formatter = new DecimalFormat("#0.00");
        if (isKM) {
            rholder.distanceTextView.setText(String.valueOf(formatter.format(distance)) + " km");
        } else {
            rholder.distanceTextView.setText(String.valueOf(formatter.format(distance)) + " " + context.getResources().getString(R.string.mile));
        }

        rholder.placeName.setText(name);
        rholder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleLocation location = new SimpleLocation(context);
                if (!location.hasLocationEnabled()) {
                    Toast.makeText(context, context.getResources().getString(R.string.gps_turned_off), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (NetworkStatus.checkNetworkStatus(context)) {
                    Intent i = new Intent(context, MapActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle koszyk = new Bundle();
                    koszyk.putBoolean("isPoint", true);
                    koszyk.putDouble("longitude", getLongitude());
                    koszyk.putDouble("latitude", getLatitude());
                    koszyk.putString("name", name);
                    i.putExtras(koszyk);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });

        rholder.acceptRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               POSTVote(true);
            }
        });

        rholder.declineRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                POSTVote(false);
            }
        });

    }


    public void setListeners(final BookshelfAdapter.ViewHolder holder, BookshelfAdapter adapter, final Context context) {
        this.holder = holder;
        this.adapter = adapter;
        this.context = context;
        //setDistance(latitude, longitude);

        SharedPreferences preferences = context.getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);

        isKM = preferences.getBoolean("isKM", true);

        NumberFormat formatter = new DecimalFormat("#0.00");
        if (isKM) {
            holder.distanceTextView.setText(String.valueOf(formatter.format(distance)) + " km");
        } else {
            holder.distanceTextView.setText(String.valueOf(formatter.format(distance)) + " " + context.getResources().getString(R.string.mile));
        }


        holder.placeName.setText(getNamePlace());
        holder.bookcount.setText(context.getResources().getString(R.string.b_count) + " " + getBookcount());

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleLocation location = new SimpleLocation(context);
                if (!location.hasLocationEnabled()) {
                    Toast.makeText(context, context.getResources().getString(R.string.gps_turned_off), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (NetworkStatus.checkNetworkStatus(context)) {
                    Intent i = new Intent(context, MapActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Bundle koszyk = new Bundle();
                    koszyk.putBoolean("isPoint", true);
                    koszyk.putDouble("longitude", getLongitude());
                    koszyk.putDouble("latitude", getLatitude());
                    koszyk.putString("name", name);
                    i.putExtras(koszyk);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

            }
        });
        holder.shelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkStatus.checkNetworkStatus(context)) {
                    Intent i = new Intent(context, BookshelfActivity.class);
                    Bundle bundle = new Bundle();
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bundle.putInt("id", getId());
                    bundle.putString("name", getNamePlace());
                    bundle.putDouble("longitude", getLongitude());
                    bundle.putFloat("dist", distance);
                    bundle.putInt("bookcount", getBookcount());
                    bundle.putDouble("latitude", getLatitude());
                    i.putExtras(bundle);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    void POSTVote(final Boolean isApprove) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    POSTHandler han = new POSTHandler();
                    JSONObject par = new JSONObject();
                    try {
                        par.put("user_auth_token", UserInfo.token);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(isApprove){
                        ob = han.handlePOSTmethod(Constants.request_vote_url_1+id+Constants.request_vote_url_2_approve, par, true);
                    } else {
                        ob = han.handlePOSTmethod(Constants.request_vote_url_1+id+Constants.request_vote_url_2_disapprove, par, true);
                    }

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ob.has("error")) {
                                if (ob.has("sub_error")) {
                                    int sub_error = 0;
                                    try {
                                        sub_error = ob.getInt("sub_error");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    sub_error = sub_error * -1;
                                    try {
                                        Toast.makeText(context, context.getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), context) + context.getResources().getString(R.string.ADDITIONAL_ERROR_INFO) + " " + GetStringCode.getErrorResource(sub_error, context), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Toast.makeText(context, context.getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), context), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //Toast.makeText(SignInActivity.this, signInPassword.getText().toString() + "   "  +signInLogin.getText().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                ((RequestsActivity) activity).onResume();
                                 Toast.makeText(context, context.getResources().getString(R.string.VOTE_SUCCESSFULL), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}