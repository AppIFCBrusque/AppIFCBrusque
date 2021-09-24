package com.ifcbrusque.app.ui.home.noticias;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.service.SyncService;
import com.ifcbrusque.app.ui.base.BasePresenter;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/*
Presenter dos previews (tela que você é levado ao clicar em "Notícias"), e não ao clicar para abrir em uma notícia
 */
public class NoticiasPresenter<V extends NoticiasContract.NoticiasView> extends BasePresenter<V> implements NoticiasContract.NoticiasPresenter<V> {
    private boolean mPodeCarregar;

    @Inject
    public NoticiasPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    private void carregarPreviewsArmazenados() {
        getCompositeDisposable().add(getDataManager()
                .getPreviewsArmazenados()
                .doOnNext(previews -> {
                    getMvpView().atualizarRecyclerView(previews);
                    int previewTopoRecyclerView = getDataManager().getPreviewTopoRecyclerViewNoticias();
                    getMvpView().setRecyclerViewPosition(previewTopoRecyclerView);
                }).subscribe());
    }

    private void carregarPagina(int pagina) {
        mPodeCarregar = false;
        getMvpView().mostrarProgressBar();

        getCompositeDisposable().add(getDataManager()
                .getPaginaNoticias(pagina)
                .flatMap(previews  -> getDataManager().armazenarPreviewsNovos(previews, false))
                .subscribe(previewsArmazenados -> {
                    mPodeCarregar = true;
                    getMvpView().esconderProgressBar();

                    getMvpView().atualizarRecyclerView(previewsArmazenados);
                }, erro -> {
                    mPodeCarregar = false;
                    getMvpView().esconderProgressBar();

                    //Retornar o valor da ultima página
                    int paginaAnterior = getDataManager().getUltimaPaginaAcessadaNoticias() - 1;
                    getDataManager().setUltimaPaginaAcessadaNoticias(paginaAnterior);

                    if (erro.getClass() == NoInternetException.class) {
                        //Sem internet
                        getMvpView().onError(R.string.erro_sem_internet);
                    } else {
                        //Página inexistente
                        getMvpView().onError(R.string.erro_ultima_pagina_noticias);
                    }
                }));
    }

    private void anexarDisposableDaSincronizacao() {
        //Se o serviço de sincronização estiver rodando e carregar novos previews, ele pode notificar este presenter para atualizar a recycler view
        SyncService.getObservable().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                getCompositeDisposable().add(d);
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                //Carregar os previews novos na recycler view
                if (integer == SyncService.OBSERVABLE_PREVIEWS_NOVOS) {
                    carregarPreviewsArmazenados();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                getMvpView().onError(e.getMessage());
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Override
    public void onViewPronta() {
        mPodeCarregar = true;

        carregarPreviewsArmazenados();
        carregarPagina(1);

        anexarDisposableDaSincronizacao();

        getDataManager().agendarSincronizacaoPeriodicaNoticias();
    }

    @Override
    public void onPause() {
        getDataManager().setPreviewTopoRecyclerViewNoticias(0);
    }

    @Override
    public void onDestroyView(int indexPreviewTopo) {
        getDataManager().setPreviewTopoRecyclerViewNoticias(indexPreviewTopo);
        super.onDetach();
    }

    @Override
    public void onFimRecyclerView() {
        if (mPodeCarregar) {
            int paginaParaCarregar = getDataManager().getUltimaPaginaAcessadaNoticias() + 1;
            getDataManager().setUltimaPaginaAcessadaNoticias(paginaParaCarregar);
            carregarPagina(paginaParaCarregar);
        }
    }
}