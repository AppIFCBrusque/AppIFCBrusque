package com.ifcbrusque.app.ui.home.lembretes;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

import java.util.List;

public interface LembretesContract {
    interface LembretesView extends MvpView {
        List<Lembrete> getLembretesNaView();

        void setLembretesNaView(List<Lembrete> lembretes);

        List<Object> getDadosNaView();

        void setDadosNaView(List<Object> dados);

        void atualizarCategoriaRecyclerView(int categoria);

        String getString(int resId);
    }

    interface LembretesPresenter<V extends LembretesView> extends MvpPresenter<V> {
        void onViewPronta();

        void onLembreteInserido();

        void onLembretesAtualizados();

        void onAlternarEstadoLembreteClick(int position);

        void onExcluirLembreteClick(int position);

        void onCategoriaAlterada(int categoria);
    }
}