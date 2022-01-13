package com.ifcbrusque.app.data.network.sigaa;

import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;
import com.stacked.sigaa_ifc.Usuario;

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
