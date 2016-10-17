package com.futurologeek.smartcrossing;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
            holder.combobox = (ImageView) convertView.findViewById(R.id.combobox_image_view);
            holder.bookcount = (TextView) convertView.findViewById(R.id.book_count_textview);
            holder.shelf = (LinearLayout) convertView.findViewById(R.id.shelf);

            holder.placeName.setText(getItem(position).getNamePlace());
            holder.bookcount.setText(context.getResources().getString(R.string.b_count) + " " + getItem(position).getBookcount());
            holder.combobox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Combobox", Toast.LENGTH_SHORT).show();
                }
            });
            holder.location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, MapActivity.class);
                    Bundle koszyk = new Bundle();
                    koszyk.putBoolean("isPoint",true);
                    koszyk.putDouble("longitude", getItem(position).getLongitude());
                    koszyk.putDouble("latitude", getItem(position).getLatitude());
                    i.putExtras(koszyk);
                    context.startActivity(i);
                }
            });
            holder.shelf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,BookshelfActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name",getItem(position).getNamePlace());
                    bundle.putDouble("longitude", getItem(position).getLongitude());
                    bundle.putInt("bookcount", getItem(position).getBookcount());
                    bundle.putDouble("latitude", getItem(position).getLatitude());
                    i.putExtras(bundle);
                    context.startActivity(i);
                }
            });
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
        ImageView combobox;
        LinearLayout shelf;

    }
}