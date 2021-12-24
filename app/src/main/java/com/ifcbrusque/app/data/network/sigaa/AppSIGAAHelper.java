package com.ifcbrusque.app.data.network.sigaa;

import android.content.Context;

import com.ifcbrusque.app.di.ApplicationContext;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
import com.stacked.sigaa_ifc.Sessao;
import com.stacked.sigaa_ifc.Usuario;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppSIGAAHelper implements SIGAAHelper {
    Sessao mSessao;

    @Inject
    public AppSIGAAHelper(@ApplicationContext Context context) {
        mSessao = new Sessao(context);
        //TODO: Utilizar o mesmo cliente? Precisaria arrumar os interceptors
    }

    @Override
    public Usuario getUsuarioSIGAA() {
        return mSessao.getUsuario();
    }

    @Override
    public Observable<Boolean> logarSIGAA(String usuario, String senha) {
        return Observable.defer(() -> {
            return Observable.just(mSessao.login(usuario, senha));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Nota>> getNotasDisciplina(Disciplina disciplina) {
        return Observable.defer(() -> {
            return Observable.just(mSessao.disciplinaPegarNotas(disciplina));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
