package com.ifcbrusque.app.ui.noticia;

import android.os.Bundle;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface NoticiaContract {
    interface NoticiaView extends MvpView {
        void loadHtmlWebView(String html, int idTema);

        void loadImage(String url);

        void hideImageView();

        void setTitulo(String titulo);

        void setDisciplina(String disciplina);

        void showDisciplina();

        void setData(String data);

        void showView();
    }

    interface NoticiaPresenter<V extends NoticiaView> extends MvpPresenter<V> {
        void onViewPronta(Bundle bundle);
    }
}
