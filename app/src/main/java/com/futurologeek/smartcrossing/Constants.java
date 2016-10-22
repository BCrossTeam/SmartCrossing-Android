package com.futurologeek.smartcrossing;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String bookshelf_url = "https://api.smartcrossing.pl/bookshelf/";
    public static final String auth_url = "https://api.smartcrossing.pl/user/auth/";
    public static final String book_url = "https://api.smartcrossing.pl/book/";
    public static final String user_url = "https://api.smartcrossing.pl/user/";
    public static final String gapi_url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:";
    public static final int distance = 100;
    public static final String request_url = "https://api.smartcrossing.pl/bookshelf/request/token/";
    public static final String request_vote_url_1 = "/bookshelf/request/";
    public static final String request_vote_url_2_approve = "/vote/approve";
    public static final String request_vote_url_2_disapprove = "/vote/disapprove";


    public static double maxConstant(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Constants.shared, Context.MODE_PRIVATE);
        Boolean isKM =  preferences.getBoolean("isKM",true);
        if(isKM){
            return distance;
        } else {
            return distance/1.6;
        }
    }

    public static final String search_url = "https://api.smartcrossing.pl/bookshelf/book/search/";
    public static final int uid = 8;
    public static final String shared = "com.futurologeek.smartcrossing";
    public static final String fileRoot = Environment.getExternalStorageDirectory() + File.separator + "smartcrossing" + File.separator;
    public static final int rank_places_before = 5;
    public static final String user_rank = "https://api.smartcrossing.pl/user/ranking";
    public static final String api_url = "https://api.smartcrossing.pl";
    public static final int C_TIMEOUT = 15 * 1000;
    public static final int R_TIMEOUT = 60 * 1000;

    //Validator - Login
    public static final String LOGIN_VALIDATOR_PATTERN = "[^\\w\\d\\.]";
    public static final TextValidator.RegexMode LOGIN_VALIDATOR_MODE = TextValidator.RegexMode.NotContains;
    public static final int LOGIN_VALIDATOR_MIN_LEN = 3;
    public static final int LOGIN_VALIDATOR_MAX_LEN = 20;

    //Validator - Email
    public static final String EMAIL_VALIDATOR_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9_%+-]+(\\.[a-zA-Z0-9_%+-]+)+";
    public static final TextValidator.RegexMode EMAIL_VALIDATOR_MODE = TextValidator.RegexMode.Equals;
    public static final int EMAIL_VALIDATOR_MIN_LEN = 3;
    public static final int EMAIL_VALIDATOR_MAX_LEN = 100;

    //Validator - Password
    public static final String PASSWORD_VALIDATOR_PATTERN = "\\s";
    public static final TextValidator.RegexMode PASSWORD_VALIDATOR_MODE = TextValidator.RegexMode.NotContains;
    public static final int PASSWORD_VALIDATOR_MIN_LEN = 6;
    public static final int PASSWORD_VALIDATOR_MAX_LEN = 20;

    //Validator - Username
    public static final String USERNAME_VALIDATOR_PATTERN = "";
    public static final TextValidator.RegexMode USERNAME_VALIDATOR_MODE = TextValidator.RegexMode.None;
    public static final int USERNAME_VALIDATOR_MIN_LEN = 3;
    public static final int USERNAME_VALIDATOR_MAX_LEN = 30;


}
