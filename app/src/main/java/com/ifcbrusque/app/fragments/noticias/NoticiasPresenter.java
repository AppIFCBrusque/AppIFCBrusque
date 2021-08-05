package com.ifcbrusque.app.fragments.noticias;

import android.util.Log;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.ifcbrusque.app.network.synchronization.SynchronizationService;
import com.ifcbrusque.app.models.PaginaNoticias;
import com.ifcbrusque.app.models.Preview;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/*
Presenter dos previews (tela que você é levado ao clicar em "Notícias"), e não ao clicar para abrir em uma notícia
 */
public class NoticiasPresenter {
    private View view;

    private PaginaNoticias campus;
    private AppDatabase db;
    private PreferencesHelper pref;

    private List<Preview> previewsArmazenados;
    private Integer ultimaPaginaAcessada;
    private ArrayList<Preview> ultimosPreviewsCarregados;
    private boolean isCarregandoPagina;
    private boolean atingiuPaginaFinal;

    private Disposable disposable;

    public NoticiasPresenter(View view, PreferencesHelper pref, AppDatabase db, PaginaNoticias campus) {
        //Inicializar variáveis
        this.view = view;
        this.pref = pref;
        this.db = db;
        this.campus = campus;

        isCarregandoPagina = false;
        atingiuPaginaFinal = false;
        previewsArmazenados = new ArrayList<>();
        ultimaPaginaAcessada = pref.getUltimaPaginaNoticias();
        ultimosPreviewsCarregados = null;

        //Carregar previews salvos no BD e conferir primeira página de notícias
        Completable.fromRunnable(() -> {
            previewsArmazenados = db.previewDao().getAll();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    //TODO
                }).doOnComplete(() -> {
            view.atualizarRecyclerView(previewsArmazenados);
            view.setRecyclerViewPosition(pref.getPreviewTopo());

            //Carregar primeira página
            if (previewsArmazenados.size() == 0) {
                //Carregar pela primeira vez
                ultimaPaginaAcessada = 1;
                getPaginaNoticias(ultimaPaginaAcessada);
            } else {
                //Carregar primeira página para ver se tem algo novo
                getPaginaNoticias(1);
            }

            view.definirSincronizacaoPeriodicaNoticias();
        }).subscribe();

        //Se o serviço de sincronização estiver rodando e carregar novos previews, ele pode notificar este presenter para atualizar a recycler view
        SynchronizationService.getObservable().subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                //Utilizado para não acumular observer (disposed quando a view é destruída)
                disposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                if(integer == SynchronizationService.OBSERVABLE_PREVIEWS_NOVOS) {
                    //Atualizar a recycler view
                    armazenarPreviewsNovos(new ArrayList<Preview>());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                //////////////////////////
            }

            @Override
            public void onComplete() {
                //////////////////////////
            }
        });
    }

    boolean isCarregandoPagina() {
        return isCarregandoPagina;
    }

    boolean atingiuPaginaFinal() {
        return atingiuPaginaFinal;
    }

    List<Preview> getPreviewsArmazenados() {
        return previewsArmazenados;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
    Quando sai do fragmento -> manter posição atual da recycler view, dar dispose no disposable do serviço de sincronização
     */
    void onDestroyView(int indexPreviewTopo) {
        pref.setPreviewTopo(indexPreviewTopo);

        //Dar dispose no disposable que observa o serviço de sincronização
        if(disposable != null) {
            disposable.dispose();
        }
    }

    /**
    Quando clica novamente já neste fragmento -> voltar ao topo da recycler view
     */
    void onPause() {
        pref.setPreviewTopo(0);
    }

    /**
    Utilizado pelo view para obter a próxima página quando chega no fim da recycler view ou próximo
     */
    void getProximaPaginaNoticias() {
        ultimaPaginaAcessada++;
        getPaginaNoticias(ultimaPaginaAcessada);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
    Utilizado por este presenter para obter alguma página específica de notícias
     No fim, armazena os previews novos e atualiza a recycler view
     */
    //TODO: Acho que dá para transformar a função do synchronizationservice em um observable static e utilizar no lugar disso (mesma coisa para o armazenarPreviewsNovos)
    private void getPaginaNoticias(int pagina) {
        isCarregandoPagina = true;
        ultimosPreviewsCarregados = null;
        view.mostrarProgressBar();

        Observable.defer(() -> {
            try {
                ultimosPreviewsCarregados = campus.getPaginaNoticias(pagina);
            } catch (IOException e) {
                /*
                Se acontecer algum erro, o ultimosPreviewsCarregados vai ser null
                 */
            } catch (ParseException e) {
                ////////////////////////
            }

            return (ultimosPreviewsCarregados != null) ? Observable.just(true) : Observable.just(false);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(carregou -> {
                    isCarregandoPagina = false;
                    view.esconderProgressBar();

                    if (carregou) {
                        if (ultimosPreviewsCarregados.size() > 0) { //Carregou previews
                            view.esconderProgressBar();
                            armazenarPreviewsNovos(ultimosPreviewsCarregados);
                        } else { //Chegou na última página
                            ultimaPaginaAcessada--;
                            atingiuPaginaFinal = true;

                            view.mostrarToast("ULTIMA PÁGINA"); //////////////////////////////////////////
                        }
                    } else { //Erro de conexão
                        ultimaPaginaAcessada--;
                        view.mostrarToast("ERRO DE CONEXÃO NOTICIASPRESENTER"); //////////////////////////////////////////
                    }
                }).subscribe();
    }

    /**
    Utilizada para adicionar os previews NOVOS no banco de dados
     No fim, atualiza a recycler view com os previews no armazenamento
     */
    private void armazenarPreviewsNovos(List<Preview> previews) {
        db.armazenarPreviewsNovos(previews, false)
                .doOnNext(previewsNoArmazenamento -> {
                    previewsArmazenados = previewsNoArmazenamento;
                })
                .doOnComplete(() -> {
                    pref.setUltimaPagina(ultimaPaginaAcessada); //Isso aqui é desnecessário pra quando carrega a primeira página além da primeira vez, mas não atrapalha
                    view.atualizarRecyclerView(previewsArmazenados);
                }).subscribe();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void atualizarRecyclerView(List<Preview> preview);

        void setRecyclerViewPosition(int index);

        void esconderProgressBar();

        void mostrarProgressBar();

        void mostrarToast(String texto);

        void definirSincronizacaoPeriodicaNoticias();
    }
}