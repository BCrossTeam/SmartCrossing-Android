package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LoadingActivity extends AppCompatActivity {
    private Button signInButton, signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        findViews();
        setListeners();
    }

    public void findViews() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
    }

    public void setListeners() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            goToSignIn();
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });
    }

    public void goToSignIn() {
        Intent i = new Intent(this, SignInActivity.class);
            startActivity(i);
            //finish();
}

    public void goToSignUp(){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        //finish();
    }
}

