package com.ifcbrusque.app.helpers.noticias;

import android.content.Context;
import android.graphics.Bitmap;

import com.ifcbrusque.app.helpers.ImagemHelper;
import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import static com.ifcbrusque.app.helpers.noticias.NoticiasParser.*;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.*;

import static com.ifcbrusque.app.helpers.ImagemHelper.*;

public class NoticiasHelper { //TODO: tornar isso static e revisar o codigo
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
    Armazena as imagens dos previews a partir de uma lista contendo os Preview

    Se armazenar corretamente, o nome da imagem armazenada
    Se não armazenar, retorna uma string vazia
     */
    public Observable<String> salvarImagens(List<Preview> previews, boolean overwrite) {
        return Observable.just(previews.stream().map(o -> o.getUrlImagemPreview()).collect(toList()))
                .flatMapIterable(x -> x)
                .map(url -> {
                    if(imagemFormatoAceito(url)) {
                        long tamanhoImagemArmazenada = tamanhoImagemArmazenada(context, url); //Se a imagem não existir, o valor é 0

                        if(tamanhoImagemArmazenada == 0 || overwrite) {
                            System.out.println("[NoticiasFragment] Baixando imagem: " + url);
                            byte[] bytesImagemBaixada = baixarImagem(url, client);

                            Bitmap bitmapImagemBaixada = byteParaBitmap(bytesImagemBaixada);
                            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmapImagemBaixada, 300);
                            byte[] bytesImagemRedimensionada = bitmapParaByte(bitmapRedimensionado, 100);

                            return armazenarImagem(context, bytesImagemRedimensionada, getNomeArmazenamentoImagem(url), overwrite);
                        }
                    }
                    return "";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
