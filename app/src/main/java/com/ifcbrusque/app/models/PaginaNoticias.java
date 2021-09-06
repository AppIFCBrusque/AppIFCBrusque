package com.ifcbrusque.app.models;

import android.content.Context;
import com.ifcbrusque.app.util.network.NetworkInterceptor;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ifcbrusque.app.util.PaginaNoticiasHelper.*;

/*
Funções para obter as notícias da página de internet do campus
 */
public class PaginaNoticias {
    private final String urlBase = "http://noticias.brusque.ifc.edu.br/category/noticias/page/";

    private OkHttpClient client;

    public PaginaNoticias(Context context) {
        client = new OkHttpClient.Builder()
        .addInterceptor(new NetworkInterceptor(context))
        .build();
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
    Obtém a lista de notícias (previews) do site do campus

    Retorna null se acontecer algum erro
     */
    public ArrayList<Preview> getPaginaNoticias(int numeroPagina) throws IOException, ParseException {
        Response r = null;

        r = GET(urlBase + numeroPagina, client);

        return (r != null) ? getObjetosPreview(r) : null;
    }


    /*
    Obtém uma notícia do site do campus

    Retorna null se acontecer algum erro
     */
    public Noticia getNoticia(Preview preview) throws IOException {
        Response r = null;

        r = GET(preview.getUrlNoticia(), client);

        return (r != null) ? getObjetoNoticia(r, preview) : null;
    }
}
