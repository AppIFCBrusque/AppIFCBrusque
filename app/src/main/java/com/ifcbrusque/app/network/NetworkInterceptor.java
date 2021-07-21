package com.ifcbrusque.app.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class NetworkInterceptor implements Interceptor {
    private Context context;

    public NetworkInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if(!isConnectionOn() || !isInternetAvailable()) throw new NoInternetException();

        Response response = chain.proceed(request);
        if(!response.isSuccessful()) throw new NoInternetException();

        return response;
    }

    /*
    Utilizado para conferir se a internet está ligada (ativada)
     */
    private boolean isConnectionOn() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network network = cm.getActiveNetwork();
        NetworkCapabilities connection = cm.getNetworkCapabilities(network);

        return (connection != null) && (connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    /*
    Utilizado para conferir se é possível conectar com a internet (pode estar ativada, mas não conseguir conectar)
     */
    private boolean isInternetAvailable() {
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
