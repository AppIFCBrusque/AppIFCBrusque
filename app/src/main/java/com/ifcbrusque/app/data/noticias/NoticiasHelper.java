package com.ifcbrusque.app.data.noticias;

import com.ifcbrusque.app.data.PreferencesHelper;
import com.ifcbrusque.app.data.noticias.classe.*;
import static com.ifcbrusque.app.data.noticias.NoticiasParser.*;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.*;



public class NoticiasHelper {
    final String url = "http://noticias.brusque.ifc.edu.br/category/noticias/page/";
    private OkHttpClient client;

    public NoticiasHelper() {
        client = new OkHttpClient();
    }

    /*
    Usado para requests do tipo GET
     */
    private Response GET(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        return client.newCall(request).execute();
    }

    /*
    Retorna um observable com a lista de notícias (previews)
     */
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return Observable.defer(() -> {
            return Observable.just(objetosPreview(GET("http://noticias.brusque.ifc.edu.br/category/noticias/page/" + numeroPagina)));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*
    Retorna um Observable com uma notícia completa
     */
    public Observable<Noticia> getNoticia(Preview p) {
        return Observable.defer(() -> {
            return Observable.just(objetoNoticia(GET(p.getUrlNoticia()), p));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
