package com.ifcbrusque.app.ui.home.sigaa.noticias;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class NoticiasSIGAAPresenter<V extends NoticiasSIGAAContract.NoticiasSIGAAView> extends BasePresenter<V> implements NoticiasSIGAAContract.NoticiasSIGAAPresenter<V> {
    @Inject
    public NoticiasSIGAAPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onViewPronta() {
        // Carregar as notícias do SIGAA salvas
        getCompositeDisposable().add(
                getDataManager()
                        .getAllNoticiasArmazenaveis()
                        .subscribe(
                                noticiasArmazenaveis -> getMvpView().setNoticiasArmazenaveis(noticiasArmazenaveis),
                                erro -> getMvpView().onError(erro.getMessage())
                        )
        );
    }
}
