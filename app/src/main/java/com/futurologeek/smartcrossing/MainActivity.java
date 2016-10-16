package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    ListView booklist;
    ArrayList<Place> listItems=new ArrayList<Place>();
    PlacesAdapter adapter;
    TableRow settings;
    TableRow profile;
    final Activity activity = this;
    ImageView plus;

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
    }
    public void setListeners(){
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast.makeText(MainActivity.this, "Dialog", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Profil", Toast.LENGTH_SHORT).show();
            }
        });

        Place plejs = new Place("Trzemeska", 5);
        adapter=new PlacesAdapter(this, listItems);
        booklist.setAdapter(adapter);
        listItems.add(new Place("Trzemeska",5));
        listItems.add(new Place("Wiśniowa",4));
        listItems.add(new Place("Mazowiecka",8));
        listItems.add(new Place("Gajowa",14));

        listItems.add(new Place("Stanisława",5));
        listItems.add(new Place("Piękna",4));
        listItems.add(new Place("Kasztanowa",8));
        listItems.add(new Place("Powstańców",14));

        listItems.add(new Place("3 maja",5));
        listItems.add(new Place("Karmazynowa",4));
        listItems.add(new Place("Orląt",8));
        listItems.add(new Place("Wyspiańskiego",14));

        listItems.add(new Place("Niemcewicza",5));
        listItems.add(new Place("Norwida",4));
        listItems.add(new Place("Grabiszyńska",8));
        listItems.add(new Place("Tęczowa",14));

        adapter.notifyDataSetChanged();

    }


}
