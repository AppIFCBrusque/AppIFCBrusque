package com.ifcbrusque.app.ui.home.noticias;

import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

import java.util.List;

public interface NoticiasContract {
    interface NoticiasView extends MvpView {
        void atualizarRecyclerView(List<Preview> previews);

        void mostrarProgressBar();

        void esconderProgressBar();
    }

    interface NoticiasPresenter<V extends NoticiasView> extends MvpPresenter<V> {
        void onViewPronta();

        void onFimRecyclerView();
    }
}
