package com.ifcbrusque.app.data.notification;

import android.os.Bundle;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.service.SyncService;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;

import java.util.List;

public interface NotificationHelper {
    void criarCanalNotificacoes();

    void notificarLembrete(Bundle bundle);

    void agendarNotificacaoLembrete(Lembrete lembrete);

    void agendarNotificacaoLembreteSeFuturo(Lembrete lembrete);

    void agendarNotificacoesLembretesFuturos(List<Lembrete> lembretes);

    void desagendarNotificacaoLembrete(Lembrete lembrete);

    void notificarNoticia(Preview preview, int idNotificacao);

    void iniciarSincronizacao();

    void agendarSincronizacao();

    void notificarSincronizacao(SyncService service);

    void notificarSincronizacaoNoticias(SyncService service, int tarefaAtual, int totalTarefas);

    void notificarSincronizacaoSIGAA(SyncService service, Disciplina disciplina, int tarefaAtual, int totalTarefas);

    void notificarAvaliacaoNova(Avaliacao avaliacao, Lembrete lembrete, int idNotificacao);

    void notificarAvaliacaoAlterada(Avaliacao avaliacao, Lembrete lembrete, int idNotificacao);

    void notificarTarefaNova(Tarefa tarefa, Lembrete lembrete, int idNotificacao);

    void notificarTarefaAlterada(Tarefa tarefa, Lembrete lembrete, int idNotificacao);

    void notificarQuestionarioNovo(Questionario questionario, Lembrete lembrete, int idNotificacao);

    void notificarQuestionarioAlterado(Questionario questionario, Lembrete lembrete, int idNotificacao);
}
