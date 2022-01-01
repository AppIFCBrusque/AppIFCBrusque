package com.ifcbrusque.app.data.network.sigaa;

import android.content.Context;

import com.ifcbrusque.app.di.ApplicationContext;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
import com.stacked.sigaa_ifc.Sessao;
import com.stacked.sigaa_ifc.Tarefa;
import com.stacked.sigaa_ifc.Usuario;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class AppSIGAAHelper implements SIGAAHelper {
    private Sessao mSessao;

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
        return Observable.defer(() -> Observable.just(mSessao.login(usuario, senha)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Tarefa>> getTarefasDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> {
            Timber.d("Acessando a página de tarefas: " + disciplina.getNome());
            return Observable.just(mSessao.disciplinaPegarTarefas(disciplina));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Avaliacao>> getAvaliacoesDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> {
            Timber.d("Acessando a página de avaliações: " + disciplina.getNome());
            return Observable.just(mSessao.disciplinaPegarAvaliacoes(disciplina));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Nota>> getNotasDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> Observable.just(mSessao.disciplinaPegarNotas(disciplina))).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
