package com.ifcbrusque.app.ui.main;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface MainContract {
    interface MainView extends MvpView {
        void abrirLogin();

        void abrirHome();

        void fecharActivity();
    }

    interface MainPresenter<V extends MainView> extends MvpPresenter<V> {
        void onViewPronta();
    }
}
