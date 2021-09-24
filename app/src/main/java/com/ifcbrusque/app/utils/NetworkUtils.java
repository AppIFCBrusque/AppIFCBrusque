package com.ifcbrusque.app.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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

    /**
     * Utilizado para conferir se é possível conectar com a internet (pode estar ativada, mas não conseguir conectar)
     */
    public static boolean isInternetAvailable() {
        try {
            int timeout = 1500;
            Socket sock = new Socket();
            InetSocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);

            sock.connect(sockaddr, timeout);
            sock.close();

            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
