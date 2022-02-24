package com.ifcbrusque.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ifcbrusque.app.data.db.AppDbHelper;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.notification.AppNotificationHelper;
import com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppDbHelper appDbHelper = new AppDbHelper(context);
        AppNotificationHelper appNotificationHelper = new AppNotificationHelper(context);

        Bundle bundle = intent.getExtras();
        appNotificationHelper.notificarLembrete(bundle); // Notificar o lembrete

        // Reagendar os lembretes com repetição para a próxima data
        int tipoRepeticao = bundle.getInt(InserirLembreteActivity.EXTRAS_LEMBRETE_TIPO_REPETICAO, Lembrete.REPETICAO_SEM);

        if (tipoRepeticao != Lembrete.REPETICAO_SEM) {
            long idLembrete = intent.getExtras().getLong(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, -1);
            appDbHelper.atualizarParaProximaDataLembreteComRepeticao(idLembrete)
                    .doOnNext(lembrete -> appNotificationHelper.agendarNotificacaoLembrete(lembrete))
                    .subscribe();
        }
    }
}