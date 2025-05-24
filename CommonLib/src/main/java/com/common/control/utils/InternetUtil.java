package com.common.control.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class InternetUtil {
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network nw = connectivityManager.getActiveNetwork();
        if (nw == null) {
            return false;
        }
        NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
        if (actNw == null) {
            return false;
        }
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            return true;
        }
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
            return true;
        }
        // For other devices that are able to connect with Ethernet
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            return true;
        }
        // For checking internet over Bluetooth
        if (actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)) {
            return true;
        }
        return false;
    }
}
