package com.futurologeek.smartcrossing;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

public class Book {
    String title;
    String author;
    int id;
    String year;
    BookListAdapter.ViewHolder holder;
    BookListAdapter adapter;

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

    public void setListeners(final BookListAdapter.ViewHolder holder, BookListAdapter adapter, final Context context, Boolean isBorrow) {
        this.holder = holder;
        this.adapter = adapter;


        if(!isBorrow){
            holder.whole.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkStatus.checkNetworkStatus(context)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setTitle(context.getResources().getString(R.string.confirm));
                        builder.setMessage(context.getResources().getString(R.string.return_book_confirm_1)+" \""+getTitle()+ "\" " + context.getResources().getString(R.string.return_book_confirm_2));

                        builder.setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                //Todo: dodaj ksiazke do półki
                                dialog.dismiss();
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
                                //Todo: Wypożycz książkę z półki
                                dialog.dismiss();
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
