package com.futurologeek.smartcrossing;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


public class AppController extends Application {
    private static AppController instance;
    public static boolean uiInForeground;

    public AppController(){
        instance = this;
    }

    public static Context getContext(){
        return instance;
    }

    public static void onResume() {
        uiInForeground = true;
    }

    public static void onPause() {
        uiInForeground = false;
    }

    public static SharedPreferences getSharedPreferences(){
        return instance.getSharedPreferences("com.futurologeek.challengetic", Context.MODE_PRIVATE);
    }

    public static void showToast(String msg, int length){
        if(uiInForeground){
            Toast.makeText(instance, msg, length).show();
        }
    }

    public static void showToast(int msg, int length){
        if(uiInForeground){
            Toast.makeText(instance, msg, length).show();
        }
    }
}