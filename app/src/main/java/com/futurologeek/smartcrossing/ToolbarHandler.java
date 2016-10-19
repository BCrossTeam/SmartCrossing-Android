package com.futurologeek.smartcrossing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class ToolbarHandler {
    private TableRow profileButton, settingsButton;
    Activity ctx;
    private TextView firstButtonTextView, secondButtonTextView;
    buttonVariation wariacja;
    enum buttonVariation {Profile, Main}

    public ToolbarHandler(Activity ctx, buttonVariation wariacja){
        this.ctx = ctx;
        this.wariacja = wariacja;
    }

    void setListeners(){
        profileButton = (TableRow)  ctx.findViewById(R.id.profile_button);
        settingsButton = (TableRow) ctx.findViewById(R.id.settings_button);
        firstButtonTextView = (TextView) ctx.findViewById(R.id.first_button_textview);

        switch (wariacja){
            case Profile:
                profileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(NetworkStatus.checkNetworkStatus(ctx)){
                            Intent i = new Intent(ctx, ProfileActivity.class);
                            //Todo: Pobieranie user id
                            Bundle koszyk = new Bundle();
                            koszyk.putString("u_id", String.valueOf(Constants.uid));
                            i.putExtras(koszyk);
                            ctx.startActivity(i);
                        } else {
                            Toast.makeText(ctx, ctx.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                        }

                    }
                });
                break;
            case Main:
                firstButtonTextView.setText(ctx.getResources().getString(R.string.main_act));
                profileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       Intent i = new Intent(ctx, MainActivity.class);
                        ctx.startActivity(i);
                        ctx.finish();
                    }
                });
                break;
            default:
                firstButtonTextView.setText(ctx.getResources().getString(R.string.main_act));
                profileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(ctx, MainActivity.class);
                        ctx.startActivity(i);
                        ctx.finish();
                    }
                });


        }


        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ctx, BookActivity.class);
                ctx.startActivity(i);
                ctx.finish();
            }
        });
    }
}
