package com.futurologeek.smartcrossing;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;

import im.delight.android.location.SimpleLocation;


public class ToolbarHandler {
    private TableRow profileButton, settingsButton;
    Activity ctx;
    private TextView firstButtonTextView, secondButtonTextView;
    buttonVariation wariacja;

    enum buttonVariation {Profile, Main, Bookshelf}

    ;
    SimpleLocation loc;
    ImageView plus;


    public ToolbarHandler(Activity ctx, buttonVariation wariacja) {
        this.ctx = ctx;
        this.wariacja = wariacja;
        loc = new SimpleLocation(ctx);
    }

    void setListeners() {
        profileButton = (TableRow) ctx.findViewById(R.id.profile_button);
        settingsButton = (TableRow) ctx.findViewById(R.id.settings_button);
        firstButtonTextView = (TextView) ctx.findViewById(R.id.first_button_textview);
        plus = (ImageView) ctx.findViewById(R.id.add_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!loc.hasLocationEnabled()){
                    SimpleLocation.openSettings(ctx);
                    return;
                } else {
                    Intent i = new Intent(ctx, SettingsActivity.class);
                    ctx.startActivity(i);
                }
            }
        });

        switch (wariacja) {
            case Profile:
                profileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!loc.hasLocationEnabled()) {
                            SimpleLocation.openSettings(ctx);
                            return;
                        } else {
                            if (NetworkStatus.checkNetworkStatus(ctx)) {
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


                    }
                });
                break;
            case Main:
            case Bookshelf:
                firstButtonTextView.setText(ctx.getResources().getString(R.string.main_act));
                profileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!loc.hasLocationEnabled()) {
                            SimpleLocation.openSettings(ctx);
                            return;
                        } else {
                            Intent i = new Intent(ctx, MainActivity.class);
                            ctx.startActivity(i);
                            ctx.finish();
                        }
                    }
                });
                break;
            default:
                firstButtonTextView.setText(ctx.getResources().getString(R.string.main_act));
                profileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!loc.hasLocationEnabled()) {
                            SimpleLocation.openSettings(ctx);
                            return;
                        } else {
                            Intent i = new Intent(ctx, MainActivity.class);
                            ctx.startActivity(i);
                            ctx.finish();
                        }
                    }
                });


        }

        if (wariacja != buttonVariation.Bookshelf) {
            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    final Dialog dialog = new Dialog(ctx);
                    dialog.setContentView(R.layout.add_book_prompt);
                    dialog.setTitle(ctx.getResources().getString(R.string.select_src));
                    RelativeLayout takePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_a);
                    RelativeLayout choosePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_m);
                    RelativeLayout addStationRelative = (RelativeLayout) dialog.findViewById(R.id.add_p);
                    takePhotoRelative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (NetworkStatus.checkNetworkStatus(ctx)) {
                                IntentIntegrator integrator = new IntentIntegrator(ctx);
                                integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                                integrator.setPrompt("Scan");
                                integrator.setCameraId(0);
                                integrator.setBeepEnabled(false);
                                integrator.setBarcodeImageEnabled(true);
                                integrator.initiateScan();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(ctx, ctx.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                    addStationRelative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleLocation location = new SimpleLocation(ctx);
                            if (!location.hasLocationEnabled()) {
                                SimpleLocation.openSettings(ctx);
                                return;
                            } else {
                                if (NetworkStatus.checkNetworkStatus(ctx)) {
                                    Intent i = new Intent(ctx, AddPointActivity.class);
                                    ctx.startActivity(i);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(ctx, ctx.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                                }

                            }
                        }
                    });

                    choosePhotoRelative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(ctx, AddBookActivity.class);
                            ctx.startActivity(i);
                            dialog.dismiss();

                        }
                    });
                    dialog.show();

                }
            });

        }
    }
}
