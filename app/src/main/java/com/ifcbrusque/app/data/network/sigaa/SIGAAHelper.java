package com.ifcbrusque.app.data.network.sigaa;

import com.winterhazel.sigaaforkotlin.entities.Avaliacao;
import com.winterhazel.sigaaforkotlin.entities.Disciplina;
import com.winterhazel.sigaaforkotlin.entities.Nota;
import com.winterhazel.sigaaforkotlin.entities.Noticia;
import com.winterhazel.sigaaforkotlin.entities.Questionario;
import com.winterhazel.sigaaforkotlin.entities.Tarefa;
import com.winterhazel.sigaaforkotlin.entities.Usuario;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;

public interface SIGAAHelper {
    Usuario getUsuarioSIGAA();

    Observable<Boolean> logarSIGAA(String usuario, String senha);

    Observable<ArrayList<Disciplina>> getAllDisciplinasSIGAA();

    Observable<ArrayList<Noticia>> getNoticiasSIGAA(Disciplina disciplina);

    Observable<ArrayList<Nota>> getNotasDisciplinaSIGAA(Disciplina disciplina);

    Observable<ArrayList<Avaliacao>> getAvaliacoesDisciplinaSIGAA(Disciplina disciplina);

    Observable<ArrayList<Tarefa>> getTarefasDisciplinaSIGAA(Disciplina disciplina);

    Observable<ArrayList<Questionario>> getQuestionariosDisciplinaSIGAA(Disciplina disciplina);
}
