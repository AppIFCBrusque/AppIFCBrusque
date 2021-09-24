package com.ifcbrusque.app.data.network.noticias;

import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.model.NoInternetException;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ifcbrusque.app.data.network.noticias.PgNoticiasParser.*;

/*
Funções para obter as notícias da página de internet do campus
 */
public class AppPgNoticiasHelper implements PgNoticiasHelper {
    private final String urlBase = "http://noticias.brusque.ifc.edu.br/category/noticias/page/";

    private OkHttpClient mClient;

    @Inject
    public AppPgNoticiasHelper(OkHttpClient client) {
        mClient = client;
    }
    /*
    Função básica que solicita alguma página da internet e retorna a resposta
     */
    private Response GET(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        return mClient.newCall(request).execute();
    }

    /*
    Obtém a lista de notícias (previews) do site do campus
     */
    @Override
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return Observable.defer(() -> {
            Response r = GET(urlBase + numeroPagina);

            if(r != null) {
                return Observable.just(getObjetosPreview(r));
            } else {
                throw new IOException("Erro ao carregar a página");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    /*
    Obtém uma notícia do site do campus
     */
    @Override
    public Observable<Noticia> getNoticiaWeb(Preview preview) {
        return Observable.defer(() -> {
            Response r = GET(preview.getUrlNoticia());

            if(r != null) {
                return Observable.just(getObjetoNoticia(r, preview));
            } else {
                throw new IOException("Erro ao carregar a página");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
