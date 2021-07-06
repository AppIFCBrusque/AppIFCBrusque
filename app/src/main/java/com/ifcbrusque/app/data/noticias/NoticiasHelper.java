package com.ifcbrusque.app.data.noticias;

import android.content.Context;

import com.ifcbrusque.app.data.PreferencesHelper;
import com.ifcbrusque.app.data.noticias.classe.*;
import static com.ifcbrusque.app.data.noticias.NoticiasParser.*;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.*;



public class NoticiasHelper {
    final String url = "http://noticias.brusque.ifc.edu.br/category/noticias/page/";
    private OkHttpClient client;
    Context context;

    public NoticiasHelper(Context context) {
        client = new OkHttpClient();
        this.context = context;
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

    /*
    Salvar as imagens de preview no armazenamento interno
     */
    public Observable<String> salvarImagens(List<Preview> previews) {
        return Observable.just(previews.stream().map(o -> o.getUrlImagemPreview()).collect(toList()))
                .flatMapIterable(x -> x)
                .map(url -> {
                    return ImagemHelper.salvarImagemUrl(url, client, context);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
