package com.futurologeek.smartcrossing;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BookshelfActivity extends FragmentActivity {
    private GoogleMap mMap;
    TextView name, tvBookCount;
    double longitude;
    double latitude;
    int bookcount;

    public static BookshelfActivity newInstance() {
        BookshelfActivity fragment = new BookshelfActivity();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        findViews();
        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            name.setText(przekazanedane.getString("name"));
            longitude = przekazanedane.getDouble("longitude");
            latitude = przekazanedane.getDouble("latitude");
            bookcount = przekazanedane.getInt("bookcount");
            tvBookCount.setText(this.getResources().getString(R.string.b_count)+" "+bookcount);

            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;
                    LatLng sydney = new LatLng(latitude, longitude);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
                    googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

                }
            });
        }
    }
    public void findViews(){
        name = (TextView) findViewById(R.id.name);
        tvBookCount = (TextView) findViewById(R.id.tv_book_count);
    }
}