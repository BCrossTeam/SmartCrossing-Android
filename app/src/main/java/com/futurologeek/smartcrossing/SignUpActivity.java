package com.futurologeek.smartcrossing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpActivity extends AppCompatActivity {
    public static SignUpActivity instance;
    private EditText emailView;
    private EditText emailConfirmationView;
    private EditText passwordView;
    private EditText passwordConfirmationView;
    private EditText usernameView;
    private TableRow registerButton;
    private TextValidator.ValidText email;
    private TextValidator.ValidText emailConfirmation;
    private TextValidator.ValidText password;
    private TextValidator.ValidText passwordConfirmation;
    private TextValidator.ValidText username;
    private boolean usernameEdited = false;
    private TableRow loadingTableRow;
    private RelativeLayout mainLinearLayout;
    TextView goToSignInButton;
    JSONObject ob;


    private class SignUpActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView view, int i, KeyEvent keyEvent) {
            Boolean handled = false;
            if (i == EditorInfo.IME_ACTION_NEXT) {
                if (!email.valid) {
                    handled = true;
                    if (email.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                    emailView.requestFocus();
                } else if (!emailConfirmation.valid) {
                    handled = true;
                    if (emailConfirmation.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                    emailConfirmationView.requestFocus();
                } else if (!emailConfirmation.text.equals(email.text)) {
                    handled = true;
                    if (email.text.length() > 0 && emailConfirmation.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_MATCH), Toast.LENGTH_LONG).show();
                    }
                    if (email.text.length() == 0) {
                        emailView.requestFocus();
                    } else {
                        emailConfirmationView.requestFocus();
                    }
                } else if (!password.valid) {
                    handled = true;
                    if (password.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                    passwordView.requestFocus();
                } else if (!passwordConfirmation.valid) {
                    handled = true;
                    if (passwordConfirmation.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                    passwordConfirmationView.requestFocus();
                } else if (!passwordConfirmation.text.equals(password.text)) {
                    handled = true;
                    if (password.text.length() > 0 && passwordConfirmation.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_MATCH), Toast.LENGTH_LONG).show();
                    }
                    if (password.text.length() == 0) {
                        passwordView.requestFocus();
                    } else {
                        passwordConfirmationView.requestFocus();
                    }
                } else if (!username.valid) {
                    handled = true;
                    if (username.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_USERNAME_INVALID), Toast.LENGTH_LONG).show();
                    }
                    usernameView.requestFocus();
                } else if (!usernameEdited) {
                    handled = true;
                    usernameEdited = true;
                    usernameView.requestFocus();
                } else if (emailConfirmation.text.equals(email.text) && passwordConfirmation.text.equals(password.text)) {
                    handled = true;
                    Toast.makeText(SignUpActivity.this, "succes", Toast.LENGTH_SHORT).show();
                }
            }
            return handled;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        findViews();
        setListeners();
        instance = this;
        email = new TextValidator.ValidText();
        emailConfirmation = new TextValidator.ValidText();
        password = new TextValidator.ValidText();
        passwordConfirmation = new TextValidator.ValidText();
        username = new TextValidator.ValidText();
        setupActivity();
    }

    public void setupActivity() {
        emailView = (EditText) findViewById(R.id.sign_up_email_input);
        emailConfirmationView = (EditText) findViewById(R.id.sign_up_email_confirmation_input);
        passwordView = (EditText) findViewById(R.id.sign_up_password_input);
        passwordConfirmationView = (EditText) findViewById(R.id.sign_up_password_confirmation_input);
        usernameView = (EditText) findViewById(R.id.sign_up_username_input);
        registerButton = (TableRow) findViewById(R.id.sign_up_button);
        emailView.setText(email.text);
        emailConfirmationView.setText(emailConfirmation.text);
        passwordView.setText(password.text);
        passwordConfirmationView.setText(passwordConfirmation.text);
        usernameView.setText(username.text);
        loadingTableRow = (TableRow) findViewById(R.id.loading_table_row);

        mainLinearLayout = (RelativeLayout) findViewById(R.id.mainlinearlayout);

        mainLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                return false;
            }
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

        passwordConfirmationView.setFilters(new InputFilter[]{filter});
        passwordView.setFilters(new InputFilter[]{filter});
        emailView.setFilters(new InputFilter[]{filter});
        emailConfirmationView.setFilters(new InputFilter[]{filter});


        //Email
        emailView.addTextChangedListener(new TextValidator(emailView, email, Constants.EMAIL_VALIDATOR_PATTERN, Constants.EMAIL_VALIDATOR_MODE, Constants.EMAIL_VALIDATOR_MIN_LEN, Constants.EMAIL_VALIDATOR_MAX_LEN));

        emailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!email.valid && email.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        emailConfirmationView.addTextChangedListener(new TextValidator(emailConfirmationView, emailConfirmation, Constants.EMAIL_VALIDATOR_PATTERN, Constants.EMAIL_VALIDATOR_MODE, Constants.EMAIL_VALIDATOR_MIN_LEN, Constants.EMAIL_VALIDATOR_MAX_LEN));

        emailConfirmationView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!emailConfirmation.valid) {
                        if (emailConfirmation.text.length() > 0) {
                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                        }
                    } else if (!emailConfirmation.text.equals(email.text)) {
                        if (emailConfirmation.text.length() > 0) {
                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_MATCH), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        //Password
        passwordView.addTextChangedListener(new TextValidator(passwordView, password, Constants.PASSWORD_VALIDATOR_PATTERN, Constants.PASSWORD_VALIDATOR_MODE, Constants.PASSWORD_VALIDATOR_MIN_LEN, Constants.PASSWORD_VALIDATOR_MAX_LEN));

        passwordView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!password.valid && password.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        passwordConfirmationView.addTextChangedListener(new TextValidator(passwordConfirmationView, passwordConfirmation, Constants.PASSWORD_VALIDATOR_PATTERN, Constants.PASSWORD_VALIDATOR_MODE, Constants.PASSWORD_VALIDATOR_MIN_LEN, Constants.PASSWORD_VALIDATOR_MAX_LEN));

        passwordConfirmationView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!passwordConfirmation.valid) {
                        if (passwordConfirmation.text.length() > 0) {
                            Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                        }

                    } else if (!passwordConfirmation.text.equals(password.text)) {
                        if (passwordConfirmation.text.length() > 0) {

                        }
                    }
                }
            }
        });

        //Username
        usernameView.addTextChangedListener(new TextValidator(usernameView, username, Constants.USERNAME_VALIDATOR_PATTERN, Constants.USERNAME_VALIDATOR_MODE, Constants.USERNAME_VALIDATOR_MIN_LEN, Constants.USERNAME_VALIDATOR_MAX_LEN));

        usernameView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isFocused()) {
                    if (!username.valid && username.text.length() > 0) {
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_USERNAME_INVALID), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        emailView.setOnEditorActionListener(new SignUpActionListener());
        emailConfirmationView.setOnEditorActionListener(new SignUpActionListener());
        passwordView.setOnEditorActionListener(new SignUpActionListener());
        passwordConfirmationView.setOnEditorActionListener(new SignUpActionListener());
        usernameView.setOnEditorActionListener(new SignUpActionListener());

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!email.valid) {
                    if (email.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                    emailView.requestFocus();
                } else if (!emailConfirmation.valid) {
                    if (emailConfirmation.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_INVALID), Toast.LENGTH_LONG).show();
                    }
                    emailConfirmationView.requestFocus();
                } else if (!emailConfirmation.text.equals(email.text)) {
                    if (email.text.length() > 0 && emailConfirmation.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_EMAIL_MATCH), Toast.LENGTH_LONG).show();
                    }
                    if (email.text.length() == 0) {

                        emailView.requestFocus();

                    } else {
                        emailConfirmationView.requestFocus();


                    }
                } else if (!password.valid) {
                    if (password.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                    passwordView.requestFocus();
                } else if (!passwordConfirmation.valid) {
                    if (passwordConfirmation.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_INVALID), Toast.LENGTH_LONG).show();
                    }
                    passwordConfirmationView.requestFocus();
                } else if (!passwordConfirmation.text.equals(password.text)) {
                    if (password.text.length() > 0 && passwordConfirmation.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_PASSWORD_MATCH), Toast.LENGTH_LONG).show();
                    }
                    if (password.text.length() == 0) {
                        passwordView.requestFocus();

                    } else {
                        passwordConfirmationView.requestFocus();

                    }
                } else if (!username.valid) {
                    if (username.text.length() > 0) {

                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.ERROR_USERNAME_INVALID), Toast.LENGTH_LONG).show();
                    }
                    usernameView.requestFocus();
                } else if (!usernameEdited) {
                    usernameEdited = true;
                    signUP();
                } else if (emailConfirmation.text.equals(email.text) && passwordConfirmation.text.equals(password.text)) {
                    signUP();
                }

            }
        });
    }

    public void signUP() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    POSTHandler han = new POSTHandler();
                    JSONObject par = new JSONObject();
                    try {
                        par.put("user_password", passwordView.getText().toString());
                        par.put("user_email", emailView.getText().toString());
                        par.put("user_name",usernameView.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ob = han.handlePOSTmethod("/user", par, true);
                    SignUpActivity.this.runOnUiThread(new Runnable() {
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
                                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.JUST_ERROR)+" "+ GetStringCode.getErrorResource(ob.getInt("error"), SignUpActivity.this) + getResources().getString(R.string.ADDITIONAL_ERROR_INFO)+" "+ GetStringCode.getErrorResource(sub_error, SignUpActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.JUST_ERROR) + " " + GetStringCode.getErrorResource(ob.getInt("error"), SignUpActivity.this), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                //Toast.makeText(SignUpActivity.this, signInPassword.getText().toString() + "   "  +signInLogin.getText().toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    Toast.makeText(SignUpActivity.this, GetStringCode.getSuccessCode(ob.getInt("success"), SignUpActivity.this), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(i);
                                finish();
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
