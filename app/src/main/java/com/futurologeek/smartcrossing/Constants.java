package com.futurologeek.smartcrossing;


import android.os.Environment;

import java.io.File;

public class Constants {
    public static final String bookshelf_url = "https://api.smartcrossing.pl/bookshelf/";
    public static final String book_url = "https://api.smartcrossing.pl/book/";
    public static final String user_url = "https://api.smartcrossing.pl/user/";
    public static final String gapi_url = "https://www.googleapis.com/books/v1/volumes?q=ISBN:";
    public static final int uid = 8;
    public static final String shared = "com.futurologeek.smartcrossing";
    public static final String fileRoot = Environment.getExternalStorageDirectory() + File.separator + "smartcrossing" + File.separator;
    public static final int rank_places_before = 5;
    public static final String user_rank = "https://api.smartcrossing.pl/user/ranking";
}
