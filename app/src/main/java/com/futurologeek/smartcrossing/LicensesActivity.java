package com.futurologeek.smartcrossing;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class LicensesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        WebView view = new WebView(this);
        view.setBackgroundColor(0x00000000);
        if (Build.VERSION.SDK_INT >= 11) {
            view.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
        view.setVerticalScrollBarEnabled(false);
        ((LinearLayout) findViewById(R.id.mainlinearlayout)).addView(view);
        view.loadData(getString(R.string.licenses), "text/html; charset=utf-8", "utf-8");
    }
}