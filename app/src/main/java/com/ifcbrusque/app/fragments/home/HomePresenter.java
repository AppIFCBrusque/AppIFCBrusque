package com.ifcbrusque.app.fragments.home;

import com.ifcbrusque.app.data.AppDatabase;

public class HomePresenter {
    private View view;
    private AppDatabase db;

    public HomePresenter(View view, AppDatabase db) {
        this.view = view;
        this.db = db;
    }

    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void mostrarToast(String texto);
    }
}
