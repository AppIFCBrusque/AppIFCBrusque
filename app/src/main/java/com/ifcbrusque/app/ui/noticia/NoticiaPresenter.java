package com.ifcbrusque.app.ui.noticia;

import static com.ifcbrusque.app.data.db.Converters.fromTimestamp;
import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.NOTICIA_DATA;
import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.NOTICIA_HTML_CONTEUDO;
import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.NOTICIA_NOME_DISCIPLINA;
import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.NOTICIA_TITULO;
import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.NOTICIA_URL;
import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.NOTICIA_URL_IMAGEM_PREVIEW;

import android.os.Bundle;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.data.network.noticias.PgNoticiasParser;
import com.ifcbrusque.app.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;

public class NoticiaPresenter<V extends NoticiaContract.NoticiaView> extends BasePresenter<V> implements NoticiaContract.NoticiaPresenter<V> {
    @Inject
    public NoticiaPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    /**
     * Utilizado para carregar uma notícia (do banco de dados ou da internet), armazenar e mostrar na view
     */
    private void carregarNoticiaArmazenada(Preview preview) {
        Timber.i("Tentando carregar notícia armazenada");
        getCompositeDisposable().add(getDataManager()
                .getNoticia(preview.getUrlNoticia())
                .subscribe(noticia -> exibirNoticiaNaView(preview, noticia),
                        erro -> carregarNoticiaWeb(preview)));
    }

    private void carregarNoticiaWeb(Preview preview) {
        Timber.i("Carregando notícia da web");
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
        if (preview.getUrlImagemPreview().length() > 0) {
            getMvpView().loadImage(preview.getUrlImagemPreview());
        } else {
            getMvpView().hideImageView();
        }

        getMvpView().setTitulo(noticia.getTitulo());
        getMvpView().setData(noticia.getDataFormatada());
        getMvpView().loadHtmlWebView(PgNoticiasParser.formatarCorpoNoticia(preview, noticia.getHtmlConteudo()), getDataManager().getPrefTema());
        getMvpView().showView();
    }

    @Override
    public void onViewPronta(Bundle bundle) {
        String url = bundle.getString(NOTICIA_URL);

        if (url.length() > 0) {
            // Notícia do campus
            Timber.i("Notícia do campus");
            Preview preview = new Preview(bundle.getString(NOTICIA_TITULO),
                    "",
                    bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW),
                    bundle.getString(NOTICIA_URL),
                    fromTimestamp(bundle.getLong(NOTICIA_DATA)));
            carregarNoticiaArmazenada(preview);
        } else {
            // Notícia do SIGAA
            Timber.i("Notícia do SIGAA");
            getMvpView().setTitulo(bundle.getString(NOTICIA_TITULO));
            getMvpView().setData(bundle.getString(NOTICIA_DATA));
            getMvpView().setDisciplina(bundle.getString(NOTICIA_NOME_DISCIPLINA));
            getMvpView().showDisciplina();
            getMvpView().hideImageView();
            getMvpView().loadHtmlWebView(bundle.getString(NOTICIA_HTML_CONTEUDO), getDataManager().getPrefTema());
        }
    }
}
