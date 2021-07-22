package com.ifcbrusque.app.fragments.noticias;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.ifcbrusque.app.network.PaginaNoticias;
import com.ifcbrusque.app.models.Preview;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
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
    private boolean isCarregandoPagina;
    private boolean atingiuPaginaFinal;

    private ArrayList<Preview> ultimosPreviewsCarregados;

    public NoticiasPresenter(View view, PreferencesHelper pref, AppDatabase db, PaginaNoticias campus) {
        this.view = view;
        this.pref = pref;
        this.db = db;
        this.campus = campus;

        isCarregandoPagina = false;
        atingiuPaginaFinal = false;
        previewsArmazenados = new ArrayList<>();
        ultimaPaginaAcessada = pref.getUltimaPaginaNoticias();

        ultimosPreviewsCarregados = null;

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
            }).subscribe();
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
    /*
    Quando sai do fragmento (manter posição atual)
     */
    void onDestroyView(int indexPreviewTopo) {
        pref.setPreviewTopo(indexPreviewTopo);
    }

    /*
    Quando clica novamente já neste fragmento (voltar ao topo)
     */
    void onPause() {
        pref.setPreviewTopo(0);
    }

    /*
    Utilizado pelo view para obter a próxima página quando chega no fim da recycler view ou próximo
     */
    void getProximaPaginaNoticias() {
        ultimaPaginaAcessada++;
        getPaginaNoticias(ultimaPaginaAcessada);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Utilizado por este presenter para obter alguma página específica de notícias
     */
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

                    if(carregou) {
                        if(ultimosPreviewsCarregados.size() > 0) { //Carregou previews
                            view.esconderProgressBar();
                            armazenarPreviewsNovos(ultimosPreviewsCarregados);
                        } else { //Chegou na última página
                            ultimaPaginaAcessada--;
                            atingiuPaginaFinal = true;

                            view.mostrarToast("ULTIMA PÁGINA"); //////////////////////////////////////////
                        }
                    } else { //Erro de conexão
                        view.mostrarToast("ERRO DE CONEXÃO NOTICIASPRESENTER"); //////////////////////////////////////////
                    }
                }).subscribe();
    }

    /*
    Utilizada para adicionar os previews NOVOS no banco de dados
     */
    private void armazenarPreviewsNovos(List<Preview> previews) {
        Completable.fromRunnable(() -> {
            previewsArmazenados = db.previewDao().getAll(); //Atualizar para o mais recente

            List<Preview> previewsNovos = new ArrayList<>();
            for (Preview p : previews) { //Procurar os previews não armazenados
                if (!previewsArmazenados.stream().filter(_p -> _p.getUrlNoticia().equals(p.getUrlNoticia())).findFirst().isPresent()) {
                    previewsNovos.add(p);
                }
            }

            if (previewsNovos.size() > 0) {
                db.previewDao().insertAll(previewsNovos);
                previewsArmazenados = db.previewDao().getAll();
            }

            pref.setUltimaPagina(ultimaPaginaAcessada); //Isso aqui é desnecessário pra quando carrega a primeira página além da primeira vez, mas não atrapalha
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    //TODO: Conferir se há algum erro que pode ocorrer no runnable, como não ter acesso ao banco de dados (?)
                }).doOnComplete(() -> view.atualizarRecyclerView(previewsArmazenados))
                .subscribe();
    }

    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void atualizarRecyclerView(List<Preview> preview);

        void setRecyclerViewPosition(int index);

        void esconderProgressBar();

        void mostrarProgressBar();

        void mostrarToast(String texto);
    }
}
