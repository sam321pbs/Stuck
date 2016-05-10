package com.example.sammengistu.stuck;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

public class NetworkStatus {

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showOffLineDialog(Context context){
        new AlertDialog.Builder(context)
            .setTitle("You are offline")
            .setMessage("Please connect to the internet")
            .setPositiveButton(android.R.string.ok, null)
            .show();
    }
}
