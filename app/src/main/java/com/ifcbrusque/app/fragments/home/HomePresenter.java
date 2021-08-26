package com.ifcbrusque.app.fragments.home;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter {
    private View view;
    private AppDatabase db;

    private List<Lembrete> lembretesArmazenados;

    public HomePresenter(View view, AppDatabase db) {
        //Iniciar variáveis
        this.view = view;
        this.db = db;

        lembretesArmazenados = new ArrayList<>();
        carregarLembretesArmazenados();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções deste presenter que podem ser utilizadas pela view
     */
    public List<Lembrete> getLembretesArmazenados() {
        return lembretesArmazenados;
    }

    /**
     * Utilizado pelo view para carregar os lembretes do banco de dados
     * Após carregar, atualiza a recycler view com os lembretes retornados
     */
    public void carregarLembretesArmazenados() {
        Completable.fromRunnable(() -> {
            lembretesArmazenados = db.lembreteDao().getAll();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    view.atualizarRecyclerView(lembretesArmazenados);
                }).subscribe();
    }

    /**
     * Utilizado pela view para definir um lembrete como completo
     * Depois de definir, atualiza a recycler view
     * @param lembrete lembrete para definir completo
     */
    public void completarLembrete(Lembrete lembrete) {
        Completable.fromRunnable(() -> {
            db.lembreteDao().alterarEstadoLembrete(lembrete.getId(), Lembrete.ESTADO_COMPLETO);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    carregarLembretesArmazenados();
                })
                .subscribe();
    }

    /**
     * Utilizado pela view para excluir um lembrete
     * Depois de definir, atualiza a recycler view
     * @param lembrete lembrete para excluir
     */
    public void excluirLembrete(Lembrete lembrete) {
        Completable.fromRunnable(() -> {
            db.lembreteDao().delete(lembrete);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    carregarLembretesArmazenados();
                })
                .subscribe();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Declarar métodos que serão utilizados por este presenter e definidos na view
     */
    public interface View {
        void atualizarRecyclerView(List<Lembrete> lembretes);

        void mostrarToast(String texto);
    }
}
