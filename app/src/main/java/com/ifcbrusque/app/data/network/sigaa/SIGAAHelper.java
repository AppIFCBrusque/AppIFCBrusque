package com.ifcbrusque.app.data.network.sigaa;

import com.imawa.sigaaforkotlin.entities.Avaliacao;
import com.imawa.sigaaforkotlin.entities.Disciplina;
import com.imawa.sigaaforkotlin.entities.Nota;
import com.imawa.sigaaforkotlin.entities.Questionario;
import com.imawa.sigaaforkotlin.entities.Tarefa;
import com.imawa.sigaaforkotlin.entities.Usuario;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;

public interface SIGAAHelper {
    Usuario getUsuarioSIGAA();

    Observable<Boolean> logarSIGAA(String usuario, String senha);

    Observable<ArrayList<Nota>> getNotasDisciplinaSIGAA(Disciplina disciplina);

    Observable<ArrayList<Avaliacao>> getAvaliacoesDisciplinaSIGAA(Disciplina disciplina);

    Observable<ArrayList<Tarefa>> getTarefasDisciplinaSIGAA(Disciplina disciplina);

    Observable<ArrayList<Questionario>> getQuestionariosDisciplinaSIGAA(Disciplina disciplina);
}
