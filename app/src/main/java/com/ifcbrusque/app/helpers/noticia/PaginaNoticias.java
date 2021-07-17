package com.ifcbrusque.app.helpers.noticia;

import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ifcbrusque.app.helpers.noticia.NoticiasParser.*;

/*
Funções para obter as notícias da página de internet do campus
 */
public class PaginaNoticias {
    private final String urlBase = "http://noticias.brusque.ifc.edu.br/category/noticias/page/";

    private OkHttpClient client;

    public PaginaNoticias() {
        client = new OkHttpClient();
    }

    public OkHttpClient getClient() {return client;}

    /*
    Função básica que solicita alguma página da internet e retorna a resposta
     */
    private Response GET(String url, OkHttpClient client) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        return client.newCall(request).execute();
    }

    /*
    Cria um observable para obter a lista de notícias (previews) do site do campus
     */
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return Observable.defer(() -> {
            return Observable.just(objetosPreview(GET(urlBase + numeroPagina, client)));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /*
    Cria um observable para obter uma notícia do site do campus
     */
    public Observable<Noticia> getNoticia(Preview preview) {
        return Observable.defer(() -> {
            return Observable.just(objetoNoticia(GET(preview.getUrlNoticia(), client), preview));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
