package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {
    TextView goToSignUpButton;
    TableRow signInRow;
    EditText emailView, passwordView;
    private TableRow loadingTableRow;
    private TextValidator.ValidText email;
    DBHandler db;
    private TextValidator.ValidText password;

    JSONObject ob;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        findViews();
        db  = new DBHandler(SignInActivity.this);

        if(db.giveArray(false).size()>0){
            Cursor cur = db.giveLastLogin();
            cur.moveToLast();
            String login = cur.getString(1);
            emailView.setText(login);
            cur.close();
        }

        email = new TextValidator.ValidText();
        password = new TextValidator.ValidText();
        //email.valid = TextValidator.validate(email, Constants.EMAIL_VALIDATOR_PATTERN, Constants.EMAIL_VALIDATOR_MODE, Constants.EMAIL_VALIDATOR_MIN_LEN, Constants.EMAIL_VALIDATOR_MAX_LEN);
        setListeners();
    }

    private class SignInActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView view, int i, KeyEvent keyEvent) {
            Boolean handled = false;
            if (i == EditorInfo.IME_ACTION_NEXT) {
                if (!email.valid) {
                    handled = true;
                    if (email.text.length() > 0) {
                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                    emailView.requestFocus();
                } else if (!password.valid) {
                    handled = true;
                    if (password.text.length() > 0) {
                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                    passwordView.requestFocus();
                } else {
                    handled = true;
                    signIN();
                }
            }
            return handled;
        }
    }


    public void findViews(){
        goToSignUpButton = (TextView) findViewById(R.id.register_text_view);
        signInRow = (TableRow) findViewById(R.id.sign_in_sing_in_button);
        emailView = (EditText) findViewById(R.id.sign_in_login_input);
        passwordView = (EditText) findViewById(R.id.sign_in_password_input);
        loadingTableRow = (TableRow) findViewById(R.id.loading_table_row);
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

                if (!email.valid) {
                    if (email.text.length() > 0) {

                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                    emailView.requestFocus();
                } else if (!password.valid) {

                    Toast.makeText(SignInActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    passwordView.requestFocus();
                } else {
                    signIN();
                }}
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String filtered = "";
                for (int i = start; i < end; i++) {
                    char character = source.charAt(i);
                    if (!Character.isWhitespace(character)) {
                        filtered += character;
                    }
                }

                return filtered;
            }
        };


        passwordView.setFilters(new InputFilter[] { filter });
        emailView.setFilters(new InputFilter[] { filter });

        emailView.addTextChangedListener(new TextValidator(emailView, email, Constants.EMAIL_VALIDATOR_PATTERN, Constants.EMAIL_VALIDATOR_MODE, Constants.EMAIL_VALIDATOR_MIN_LEN, Constants.EMAIL_VALIDATOR_MAX_LEN));

        emailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!email.valid && email.text.length() > 0) {

                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        passwordView.addTextChangedListener(new TextValidator(passwordView, password, Constants.PASSWORD_VALIDATOR_PATTERN, Constants.PASSWORD_VALIDATOR_MODE, Constants.PASSWORD_VALIDATOR_MIN_LEN, Constants.PASSWORD_VALIDATOR_MAX_LEN));

        passwordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!password.valid && password.text.length() > 0) {
                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        emailView.setOnEditorActionListener(new SignInActionListener());
        passwordView.setOnEditorActionListener(new SignInActionListener());



    }
    public void goToSignUp(){
        Intent i = new Intent(this, SignUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    public void signIN(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    POSTHandler han = new POSTHandler();
                    JSONObject par = new JSONObject();
                    try {
                        par.put("user_password",passwordView.getText().toString());
                        par.put("user_email", emailView.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ob = han.handlePOSTmethod("/user/sign",par, true);
                    SignInActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ob.has("error")) {
                                if(ob.has("sub_error")) {
                                    int sub_error = 0;
                                    try {
                                        sub_error = ob.getInt("sub_error");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    sub_error = sub_error*-1;
                                    try {
                                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.JUST_ERROR)+" "+ GetStringCode.getErrorResource(ob.getInt("error"), SignInActivity.this) + getResources().getString(R.string.ADDITIONAL_ERROR_INFO)+" "+ GetStringCode.getErrorResource(sub_error, SignInActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Toast.makeText(SignInActivity.this, getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), SignInActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //Toast.makeText(SignInActivity.this, signInPassword.getText().toString() + "   "  +signInLogin.getText().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    db.deleteAll();
                                    int id = ob.getInt("user_id");
                                    String tok = ob.getString("user_auth_token");
                                    setUserInfo(tok, id);
                                    db.addUserData(String.valueOf(id),tok);
                                    db.addRecord(emailView.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                goToMain();
                            }
                        }
                    });




                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    void setUserInfo(String token, int id){
        UserInfo.token = token;
        UserInfo.uid = id;
    }

    public void goToMain(){
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
