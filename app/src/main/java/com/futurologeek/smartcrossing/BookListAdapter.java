package com.futurologeek.smartcrossing;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookListAdapter extends ArrayAdapter<Book> {
    private final Context context;
    private final ArrayList<Book> itemsArrayList;

    public BookListAdapter(Context context, ArrayList<Book> itemsArrayList) {
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
            convertView = inflater.inflate(R.layout.book_list_template, parent, false);
            holder = new ViewHolder();
            //Log.e("NewHolder", "position = " + position);
            holder.tvtitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvauthor = (TextView) convertView.findViewById(R.id.tvAuthor);
            holder.ivimage = (ImageView) convertView.findViewById(R.id.ivBookCover);

            holder.tvtitle.setText(getItem(position).getTitle());
            holder.tvauthor.setText(getItem(position).getAuthor());
            holder.ivimage.setImageResource(R.drawable.nocover);

            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    static class ViewHolder {
        TextView  tvtitle;
        TextView  tvauthor;
        ImageView ivimage;
    }
}