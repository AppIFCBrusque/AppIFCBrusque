package com.ifcbrusque.app.ui.noticia;

import android.os.Bundle;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface NoticiaContract {
    interface NoticiaView extends MvpView {
        void carregarHtmlWebView(String html);

        void mostrarLoading();

        void esconderLoading();
    }

    interface NoticiaPresenter<V extends NoticiaView> extends MvpPresenter<V> {
        void onViewPronta(Bundle bundle);
    }
}
