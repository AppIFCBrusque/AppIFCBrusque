package com.ifcbrusque.app.activities.noticia;

import android.os.Bundle;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.PaginaNoticias;
import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.Converters.*;
import static com.ifcbrusque.app.util.PaginaNoticiasHelper.*;

public class NoticiaPresenter  {
    private View view;

    private Preview preview;
    private Noticia noticia;

    private PaginaNoticias campus;
    private AppDatabase db;

    public NoticiaPresenter(NoticiaPresenter.View view, Bundle bundle, AppDatabase db, PaginaNoticias campus) {
        //Iniciar variáveis
        this.view = view;
        this.db = db;
        this.campus = campus;
        this.noticia = null;

        //Criar o Preview a partir das informações no bundle
        preview = new Preview(bundle.getString(NOTICIA_TITULO), "", bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW), bundle.getString(NOTICIA_URL), fromTimestamp(bundle.getLong(NOTICIA_DATA)));

        carregarNoticia();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas somente por este presenter
     */

    /**
    Utilizado para carregar uma notícia (do banco de dados ou da internet), armazenar e mostrar na view
     */
    private void carregarNoticia() {
        view.mostrarProgressBar();

        Observable.defer(() -> {
            noticia = db.noticiaDao().getNoticia(preview.getUrlNoticia()); //Consultar no banco de dados

            if(noticia == null) { //Não armazenada anteriormente -> obter da internet
                try {
                    noticia = campus.getNoticia(preview);
                } catch (IOException e) {
                    /*
                    Se acontecer algum erro, o noticia vai ser null
                    */
                }
                if(noticia != null) db.noticiaDao().insert(noticia); //Armazenar a notícia da internet
            }

            return (noticia != null) ? Observable.just(true) : Observable.just(false);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(carregou -> {
                    view.esconderProgressBar();

                    if(carregou) { //Carregou normalmente
                        view.carregarHtmlWebView(formatarCorpoNoticia(preview, noticia.getHtmlConteudo()));
                        view.esconderProgressBar();
                    } else { //Erro de conexão
                        view.mostrarToast("ERRO DE CONEXÃO NOTICIAPRESENTER"); //TODO: Arrumar esta parte
                    }
                }).subscribe();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Declarar métodos que serão utilizados por este presenter e definidos na view
     */
    public interface View {
        void carregarHtmlWebView(String html);

        void esconderProgressBar();

        void mostrarProgressBar();

        void mostrarToast(String texto);
    }
}
