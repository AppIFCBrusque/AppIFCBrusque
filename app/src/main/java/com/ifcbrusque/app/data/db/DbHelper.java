package com.ifcbrusque.app.data.db;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.db.model.QuestionarioArmazenavel;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;
import com.imawa.sigaaforkotlin.entities.Avaliacao;
import com.imawa.sigaaforkotlin.entities.Disciplina;
import com.imawa.sigaaforkotlin.entities.Questionario;
import com.imawa.sigaaforkotlin.entities.Tarefa;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface DbHelper {
    Observable<List<Preview>> getPreviewsArmazenados();

    Observable<List<Preview>> armazenarPreviewsNovos(List<Preview> previews, boolean retornarPreviewsNovos);

    Observable<Noticia> getNoticia(String url);

    Observable<Long> inserirNoticia(Noticia noticia);

    Observable<Lembrete> getLembrete(long id);

    Observable<List<Lembrete>> getLembrete(Avaliacao avaliacao);

    Observable<List<Lembrete>> getLembrete(Tarefa tarefa);

    Observable<List<Lembrete>> getLembrete(Questionario questionario);

    Observable<List<Lembrete>> getLembretesArmazenados();

    Observable<Lembrete> inserirLembrete(Lembrete lembrete);

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

    Observable<Avaliacao> inserirAvaliacao(Avaliacao avaliacao);

    Observable<List<Avaliacao>> inserirAvaliacoes(List<Avaliacao> avaliacoes);

    Completable deletarAvaliacao(Avaliacao avaliacao);

    Observable<Integer> atualizarAvaliacao(Avaliacao avaliacao);

    Observable<TarefaArmazenavel> getTarefaArmazenavel(String id);

    Observable<TarefaArmazenavel> getTarefaArmazenavel(Lembrete lembrete);

    Observable<List<Tarefa>> getAllTarefas();

    Observable<Tarefa> inserirTarefa(Tarefa tarefa);

    Observable<List<Tarefa>> inserirTarefas(List<Tarefa> tarefas);

    Completable deletarTarefa(Tarefa tarefa);

    Observable<Integer> atualizarTarefa(Tarefa tarefa);

    Observable<List<Questionario>> getAllQuestionarios();

    Observable<Questionario> inserirQuestionario(Questionario questionario);

    Observable<List<Questionario>> inserirQuestionarios(List<Questionario> questionarios);

    Completable deletarQuestionario(Questionario questionario);

    Observable<Integer> atualizarQuestionario(Questionario questionario);

    Observable<QuestionarioArmazenavel> getQuestionarioArmazenavel(long id);

    Observable<QuestionarioArmazenavel> getQuestionarioArmazenavel(Lembrete lembrete);

    Completable deletarTudoSIGAA();
}
