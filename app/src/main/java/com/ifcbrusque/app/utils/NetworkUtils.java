package com.ifcbrusque.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public final class NetworkUtils {
    private NetworkUtils() {
    }

    /**
     * Utilizado para conferir se a internet está ligada (ativada)
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network network = cm.getActiveNetwork();
        NetworkCapabilities connection = cm.getNetworkCapabilities(network);

        return (connection != null) && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }
}
