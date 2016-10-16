package com.futurologeek.smartcrossing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {
    private WebView webview;
    private EditText number_edittext;
    private Button setwebviewbutton;
    private Button parseButton;
    private String creators = "";
    private String url = "http://api.androidhive.info/contacts/";
    private TextView titleTextView, authorTextView, dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        authorTextView= (TextView) findViewById(R.id.author_textview);
    }

}