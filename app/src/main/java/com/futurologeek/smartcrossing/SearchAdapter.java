package com.futurologeek.smartcrossing;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<Book> {
    private final Context context;
    private final ArrayList<Book> itemsArrayList;
    Activity act;

    public SearchAdapter(Context context, ArrayList<Book> itemsArrayList, Activity act) {
        super(context, R.layout.book_list_template, itemsArrayList);
        this.context = context;
        this.itemsArrayList = itemsArrayList;
        this.act = act;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_book_list_item, parent, false);
            holder = new ViewHolder();
            //Log.e("NewHolder", "position = " + position);
            holder.tvtitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvauthor = (TextView) convertView.findViewById(R.id.tvAuthor);
            holder.ivimage = (ImageView) convertView.findViewById(R.id.ivBookCover);
            holder.whole = (RelativeLayout) convertView.findViewById(R.id.whole_layout);
            holder.tvtitle.setText(getItem(position).getTitle());
            holder.tvauthor.setText(getItem(position).getAuthor());
            getItem(position).setSearchListeners(holder, this, context);
            getItem(position).setCover(act, holder.ivimage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }


    static class ViewHolder {
        TextView tvtitle;
        TextView tvauthor;
        ImageView ivimage;
        RelativeLayout whole;
    }

}
