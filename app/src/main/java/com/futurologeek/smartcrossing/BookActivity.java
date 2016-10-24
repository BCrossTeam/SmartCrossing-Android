package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    private TextView titleTextView, authorTextView, dateTextView, isbnTextView, addedByTextView, categoryTextView;
    private RelativeLayout visitProfile;
    int id;
    int creator_id;
    Boolean hasCover = false;
    String book_cover;
    ImageView coverImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        findViews();
        ToolbarHandler handler = new ToolbarHandler(this, ToolbarHandler.buttonVariation.Main);
        handler.setListeners();

        if(getIntent().getExtras()!=null) {
            Bundle przekazanedane = getIntent().getExtras();
            id = przekazanedane.getInt("ajdi");
            new getUserBooks(true).execute();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {
                String url = Constants.gapi_url + result.getContents();
                Bundle koszyk = new Bundle();
                koszyk.putString("jurl", url);
                Intent cel = new Intent(this, AddBookActivity.class);
                cel.putExtras(koszyk);
                startActivity(cel);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void findViews(){
        titleTextView = (TextView) findViewById(R.id.title_textview);
        authorTextView= (TextView) findViewById(R.id.author_textview);
        visitProfile = (RelativeLayout) findViewById(R.id.visit_profile);
        dateTextView = (TextView) findViewById(R.id.year_textview);
        categoryTextView = (TextView) findViewById(R.id.category_textview);
        addedByTextView= (TextView) findViewById(R.id.added_by_textview);
        isbnTextView= (TextView) findViewById(R.id.isbn_textview);
        coverImage = (ImageView) findViewById(R.id.cover_image);
    }


    class getUserBooks extends AsyncTask<Void, Void, Void> {
        Boolean getList;


        public getUserBooks(Boolean getList){
            super();
            this.getList = getList;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr;
            if(getList){
                 jsonStr = sh.makeServiceCall(Constants.book_url+id);
            } else {
                jsonStr = sh.makeServiceCall(Constants.user_url+creator_id);
            }


            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    if(getList){
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        final String title = jsonObj.getString("book_title");
                        final int id = jsonObj.getInt("book_id");
                        final String author = jsonObj.getString("book_author");
                        final String ISBN = jsonObj.getString("book_isbn");
                        final String pub_date = jsonObj.getString("book_publication_date");
                        final String cat = jsonObj.getString("book_category");
                        creator_id = jsonObj.getInt("book_user_author");
                        if(!(jsonObj.isNull("book_cover"))){
                            book_cover = jsonObj.getString("book_cover");
                            hasCover = true;
                        }
                        Book ksiazka = new Book(id, title, author);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                titleTextView.setText(title);
                                authorTextView.setText(author);
                                if(hasCover){
                                    PicassoTrustAll.getInstance(BookActivity.this).load(Constants.content+book_cover).fit().into(coverImage);
                                } else {
                                    coverImage.setImageResource(R.drawable.nocover);
                                }
                                categoryTextView.setText(getResources().getString(R.string.cat)+" "+GetCategory.returnCategory(BookActivity.this, cat));
                                dateTextView.setText(getResources().getString(R.string.year)+" "+pub_date);
                                if(ISBN.equals("0000000000")||ISBN.equals("0000000000000")){
                                    isbnTextView.setText(getResources().getString(R.string.isbn)+" "+getResources().getString(R.string.no_ISBN));
                                } else {
                                    isbnTextView.setText(getResources().getString(R.string.isbn)+" "+ISBN);
                                }

                                visitProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (NetworkStatus.checkNetworkStatus(BookActivity.this)) {
                                            Intent i = new Intent(BookActivity.this, ProfileActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            Bundle koszyk = new Bundle();
                                            koszyk.putString("u_id", String.valueOf(creator_id));
                                            i.putExtras(koszyk);
                                            startActivity(i);
                                            finish();
                                        } else {
                                            Toast.makeText(BookActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                                new getUserBooks(false).execute();
                            }
                        });

                    } else {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        final String uname = jsonObj.getString("user_name");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              addedByTextView.setText(getResources().getString(R.string.added_by)+" "+uname);
                            }
                        });

                    }
                } catch (final JSONException e) {
                    Log.e("TAG", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("TAG", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }
    }

}