package com.futurologeek.smartcrossing;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RankAdapter extends ArrayAdapter<User> {
    private final Context context;
    private final ArrayList<User> itemsArrayList;

    public RankAdapter(Context context, ArrayList<User> itemsArrayList) {
        super(context, R.layout.book_list_template, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.ranking_list_item, parent, false);
            holder = new ViewHolder();
            //Log.e("NewHolder", "position = " + position);
            holder.nr_tv = (TextView) convertView.findViewById(R.id.nr_tv);
            holder.user_tv = (TextView) convertView.findViewById(R.id.name_tv);
            holder.points_tv = (TextView) convertView.findViewById(R.id.points_tv);
            holder.whole = (LinearLayout) convertView.findViewById(R.id.whole);
            getItem(position).setListeners(holder, this, context);
            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView  user_tv;
        TextView  points_tv;
        TextView  nr_tv;
        LinearLayout whole;
    }
}