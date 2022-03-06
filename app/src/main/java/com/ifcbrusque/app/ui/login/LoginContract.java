package com.ifcbrusque.app.ui.login;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface LoginContract {
    interface LoginView extends MvpView {

        void desativarBotaoEntrar();

        void ativarBotaoEntrar();

        void esconderBotaoPular();

        void mostrarLoading();

        void esconderLoading();

        void mostrarVoltar();

        void mostrarMensagemErro();

        void setMensagemErro(int resid);

        void abrirHome();

        void fecharActivity();
    }

    interface LoginPresenter<V extends LoginView> extends MvpPresenter<V> {
        void onEntrarClick(String usuario, String senha);

        void onPularClick();
    }
}
