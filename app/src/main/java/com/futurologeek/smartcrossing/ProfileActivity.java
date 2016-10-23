package com.futurologeek.smartcrossing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private TextView usernametv, scoretv;
    String id;
    ArrayList<Book> user_books = new ArrayList<Book>();
    TableRow tableToInflejt, tableToInflejtBadges;
    RelativeLayout rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_profile);
        findViews();
        setListeners();
        ToolbarHandler handler = new ToolbarHandler(ProfileActivity.this, ToolbarHandler.buttonVariation.Main);
        handler.setListeners();
        new GetBadges().execute();

        if(getIntent().getExtras()!=null){
            Bundle przekazanedane = getIntent().getExtras();
            id  = przekazanedane.getString("u_id");
            if(NetworkStatus.checkNetworkStatus(this)){
                new GetUserInfo().execute();
                new getUserBooks().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void findViews(){
        usernametv = (TextView) findViewById(R.id.username_tv);
        scoretv = (TextView) findViewById(R.id.points_tv);
        tableToInflejt = (TableRow) findViewById(R.id.table_to_inflate);
        rank = (RelativeLayout) findViewById(R.id.go_to_ranking);
        tableToInflejtBadges = (TableRow) findViewById(R.id.table_to_inflate_badges);
    }

    public void setListeners(){
        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, RankingActivity.class);
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle b = new Bundle();
                b.putInt("u_id", Integer.parseInt(id));
                i.putExtras(b);
                startActivity(i);
            }
        });
    }

    public void inflateBadge(int res){
        View child = View.inflate(ProfileActivity.this, R.layout.badge_item, null);
        ImageView img = (ImageView) child.findViewById(R.id.badge_img);
        img.setImageResource(res);
        CustomOnClickListener lis = new CustomOnClickListener(img);
        lis.setSpecificListener(img);
        tableToInflejtBadges.addView(child);
    }

    class GetUserInfo extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.user_url+id);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    final String uname = jsonObj.getString("user_name");
                    final int points = jsonObj.getInt("user_score");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            usernametv.setText(uname);
                            scoretv.setText(String.valueOf(points));
                        }
                    });

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

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }

    class getUserBooks extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.user_url+id+"/book");

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("user_borrowed_books");
                    user_books.clear();
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);
                        final String title = c.getString("book_title");
                        final int id = c.getInt("book_id");
                        final String author = c.getString("book_author");
                        Book ksiazka = new Book(id, title, author);
                        user_books.add(ksiazka);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if(user_books.size()>0){
                                for(Book k: user_books) {
                                    View child = View.inflate(ProfileActivity.this, R.layout.book_list_item_in_bookshelf, null);
                                    TextView title = (TextView) child.findViewById(R.id.title_textview);
                                    if (k.getTitle().length() > 18) {
                                        title.setText(k.getTitle().substring(0, 17) + "...");
                                    } else {
                                        title.setText(k.getTitle());
                                    }
                                    tableToInflejt.addView(child);
                                    CustomOnClickListener list = new CustomOnClickListener(k, child);
                                    list.setListener();
                                }
                            } else {
                                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService
                                        (Context.LAYOUT_INFLATER_SERVICE);
                                View view = inflater.inflate(R.layout.no_books, null);
                                tableToInflejt.addView(view);
                            }

                        }
                    });

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

    class GetBadges extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(Constants.badges_url+id+Constants.badges_url_2);

            Log.e("tag", "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    final int addedBooksTier = jsonObj.getInt("user_badge_added_books_tier");
                    final int addedBookshelvesTier = jsonObj.getInt("user_badge_added_bookshelves_tier");
                    final int booksBorrowedByUserTier = jsonObj.getInt("user_badge_books_borrowed_by_user_tier");
                    final int booksBorrowedByOtherTier = jsonObj.getInt("user_badge_books_borrowed_by_other_tier");
                    final int badgeScoreTier = jsonObj.getInt("user_badge_score_tier");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    tableToInflejtBadges.removeAllViews();

                    switch (addedBooksTier) {
                        case 1:
                            inflateBadge(R.drawable.added_books_1);
                            break;
                        case 2:
                            inflateBadge(R.drawable.added_books_5);
                            break;
                        case 3:
                            inflateBadge(R.drawable.added_books_20);
                            break;
                        case 4:
                            inflateBadge(R.drawable.added_books_50);
                            break;
                        default:
                            break;
                    }

                    switch (addedBookshelvesTier) {
                        case 1:
                            inflateBadge(R.drawable.added_bookshelves_1);
                            break;
                        case 2:
                            inflateBadge(R.drawable.added_bookshelves_3);
                            break;
                        case 3:
                            inflateBadge(R.drawable.added_bookshelves_10);
                            break;
                        case 4:
                            inflateBadge(R.drawable.added_bookshelves_20);
                            break;
                        default:
                            break;
                    }

                    switch (booksBorrowedByOtherTier) {
                        case 1:
                            inflateBadge(R.drawable.borrowed_by_others_1);
                            break;
                        case 2:
                            inflateBadge(R.drawable.borrowed_by_others_5);
                            break;
                        case 3:
                            inflateBadge(R.drawable.borrowed_by_others_20);
                            break;
                        case 4:
                            inflateBadge(R.drawable.borrowed_by_others_50);
                            break;
                        default:
                            break;
                    }

                    switch (booksBorrowedByUserTier) {
                        case 1:
                            inflateBadge(R.drawable.borrowed_books_1);
                            break;
                        case 2:
                            inflateBadge(R.drawable.borrowed_books_5);
                            break;
                        case 3:
                            inflateBadge(R.drawable.borrowed_books_20);
                            break;
                        case 4:
                            inflateBadge(R.drawable.borrowed_books_50);
                            break;
                        default:
                            break;
                    }

                    switch (badgeScoreTier) {
                        case 1:
                            inflateBadge(R.drawable.score_9996);
                            break;
                        case 2:
                            inflateBadge(R.drawable.score_9997);
                            break;
                        case 3:
                            inflateBadge(R.drawable.score_9998);
                            break;
                        case 4:
                            inflateBadge(R.drawable.score_9999);
                            break;
                        default:
                            break;
                    }



                        }
                    });

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

    public class CustomOnClickListener {
        Book ks;
        View v;
        ImageView iv;

        public CustomOnClickListener(Book ks, View v) {
            this.ks = ks;
            this.v = v;
        }

        public CustomOnClickListener(ImageView iv) {
            this.iv = iv;
        }

        public void setSpecificListener(ImageView img){
            if(checkImageResource(ProfileActivity.this, iv, R.drawable.added_books_1)||checkImageResource(ProfileActivity.this, iv, R.drawable.added_books_5)||checkImageResource(ProfileActivity.this, iv, R.drawable.added_books_20)||checkImageResource(ProfileActivity.this, iv, R.drawable.added_books_50)){
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this,getResources().getString(R.string.BADGE_ADDED_BOOKS), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_books_1)||checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_books_5)||checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_books_20)||checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_books_50)){
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.BADGE_USER_BORROWED), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(checkImageResource(ProfileActivity.this, iv, R.drawable.added_bookshelves_1)||checkImageResource(ProfileActivity.this, iv, R.drawable.added_bookshelves_3)||checkImageResource(ProfileActivity.this, iv, R.drawable.added_bookshelves_10)||checkImageResource(ProfileActivity.this, iv, R.drawable.added_bookshelves_20)){
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.BADGE_ADDED_BOOKSHELVES), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(checkImageResource(ProfileActivity.this, iv, R.drawable.score_9996)||checkImageResource(ProfileActivity.this, iv, R.drawable.score_9997)||checkImageResource(ProfileActivity.this, iv, R.drawable.score_9998)||checkImageResource(ProfileActivity.this, iv, R.drawable.score_9999)){
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.BADGE_USER_SCORE), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if(checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_by_others_1)||checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_by_others_5)||checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_by_others_20)||checkImageResource(ProfileActivity.this, iv, R.drawable.borrowed_by_others_50)){
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.BADGE_OTHER_BORROWED), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

        public void setListener(){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkStatus.checkNetworkStatus(ProfileActivity.this)) {
                        Bundle b = new Bundle();
                        b.putInt("ajdi", ks.getId());
                        Intent i = new Intent(ProfileActivity.this,BookActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtras(b);
                        startActivity(i);
                    } else {
                        Toast.makeText(ProfileActivity.this, getResources().getString(R.string.no_network), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static boolean checkImageResource(Context ctx, ImageView imageView,
                                             int imageResource) {
        boolean result = false;

        if (ctx != null && imageView != null && imageView.getDrawable() != null) {
            Drawable.ConstantState constantState;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                constantState = ctx.getResources()
                        .getDrawable(imageResource, ctx.getTheme())
                        .getConstantState();
            } else {
                constantState = ctx.getResources().getDrawable(imageResource)
                        .getConstantState();
            }

            if (imageView.getDrawable().getConstantState() == constantState) {
                result = true;
            }
        }

        return result;
    }


}
