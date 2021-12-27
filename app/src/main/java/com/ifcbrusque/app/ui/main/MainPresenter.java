package com.ifcbrusque.app.ui.main;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MainPresenter<V extends MainContract.MainView> extends BasePresenter<V> implements MainContract.MainPresenter<V> {
    @Inject
    public MainPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    @Override
    public void onViewPronta() {
        getDataManager().criarCanalNotificacoes();

        boolean primeiraInicializacao = getDataManager().getPrimeiraInicializacao();
        if(primeiraInicializacao) {
            getMvpView().abrirLogin();
        } else {
            getMvpView().abrirHome();
        }
        getMvpView().fecharActivity();
    }
}
