package com.futurologeek.smartcrossing;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity {

    private GoogleMap mMap;
    ArrayList<LatLng> wspolrzedne;
    boolean isPoint;
    double longitude;
    double latitude;

    public static MapActivity newInstance() {
        MapActivity fragment = new MapActivity();
        return fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        if (getIntent().getExtras() != null) {
            Bundle przekazanedane = getIntent().getExtras();
            isPoint = przekazanedane.getBoolean("isPoint");
            if(isPoint){
                longitude = przekazanedane.getDouble("longitude");
                latitude = przekazanedane.getDouble("latitude");
            }

        }

            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                public void onMapReady(GoogleMap googleMap) {
                    if (!isPoint) {
                        wspolrzedne = new ArrayList<LatLng>();
                        wspolrzedne.add(new LatLng(51.0844578, 17.0234934));
                        wspolrzedne.add(new LatLng(51.1070619, 17.0481559));
                        wspolrzedne.add(new LatLng(51.0924032, 17.0384109));
                        wspolrzedne.add(new LatLng(51.0986663, 17.0390955));
                        wspolrzedne.add(new LatLng(51.0849709, 17.0522465));
                        wspolrzedne.add(new LatLng(51.0844352, 17.0138016));
                        wspolrzedne.add(new LatLng(51.0893211, 17.0138097));
                        wspolrzedne.add(new LatLng(51.1077513, 17.0177393));
                        wspolrzedne.add(new LatLng(51.1071437, 17.0588681));
                        mMap = googleMap;
                        for (LatLng l : wspolrzedne) {
                            mMap.addMarker(new MarkerOptions().position(l).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(String.valueOf(l.latitude) + " " + String.valueOf(l.longitude)));
                        }


                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng marker : wspolrzedne) {
                                    builder.include(new MarkerOptions().position(marker).getPosition());
                                }
                                LatLngBounds bounds = builder.build();
                                int padding = 200; // offset from edges of the map in pixels
                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                mMap.animateCamera(cu);
                            }
                        });
                    } else {
                        mMap = googleMap;
                        final LatLng cordinates = new LatLng(latitude, longitude);
                        mMap.addMarker(new MarkerOptions().position(cordinates).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(String.valueOf(latitude) + " " + String.valueOf(longitude)));
                        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cordinates,15));
                                // Zoom in, animating the camera.
                                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                            }
                    });
                    }


                }
            });



    }

}