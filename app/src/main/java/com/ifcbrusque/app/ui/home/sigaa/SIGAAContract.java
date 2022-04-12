package com.ifcbrusque.app.ui.home.sigaa;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface SIGAAContract {
    interface SIGAAView extends MvpView {
        void setNomeText(String string);

        void setCursoText(String string);

        void setAvatarSIGAA(String url);
    }

    interface SIGAAPresenter<V extends SIGAAView> extends MvpPresenter<V> {
        void onViewPronta();
    }
}
