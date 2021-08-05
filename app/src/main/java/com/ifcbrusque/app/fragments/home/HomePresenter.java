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
        this.view = view;
        this.db = db;

        lembretesArmazenados = new ArrayList<>();
        carregarLembretesArmazenados();
    }

    public List<Lembrete> getLembretesArmazenados() {
        return lembretesArmazenados;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void carregarLembretesArmazenados() {
        Completable.fromRunnable(() -> {
            lembretesArmazenados = db.lembreteDao().getAll();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    view.atualizarRecyclerView(lembretesArmazenados);
                }).subscribe();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void mostrarToast(String texto);

        void atualizarRecyclerView(List<Lembrete> lembretes);
    }
}
