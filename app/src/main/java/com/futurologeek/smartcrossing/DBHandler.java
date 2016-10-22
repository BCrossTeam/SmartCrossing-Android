package com.futurologeek.smartcrossing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static android.R.attr.id;


public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;


    public DBHandler(Context context) {
        super(context, "user_data.db", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table tokeny(" + "nr integer primary key autoincrement," + "uid text," + "auth_token text);" + "");
        db.execSQL(
                "create table loginy(" + "nr integer primary key autoincrement," + "login text);" + "");
    }

    public void addUserData(String authToken, String uid) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("nr", 1);
        data.put("uid", uid);
        data.put("auth_token", authToken);
        db.insertOrThrow("tokeny", null, data);
    }

    public void addRecord(String login) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("login", login);
        db.insertOrThrow("loginy", null, data);
    }

    public Cursor giveAll() {
        String[] columns = {"nr", "uid", "auth_token"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query("tokeny", columns, null, null, null, null, null);
        //cur.close();
        return cur;
    }


    public Cursor giveLastLogin() {
        String[] columns = {"nr", "login"};
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query("loginy", columns, null, null, null, null, null);
        return cur;

    }

    public ArrayList<String> giveArray(Boolean isToken) {
        ArrayList<String> ar = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        if(isToken){
            String[] columns = {"nr", "uid", "auth_token"};
            Cursor cur = db.query("tokeny", columns, null, null, null, null, null);
            while(cur.moveToNext()){
                ar.add(cur.getString(2));
            }
        } else {
            String[] columns = {"nr", "login"};
            Cursor cur = db.query("loginy", columns, null, null, null, null, null);
            while(cur.moveToNext()){
                ar.add(cur.getString(1));
            }
        }
        return ar;
    }


    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + "tokeny");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void removeRecord(int ajdi) {
        SQLiteDatabase db = getWritableDatabase();
        String[] arg = {"" + ajdi};
        db.delete("telefony", "nr=?", arg);
    }

    public void updateRecord(int nr, String uid, String authToken) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put("uid", uid);
        data.put("auth_token", authToken);
        String[] arg = {"" + nr};
        db.update("tokeny", data, "nr=?", arg);
    }

    public String getToken(int nr) {
        SQLiteDatabase db = getReadableDatabase();
        String[] kolumny = {"nr", "uid", "auth_token"};
        String args[] = {nr + ""};
        Cursor kursor = db.query("tokeny", kolumny, " nr=?", args, null, null, null, null);
        String tok = "";
        if (kursor != null) {
            kursor.moveToFirst();
            tok = kursor.getString(1);
        }
        return tok;
    }

    public int getId(int nr) {
        SQLiteDatabase db = getReadableDatabase();
        String[] kolumny = {"nr", "uid", "auth_token"};
        String args[] = {nr + ""};
        Cursor kursor = db.query("tokeny", kolumny, " nr=?", args, null, null, null, null);
        String tok = "";
        if (kursor != null) {
            kursor.moveToFirst();
            tok = kursor.getString(2);
        }
        return Integer.parseInt(tok);
    }
    }
