package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

public class SignInActivity extends AppCompatActivity {
    TextView goToSignUpButton;
    TableRow signInRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViews();
        setListeners();
    }

    public void findViews(){
        goToSignUpButton = (TextView) findViewById(R.id.register_text_view);
        signInRow = (TableRow) findViewById(R.id.sign_in_sing_in_button);
    }
    public void setListeners(){
        goToSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp();
            }
        });

        signInRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });
    }
    public void goToSignUp(){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
        finish();
    }

    public void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
