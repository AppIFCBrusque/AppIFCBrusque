package com.ifcbrusque.app.data.network;

import android.content.Context;

import com.ifcbrusque.app.data.network.model.NoInternetException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.ifcbrusque.app.utils.NetworkUtils.isNetworkConnected;

public class NetworkInterceptor implements Interceptor {
    private final Context mContext;

    public NetworkInterceptor(Context context) {
        mContext = context.getApplicationContext();
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        if (!isNetworkConnected(mContext)) throw new NoInternetException();

        Response response = chain.proceed(request);
        if (!response.isSuccessful()) throw new IOException("Não foi possível obter a página");

        return response;
    }
}
