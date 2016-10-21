package com.futurologeek.smartcrossing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;



public class MessageAdapter extends BaseAdapter {
    private final Context context;
    private ArrayList<String> itemsArrayList = new ArrayList<String>();

    public MessageAdapter (Context context, ArrayList<String> itemsArrayList) {
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public int getCount() {
        return itemsArrayList.size();
    }

    @Override
    public String getItem(int position) {
        return itemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder holder = null;

    if (convertView == null) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.none_books_template, parent, false);
        holder = new ViewHolder();
        //Log.e("NewHolder", "position = " + position);
        holder.message = (TextView)  convertView.findViewById(R.id.message);
        holder.message.setText(itemsArrayList.get(position));
        convertView.setTag(holder);
    }
    else {
        holder = (ViewHolder) convertView.getTag();
    }

    return convertView;
}

    static class ViewHolder {
        TextView message;
    }
}
