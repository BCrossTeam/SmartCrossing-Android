package com.futurologeek.smartcrossing;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.google.api.client.json.Json;
import com.google.api.services.books.model.Volume;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ProtocolException;

import static com.futurologeek.smartcrossing.R.id.map;

public class AddPointActivity extends FragmentActivity {
    private GoogleMap mMap;
    double latitude;
    double longitude;
    Marker newMarker;
    double markLongi, markLati;
    boolean firstMarker = false;
    boolean firstPositionUpdate = true;
    private Button add_point_button;
    JSONObject ob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
        findViews();
        setListeners();

        if (isLocationPermission()) {
            //Todo: zadanie oparte na lokalizacji
            loadMap();
        } else {
            Toast.makeText(AddPointActivity.this, getResources().getString(R.string.l_permission), Toast.LENGTH_SHORT).show();
            requestPermission();
        }
    }
    void loadMap() {
        ((MapFragment) getFragmentManager().findFragmentById(map)).getMapAsync(new OnMapReadyCallback() {
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        if(firstMarker){
                            newMarker.remove();
                        }
                        newMarker = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .snippet(latLng.toString()));
                        markLati = latLng.latitude;
                        markLongi = latLng.longitude;

                        newMarker.setTitle(newMarker.getId());
                        add_point_button.setEnabled(true);
                        firstMarker = true;
                    }
                });
                final LatLng cordinates = new LatLng(latitude, longitude);
                   if (ActivityCompat.checkSelfPermission(AddPointActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddPointActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(Location location) {
                        if(firstPositionUpdate){
                            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(loc, 15);
                            mMap.animateCamera(yourLocation);
                            firstPositionUpdate = false;
                        }

                    }
                };
                mMap.setOnMyLocationChangeListener(myLocationChangeListener);
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {


                    }
                });
            }
        });
    }

    public void findViews(){
        add_point_button = (Button) findViewById(R.id.add_point_button);
    }

    public void setListeners(){
        add_point_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(AddPointActivity.this);
                dialog.setContentView(R.layout.bookshelf_request_popup);
                TextView tv_lati = (TextView) dialog.findViewById(R.id.latitude);
                Button addPointButton = (Button) dialog.findViewById(R.id.add_point_button);
                TextView tv_longi = (TextView) dialog.findViewById(R.id.longitude);
                final EditText bookshelfName = (EditText) dialog.findViewById(R.id.add_bookshelf_name);
                tv_lati.setText(String.valueOf(markLati));
                tv_longi.setText(String.valueOf(markLongi));
                addPointButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(bookshelfName.getText().toString().equals("")){
                            Toast.makeText(AddPointActivity.this, getResources().getString(R.string.bshelf_name_null), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Thread t = new Thread()
                            {
                                public void run()
                                {
                                    POSTHandler han = new POSTHandler();
                                    JSONObject par = new JSONObject();
                                    try {
                                        par.put("user_auth_token", UserInfo.token);
                                        par.put("bookshelf_latitude", markLati);
                                        par.put("bookshelf_longitude", markLongi);
                                        par.put("bookshelf_name", bookshelfName.getText().toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        ob = han.handlePOSTmethod("/bookshelf/", par, true);
                                    } catch (ProtocolException e) {
                                        e.printStackTrace();
                                    }
                                    AddPointActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(ob.has("error")){
                                                Toast.makeText(AddPointActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(AddPointActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }
                                    });
                                }
                            };
                            t.start();

                        }
                    }
                });
                dialog.setTitle(getResources().getString(R.string.b_add));
                dialog.show();
            }
        });
    }

    void requestPermission() {
        if (!isLocationPermission()) {
            ActivityCompat.requestPermissions(AddPointActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    69);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 69: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isLocationPermission()) {
                        if (ActivityCompat.checkSelfPermission(AddPointActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddPointActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        }
                        loadMap();
                        //Todo: pobieranie lokalizacji
                    } else {
                        Toast.makeText(AddPointActivity.this, this.getResources().getString(R.string.l_permission), Toast.LENGTH_LONG).show();
                    }
                }


            }}
    }

    boolean isLocationPermission() {
        if (ContextCompat.checkSelfPermission(AddPointActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

}
