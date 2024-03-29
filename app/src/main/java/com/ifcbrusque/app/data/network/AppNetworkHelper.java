package com.ifcbrusque.app.data.network;

import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.noticias.AppPgNoticiasHelper;
import com.ifcbrusque.app.data.network.sigaa.AppSIGAAHelper;
import com.ifcbrusque.app.data.network.sigaa.SIGAAHelper;
import com.winterhazel.sigaaforkotlin.entities.Avaliacao;
import com.winterhazel.sigaaforkotlin.entities.Disciplina;
import com.winterhazel.sigaaforkotlin.entities.Nota;
import com.winterhazel.sigaaforkotlin.entities.Questionario;
import com.winterhazel.sigaaforkotlin.entities.Tarefa;
import com.winterhazel.sigaaforkotlin.entities.Usuario;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class AppNetworkHelper implements NetworkHelper {
    private final AppPgNoticiasHelper mPgNoticiasHelper;
    private final SIGAAHelper mSIGAAHelper;

    @Inject
    public AppNetworkHelper(AppPgNoticiasHelper noticiasHelper, AppSIGAAHelper SIGAAHelper) {
        mPgNoticiasHelper = noticiasHelper;
        mSIGAAHelper = SIGAAHelper;
    }

    @Override
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return mPgNoticiasHelper.getPaginaNoticias(numeroPagina);
    }

    @Override
    public Observable<Noticia> getNoticiaWeb(Preview preview) {
        return mPgNoticiasHelper.getNoticiaWeb(preview);
    }

    @Override
    public Usuario getUsuarioSIGAA() {
        return mSIGAAHelper.getUsuarioSIGAA();
    }

    @Override
    public Observable<Boolean> logarSIGAA(String usuario, String senha) {
        return mSIGAAHelper.logarSIGAA(usuario, senha);
    }

    @Override
    public Observable<ArrayList<Disciplina>> getAllDisciplinasSIGAA() {
        return mSIGAAHelper.getAllDisciplinasSIGAA();
    }

    @Override
    public Observable<ArrayList<com.winterhazel.sigaaforkotlin.entities.Noticia>> getNoticiasSIGAA(Disciplina disciplina) {
        return mSIGAAHelper.getNoticiasSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Nota>> getNotasDisciplinaSIGAA(Disciplina disciplina) {
        return mSIGAAHelper.getNotasDisciplinaSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Avaliacao>> getAvaliacoesDisciplinaSIGAA(Disciplina disciplina) {
        return mSIGAAHelper.getAvaliacoesDisciplinaSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Tarefa>> getTarefasDisciplinaSIGAA(Disciplina disciplina) {
        return mSIGAAHelper.getTarefasDisciplinaSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Questionario>> getQuestionariosDisciplinaSIGAA(Disciplina disciplina) {
        return mSIGAAHelper.getQuestionariosDisciplinaSIGAA(disciplina);
    }
}
