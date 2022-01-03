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

    void agendarSincronizacaoPeriodicaNoticias();

    void notificarSincronizacao(SyncService service);

    void notificarSincronizacaoNoticias(SyncService service, int tarefaAtual, int totalTarefas);

    void notificarSincronizacaoSIGAA(SyncService service, Disciplina disciplina, int tarefaAtual, int totalTarefas);

    void notificarAvaliacaoNova(Avaliacao avaliacao, int idNotificacao);

    void notificarTarefaNova(Tarefa tarefa, int idNotificacao);

    void notificarQuestionarioNovo(Questionario questionario, int idNotificacao);
}
