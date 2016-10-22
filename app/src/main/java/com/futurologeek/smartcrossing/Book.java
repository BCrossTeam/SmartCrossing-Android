package com.futurologeek.smartcrossing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {
    String title;
    String author;
    int id;
    String year;
    BookListAdapter.ViewHolder holder;
    BookListAdapter adapter;
    JSONObject ob;
    Activity act;
    Dialog dial;

    enum Kategoria { Beletrystyka, Biografie, Biznes_i_inwestycje, Gotowanie, Historia, Komputery, Kryminały, Dla_dzieci, Polityka, Prawo, Religia, Romanse, SCI_FI, Zdrowie}


    public Book(int id, String title, String author){
        this.title = title;
        this.author = author;
        this.id = id;
    }

    public Book(String title, String author, String year){
        this.title = title;
        this.author = author;
        this.year = year;
    }

   public String getTitle(){
       return this.title;
   }

    public int getId(){
        return this.id;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setListeners(final BookListAdapter.ViewHolder holder, final BookListAdapter adapter, final Context context, Boolean isBorrow, final int bookshelfId, final Activity act, final Dialog dial) {
        this.holder = holder;
        this.adapter = adapter;
        this.act = act;
        this.dial = dial;


        if(!isBorrow){
            holder.whole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkStatus.checkNetworkStatus(context)) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setTitle(context.getResources().getString(R.string.confirm));
                        builder.setMessage(context.getResources().getString(R.string.return_book_confirm_1)+" \""+getTitle()+ "\" " + context.getResources().getString(R.string.return_book_confirm_2));

                        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                            public void onClick(final DialogInterface dialog, int which) {
                                final Thread t = new Thread()
                                {
                                    public void run()
                                    {
                                        try  {
                                            POSTHandler han = new POSTHandler();
                                            JSONObject par = new JSONObject();
                                            try {
                                                par.put("user_auth_token",UserInfo.token);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ob = han.handlePOSTmethod("/bookshelf/"+bookshelfId+"/book/"+id+"/",par, true);

                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(ob.has("error")){
                                                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "SUCCES", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                t.start();

                                dial.dismiss();
                                return;
                            }
                        });

                        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setCancelable(false);
                        alert.show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }

                }
            });
        } else {
            holder.whole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkStatus.checkNetworkStatus(context)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setTitle(context.getResources().getString(R.string.confirm));
                        builder.setMessage(context.getResources().getString(R.string.borrow_book_confirm_1)+" \""+getTitle()+ "\" " + context.getResources().getString(R.string.borrow_book_confirm_2));

                        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                final Thread t = new Thread()
                                {
                                    public void run()
                                    {
                                        try  {
                                            POSTHandler han = new POSTHandler();
                                            JSONObject par = new JSONObject();
                                            try {
                                                par.put("user_auth_token",UserInfo.token);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ob = han.handlePOSTmethod("/bookshelf/"+bookshelfId+"/book/"+id+"/",par, false);

                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(ob.has("error")){
                                                        Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "SUCCES", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                t.start();

                               dial.dismiss();
                                return;
                            }
                        });

                        builder.setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.setCancelable(false);
                        alert.show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }


}
