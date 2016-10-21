package com.futurologeek.smartcrossing;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class User {
    int id;
    int points;
    int rankpos;
    String name;
    RankAdapter.ViewHolder holder;
    RankAdapter adapter;

    public User(int id, String name, int points, int rankpos){
        this.id = id;
        this.points = points;
        this.name = name;
        this.rankpos = rankpos;
    }

    public void setListeners(final RankAdapter.ViewHolder holder, RankAdapter adapter, final Context context) {
        this.holder = holder;
        this.adapter = adapter;
        holder.user_tv.setText(name);
        holder.points_tv.setText(String.valueOf(points));
        holder.nr_tv.setText(String.valueOf(rankpos));
        if(id==Constants.uid){
            holder.user_tv.setTypeface(null, Typeface.BOLD);
            holder.points_tv.setTypeface(null, Typeface.BOLD);
            holder.nr_tv.setTypeface(null, Typeface.BOLD);
        }
        holder.whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatus.checkNetworkStatus(context)) {
                    Intent i = new Intent(context, ProfileActivity.class);
                    Bundle koszyk = new Bundle();
                    koszyk.putString("u_id", String.valueOf(id));
                    i.putExtras(koszyk);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    }
