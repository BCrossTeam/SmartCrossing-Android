package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView booklist;
    ArrayList<Place> listItems=new ArrayList<Place>();
    PlacesAdapter adapter;
    EditText searchEditText;
    TableRow settings;
    TableRow profile;
    final Activity activity = this;
    ImageView plus;
    TableRow mapview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        setListeners();
    }

    public void findViews(){
        booklist = (ListView) findViewById(R.id.book_list);
        settings = (TableRow) findViewById(R.id.settings_button);
        profile = (TableRow) findViewById(R.id.profile_button);
        plus = (ImageView) findViewById(R.id.add_button);
        mapview = (TableRow) findViewById(R.id.map_button);
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
    }
    public void setListeners(){
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_book_prompt);
                dialog.setTitle(MainActivity.this.getResources().getString(R.string.select_src));
                RelativeLayout takePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_a);
                RelativeLayout choosePhotoRelative = (RelativeLayout) dialog.findViewById(R.id.add_m);
                takePhotoRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentIntegrator integrator = new IntentIntegrator(activity);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                        dialog.dismiss();
                    }
                });
                searchEditText.setFocusable(true);

                choosePhotoRelative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, AddBookActivity.class);
                        startActivity(i);
                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });

        mapview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapActivity.class);
                Bundle koszyk = new Bundle();
                koszyk.putBoolean("isPoint",false);
                i.putExtras(koszyk);
                startActivity(i);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ustawienia", Toast.LENGTH_SHORT).show();
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });



        adapter=new PlacesAdapter(this, listItems);
        booklist.setAdapter(adapter);
        listItems.add(new Place("Wiśniowa",4, 51.0844578,17.0234934));
        listItems.add(new Place("Mazowiecka",8, 51.1070619,17.0481559));
        listItems.add(new Place("Gajowa",14,51.0924032,17.0384109));

        listItems.add(new Place("Stanisława",5, 51.0986663,17.0390955));
        listItems.add(new Place("Piękna",4,51.0849709,17.0522465));
        listItems.add(new Place("Kasztanowa",8,51.0844352,17.0138016));
        listItems.add(new Place("Powstańców",14,51.0893211,17.0138097));

        listItems.add(new Place("Orląt",8,51.1077513,17.0177393));
        listItems.add(new Place("Wyspiańskiego",14, 51.1071437,17.0588681));
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                String url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:"+result.getContents();
                Bundle koszyk = new Bundle();
                koszyk.putString("jurl", url);
                // Definiujemy cel
                Intent cel = new Intent(this, AddBookActivity.class);
                cel.putExtras(koszyk);
                // Wysyłamy
                startActivity(cel);
            }

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
