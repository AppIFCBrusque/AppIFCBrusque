package com.ifcbrusque.app.data.notification;

import android.os.Bundle;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.service.SyncService;

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
}
