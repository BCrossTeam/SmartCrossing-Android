package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Bookshelf {
    private String name;
    private int bookcount;
    double latitude;
    double longitude;
    private int id;
    float distance;
    BookshelfAdapter.ViewHolder holder;
    BookshelfAdapter adapter;


    public Bookshelf(int id, String name, double latitude, double longitude, int bookcount){
        this.id = id;
        this.name = name;
        this.bookcount = bookcount;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public Bookshelf(int id, String name, double latitude, double longitude){
        this.name = name;
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    float getDistance(){
        return this.distance;
    }
    double getLatitude(){
        return this.latitude;
    }

    int getId(){
        return this.id;
    }
    double getLongitude(){
        return this.longitude;
    }
    String getNamePlace(){
        return this.name;
    }

    int getBookcount(){
        return this.bookcount;
    }

    void setBookName(String newname){
        this.name = newname;
    }

    void setBookcount(int newcount){
        this.bookcount = newcount;
    }

    void setDistance(double latitude, double longitude){
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, getLatitude(), getLongitude(), results);
        distance = results[0];

    }


    public void setListeners(final BookshelfAdapter.ViewHolder holder, BookshelfAdapter adapter, final Context context, double latitude, double longitude) {
    this.holder = holder;
    this.adapter = adapter;

       //setDistance(latitude, longitude);

        NumberFormat formatter = new DecimalFormat("#0.00");
        holder.distanceTextView.setText(String.valueOf(formatter.format(distance/1000))+" km");

        holder.placeName.setText(getNamePlace());
        holder.bookcount.setText(context.getResources().getString(R.string.b_count) + " " + getBookcount());

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MapActivity.class);
                Bundle koszyk = new Bundle();
                koszyk.putBoolean("isPoint",true);
                koszyk.putDouble("longitude", getLongitude());
                koszyk.putDouble("latitude", getLatitude());
                i.putExtras(koszyk);
                context.startActivity(i);
            }
        });
        holder.shelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,BookshelfActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id",getId());
                bundle.putString("name",getNamePlace());
                bundle.putDouble("longitude", getLongitude());
                bundle.putInt("bookcount", getBookcount());
                bundle.putDouble("latitude", getLatitude());
                i.putExtras(bundle);
                context.startActivity(i);
            }
        });
    }

    }
