package com.ifcbrusque.app.ui.home.noticias;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.service.SyncService;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

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
        Timber.d("Carregando página: " + pagina);

        getCompositeDisposable().add(getDataManager()
                .getPaginaNoticias(pagina)
                .flatMap(previews -> getDataManager().armazenarPreviewsNovos(previews, false))
                .subscribe(previewsArmazenados -> {
                    mPodeCarregar = true;
                    getMvpView().esconderProgressBar();

                    getMvpView().atualizarRecyclerView(previewsArmazenados);

                    //Definir data da ultima atualização da primeira página
                    if (pagina == 1) {
                        getDataManager().setDataUltimaSincronizacaoAutomaticaNoticias(new Date());
                    }

                    //Atualizar a ultima página acessada
                    if (pagina > getDataManager().getUltimaPaginaAcessadaNoticias()) {
                        getDataManager().setUltimaPaginaAcessadaNoticias(pagina);
                    }

                    //Permitir que o serviço de sincronização notifique as notícias novas
                    if (getDataManager().getPrimeiraSincronizacaoNoticias()) {
                        getDataManager().setPrimeiraSincronizacaoNoticias(false);
                    }
                }, erro -> {
                    mPodeCarregar = false;
                    getMvpView().esconderProgressBar();

                    if (erro.getClass() == NoInternetException.class) {
                        //Sem internet
                        getMvpView().onError(R.string.erro_sem_internet);
                    } else {
                        //Página inexistente
                        getMvpView().onError(R.string.erro_carregar_pagina);
                    }
                }));
    }

    private void anexarDisposableDaSincronizacao() {
        //Se o serviço de sincronização estiver rodando e carregar novos previews, ele pode notificar este presenter para atualizar a recycler view
        SyncService.getObservable().subscribe(codigo -> {
            if (codigo == SyncService.OBSERVABLE_ATUALIZAR_RV_PREVIEWS) {
                carregarPreviewsArmazenados();
            }
        }, erro -> {
            /* Engolir erro */
        });
    }

    @Override
    public void onViewPronta() {
        mPodeCarregar = true;

        carregarPreviewsArmazenados();

        anexarDisposableDaSincronizacao();

        long minutosDesdeUltimaSync = TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - getDataManager().getDataUltimaSincronizacaoAutomaticaNoticias().getTime());
        Timber.d("Minutos desde a última sincronização: " + minutosDesdeUltimaSync);
        if (minutosDesdeUltimaSync >= 10) {
            carregarPagina(1);
        }
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
            carregarPagina(paginaParaCarregar);
        }
    }
}