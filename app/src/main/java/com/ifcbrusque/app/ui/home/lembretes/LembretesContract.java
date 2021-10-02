package com.ifcbrusque.app.ui.home.lembretes;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

import java.util.List;

public interface LembretesContract {
    interface LembretesView extends MvpView {
        void atualizarRecyclerView(List<Lembrete> lembretes);

        void atualizarCategoriaRecyclerView(int categoria);

        List<Lembrete> getLembretesNaView();
    }

    interface LembretesPresenter<V extends LembretesView> extends MvpPresenter<V> {
        void onViewPronta();

        void onLembreteInserido();

        void onAlternarEstadoLembreteClick(int position);

        void onExcluirLembreteClick(int position);

        void onCategoriaAlterada(int categoria);
    }
}