package com.ifcbrusque.app.ui.noticia;

import android.os.Bundle;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface NoticiaContract {
    interface NoticiaView extends MvpView {
        void carregarHtmlWebView(String html, int idTema);

        void carregarImagemGrande(String url);

        void setTitulo(String titulo);

        void setData(String data);

        void mostrarView();
    }

    interface NoticiaPresenter<V extends NoticiaView> extends MvpPresenter<V> {
        void onViewPronta(Bundle bundle);
    }
}
