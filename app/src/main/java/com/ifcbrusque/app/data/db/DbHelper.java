package com.ifcbrusque.app.data.db;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface DbHelper {
    Observable<List<Preview>> getPreviewsArmazenados();

    Observable<List<Preview>> armazenarPreviewsNovos(List<Preview> previews, boolean retornarPreviewsNovos);

    Observable<Noticia> getNoticia(String url);

    Observable<Long> inserirNoticia(Noticia noticia);

    Observable<Lembrete> getLembrete(long id);

    Observable<List<Lembrete>> getLembretesArmazenados();

    Observable<Long> inserirLembrete(Lembrete lembrete);

    Completable deletarLembrete(Lembrete lembrete);

    Completable atualizarLembrete(Lembrete lembrete);

    //TODO: Este método de baixo não é meio inutil?
    Completable alterarEstadoLembrete(long id, int novoEstado);

    Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(long idLembrete);

    Observable<List<Disciplina>> getAllDisciplinas();

    Observable<List<Disciplina>> getDisciplinas(String frontEndIdTurma);

    Completable inserirDisciplinas(List<Disciplina> disciplinas);

    Completable deletarDisciplina(Disciplina disciplina);

    Observable<List<Avaliacao>> getAllAvaliacoes();

    Completable inserirAvaliacoes(List<Avaliacao> avaliacoes);

    Completable deletarAvaliacao(Avaliacao avaliacao);

    Observable<List<Tarefa>> getAllTarefas();

    Completable inserirTarefas(List<Tarefa> tarefas);

    Completable deletarTarefa(Tarefa tarefa);

    Observable<List<Questionario>> getAllQuestionarios();

    Completable inserirQuestionarios(List<Questionario> questionarios);

    Completable deletarQuestionario(Questionario questionario);
}
