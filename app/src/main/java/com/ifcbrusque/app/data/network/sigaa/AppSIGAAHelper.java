package com.ifcbrusque.app.data.network.sigaa;

import android.content.Context;

import com.ifcbrusque.app.di.ApplicationContext;
import com.imawa.sigaaforkotlin.SIGAA;
import com.imawa.sigaaforkotlin.entities.Avaliacao;
import com.imawa.sigaaforkotlin.entities.Disciplina;
import com.imawa.sigaaforkotlin.entities.Nota;
import com.imawa.sigaaforkotlin.entities.Noticia;
import com.imawa.sigaaforkotlin.entities.Questionario;
import com.imawa.sigaaforkotlin.entities.Tarefa;
import com.imawa.sigaaforkotlin.entities.Usuario;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class AppSIGAAHelper implements SIGAAHelper {
    private final SIGAA mSIGAA;

    @Inject
    public AppSIGAAHelper(@ApplicationContext Context context) {
        mSIGAA = new SIGAA(context);
    }

    @Override
    public Usuario getUsuarioSIGAA() {
        return mSIGAA.getUsuario();
    }

    @Override
    public Observable<Boolean> logarSIGAA(String usuario, String senha) {
        return Observable.defer(() -> Observable.just(mSIGAA.login(usuario, senha)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Noticia>> getNoticiasSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> Observable.just(mSIGAA.getNoticias(disciplina)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Nota>> getNotasDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> Observable.just(mSIGAA.getNotas(disciplina)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Avaliacao>> getAvaliacoesDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> {
            Timber.d("%s | Acessando a página de avaliações", disciplina.getNome());
            return Observable.just(mSIGAA.getAvaliacoes(disciplina));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Tarefa>> getTarefasDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> {
            Timber.d("%s | Acessando a página de tarefas", disciplina.getNome());
            return Observable.just(mSIGAA.getTarefas(disciplina));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<ArrayList<Questionario>> getQuestionariosDisciplinaSIGAA(Disciplina disciplina) {
        return Observable.defer(() -> {
            Timber.d("%s | Acessando a página de questionários", disciplina.getNome());
            return Observable.just(mSIGAA.getQuestionarios(disciplina));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
