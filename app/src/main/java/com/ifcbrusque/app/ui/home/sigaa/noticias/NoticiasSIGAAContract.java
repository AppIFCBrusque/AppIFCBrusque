package com.ifcbrusque.app.ui.home.sigaa.noticias;

import com.ifcbrusque.app.data.db.model.NoticiaArmazenavel;
import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

import java.util.List;

public interface NoticiasSIGAAContract {
    interface NoticiasSIGAAView extends MvpView {
        void setNoticiasArmazenaveis(List<NoticiaArmazenavel> noticiasArmazenaveis);
    }

    interface NoticiasSIGAAPresenter<V extends NoticiasSIGAAView> extends MvpPresenter<V> {
        void onViewPronta();
    }
}
