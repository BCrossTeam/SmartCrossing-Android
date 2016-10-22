package com.futurologeek.smartcrossing;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BookshelfRequestsAdapter extends ArrayAdapter<Bookshelf> {
    private final Context context;
    Activity activity;
    private final ArrayList<Bookshelf> itemsArrayList;

    public BookshelfRequestsAdapter(Context context, ArrayList<Bookshelf> itemsArrayList, Activity activity) {
        super(context, R.layout.shelf_template, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.activity = activity;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.request_shelf_template, parent, false);
            holder = new ViewHolder();
            //Log.e("NewHolder", "position = " + position);
            holder.placeName = (TextView) convertView.findViewById(R.id.places_name_textview);
            holder.location = (ImageView) convertView.findViewById(R.id.location_image_view);
            holder.distanceTextView = (TextView) convertView.findViewById(R.id.distance_textview);
            holder.acceptRelative = (RelativeLayout) convertView.findViewById(R.id.accept_relative);
            holder.declineRelative = (RelativeLayout) convertView.findViewById(R.id.decline_relative);

            if(itemsArrayList.size()>0){
                getItem(position).setListenersRequests(holder, this, context, activity);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }


    static class ViewHolder {
        TextView placeName;
        ImageView location;
        TextView distanceTextView;
        RelativeLayout acceptRelative, declineRelative;
    }
}