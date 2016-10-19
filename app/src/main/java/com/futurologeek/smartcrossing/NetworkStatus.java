package com.futurologeek.smartcrossing;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkStatus {
    public static boolean checkNetworkStatus(final Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if( wifi.isConnected() || mobile.isConnected()  ) {
            return true;
        }else{
            return false;
        }
    }
}
