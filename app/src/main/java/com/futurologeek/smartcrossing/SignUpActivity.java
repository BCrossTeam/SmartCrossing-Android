package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {
    TextView goToSignInButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        setListeners();
        }

    public void findViews(){
    goToSignInButton = (TextView) findViewById(R.id.have_an_acc_textview);
    }

    public void setListeners(){
    goToSignInButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goToSignIn();
        }
    });
    }

    public void goToSignIn(){
        Intent i = new Intent(this,SignInActivity.class);
        startActivity(i);
        finish();
    }
}
