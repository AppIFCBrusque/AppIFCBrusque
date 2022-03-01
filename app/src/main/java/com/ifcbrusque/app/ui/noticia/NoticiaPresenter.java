package com.ifcbrusque.app.ui.noticia;

import android.os.Bundle;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.data.network.noticias.PgNoticiasParser;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.db.Converters.*;
import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA;

public class NoticiaPresenter<V extends NoticiaContract.NoticiaView> extends BasePresenter<V> implements NoticiaContract.NoticiaPresenter<V> {
    @Inject
    public NoticiaPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas somente por este presenter
     */

    /**
     * Utilizado para carregar uma notícia (do banco de dados ou da internet), armazenar e mostrar na view
     */
    private void carregarNoticiaArmazenada(Preview preview) {
        getCompositeDisposable().add(getDataManager()
                .getNoticia(preview.getUrlNoticia())
                .subscribe(noticia -> exibirNoticiaNaView(preview, noticia),
                        erro -> carregarNoticiaWeb(preview)));
    }

    private void carregarNoticiaWeb(Preview preview) {
        getCompositeDisposable().add(getDataManager()
                .getNoticiaWeb(preview)
                .flatMap(noticia -> {
                    exibirNoticiaNaView(preview, noticia);
                    return getDataManager().inserirNoticia(noticia);
                })
                .subscribe(id -> {

                        },
                        erro -> {
                            if (erro.getClass() == NoInternetException.class) {
                                getMvpView().onError(R.string.erro_sem_internet);
                            } else {
                                getMvpView().onError(R.string.erro_carregar_pagina);
                            }
                        }));
    }

    private void exibirNoticiaNaView(Preview preview, Noticia noticia) {
        getMvpView().carregarImagemGrande(preview.getUrlImagemPreview());

        getMvpView().setTitulo(noticia.getTitulo());
        getMvpView().setData(noticia.getDataFormatada());

        getMvpView().carregarHtmlWebView(PgNoticiasParser.formatarCorpoNoticia(preview, noticia.getHtmlConteudo()), getDataManager().getPrefTema());

        getMvpView().mostrarView();
    }

    @Override
    public void onViewPronta(Bundle bundle) {
        //Criar o Preview a partir das informações no bundle
        Preview preview = new Preview(bundle.getString(NOTICIA_TITULO),
                "",
                bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW),
                bundle.getString(NOTICIA_URL),
                fromTimestamp(bundle.getLong(NOTICIA_DATA)));

        carregarNoticiaArmazenada(preview);
    }
}
