package com.futurologeek.smartcrossing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {
    String title;
    String author;
    int id;
    String year;
    SearchAdapter.ViewHolder sholder;
    SearchAdapter sadapter;
    BookListAdapter.ViewHolder holder;
    BookListAdapter adapter;
    String cover = "";
    JSONObject ob;
    Activity act;
    Dialog dial;
    public static Boolean hasCover = false;

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

    public void setCover(Activity activ, ImageView iv){
        new GetBookCover(iv, activ).execute();
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

    public void setSearchListeners(final SearchAdapter.ViewHolder holder, final SearchAdapter adapter, final Context context) {
        this.sholder = holder;
        this.sadapter = adapter;
        holder.whole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkStatus.checkNetworkStatus(context)) {
                    Bundle b = new Bundle();
                    b.putInt("ajdi", id);
                    Intent i = new Intent(context,BookActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtras(b);
                    context.startActivity(i);
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void setListeners(final BookListAdapter.ViewHolder holder, final BookListAdapter adapter, final Context context, Boolean isBorrow, final int bookshelfId, final Activity act, final Dialog dial, final float distance) {
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
                                if (distance * 1000 > Constants.maxConstant(context)) {
                                    Toast.makeText(context, context.getResources().getString(R.string.ret_distance_too_long), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                                final Thread t = new Thread() {
                                    public void run() {
                                        try {
                                            POSTHandler han = new POSTHandler();
                                            JSONObject par = new JSONObject();
                                            try {
                                                par.put("user_auth_token", UserInfo.token);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ob = han.handlePOSTmethod("/bookshelf/" + bookshelfId + "/book/" + id + "/", par, true);

                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (ob.has("error")) {
                                                        if (ob.has("sub_error")) {
                                                            int sub_error = 0;
                                                            try {
                                                                sub_error = ob.getInt("sub_error");
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            sub_error = sub_error * -1;
                                                            try {
                                                                Toast.makeText(context, context.getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), context) + context.getResources().getString(R.string.ADDITIONAL_ERROR_INFO) + " " + GetStringCode.getErrorResource(sub_error, context), Toast.LENGTH_SHORT).show();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            try {
                                                                Toast.makeText(context, context.getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), context), Toast.LENGTH_SHORT).show();
                                                            } catch (JSONException e) {
                                                                 e.printStackTrace();
                                                             }
                                                        }

                                                        //Toast.makeText(SignInActivity.this, signInPassword.getText().toString() + "   "  +signInLogin.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        ((BookshelfActivity) act).onResume();
                                                        //try {
                                                        //    Toast.makeText(context, GetStringCode.getSuccessCode(ob.getInt("success"), context), Toast.LENGTH_SHORT).show();
                                                        //} catch (JSONException e) {
                                                        //    e.printStackTrace();
                                                        //}
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
                                if (distance * 1000 > Constants.maxConstant(context)) {
                                    Toast.makeText(context, context.getResources().getString(R.string.bor_distance_too_long), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                                final Thread t = new Thread() {
                                    public void run() {
                                        try {
                                            POSTHandler han = new POSTHandler();
                                            JSONObject par = new JSONObject();
                                            try {
                                                par.put("user_auth_token", UserInfo.token);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            ob = han.handlePOSTmethod("/bookshelf/" + bookshelfId + "/book/" + id + "/", par, false);

                                            act.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (ob.has("error")) {
                                                        if (ob.has("sub_error")) {
                                                            int sub_error = 0;
                                                            try {
                                                                sub_error = ob.getInt("sub_error");
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                            sub_error = sub_error * -1;
                                                            try {
                                                                Toast.makeText(context, context.getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), context) + context.getResources().getString(R.string.ADDITIONAL_ERROR_INFO) + " " + GetStringCode.getErrorResource(sub_error, context), Toast.LENGTH_SHORT).show();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        } else {
                                                            try {
                                                                Toast.makeText(context, context.getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), context), Toast.LENGTH_SHORT).show();
                                                            } catch (JSONException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }

                                                        //Toast.makeText(SignInActivity.this, signInPassword.getText().toString() + "   "  +signInLogin.getText().toString(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        ((BookshelfActivity) act).onResume();
                                                        //try {
                                                        //    Toast.makeText(context, GetStringCode.getSuccessCode(ob.getInt("success"), context), Toast.LENGTH_SHORT).show();
                                                        //} catch (JSONException e) {
                                                        //    e.printStackTrace();
                                                        //}
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

    class GetBookCover extends AsyncTask<Void, Void, Void> {
        ImageView iv;
        Activity act;

        public GetBookCover(ImageView iv, Activity act){
            super();
            this.iv = iv;
            this.act = act;
        }
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(Constants.book_url+id);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if(!(jsonObj.isNull("book_cover"))){
                        cover=Constants.content+jsonObj.getString("book_cover");
                        hasCover = true;
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PicassoTrustAll.getInstance(act.getApplicationContext()).load(cover).fit().into(iv);
                            }
                        });
                    } else {
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv.setImageResource(R.drawable.nocover);
                            }
                        });
                    }
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }


}
