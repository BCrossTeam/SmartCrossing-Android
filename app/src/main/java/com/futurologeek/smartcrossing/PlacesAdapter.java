package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PlacesAdapter extends ArrayAdapter<Place> {
    private final Context context;
    private final ArrayList<Place> itemsArrayList;

    public PlacesAdapter(Context context, ArrayList<Place> itemsArrayList) {
        super(context, R.layout.shelf_template, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shelf_template, parent, false);
            holder = new ViewHolder();
            //Log.e("NewHolder", "position = " + position);
            holder.placeName = (TextView) convertView.findViewById(R.id.places_name_textview);
            holder.location = (ImageView) convertView.findViewById(R.id.location_image_view);
            holder.distanceTextView = (TextView) convertView.findViewById(R.id.distance_textview);
            holder.bookcount = (TextView) convertView.findViewById(R.id.book_count_textview);
            holder.shelf = (LinearLayout) convertView.findViewById(R.id.shelf);
            getItem(position).setListeners(holder, this, context);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }


    static class ViewHolder {
        TextView placeName;
        TextView bookcount;
        ImageView location;
        TextView distanceTextView;
        LinearLayout shelf;

    }
}