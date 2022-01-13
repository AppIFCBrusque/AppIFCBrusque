package com.ifcbrusque.app.data.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ifcbrusque.app.data.db.AppDbHelper;
import com.ifcbrusque.app.data.db.DbHelper;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_NOTIFICAR_LEMBRETE = "com.ifcbrusque.app.ACTION_NOTIFICAR_LEMBRETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_NOTIFICAR_LEMBRETE.equals(action)) {
            NotificationHelper notf = new AppNotificationHelper(context);
            DbHelper db = new AppDbHelper(context);

            //Notificar algum lembrete
            notf.notificarLembrete(intent.getExtras());

            //Reagendar lembretes com repetição
            int tipoRepeticao = intent.getExtras().getInt(InserirLembreteActivity.EXTRAS_LEMBRETE_TIPO_REPETICAO, Lembrete.REPETICAO_SEM);
            if (tipoRepeticao != Lembrete.REPETICAO_SEM) {
                long idLembrete = intent.getExtras().getLong(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, -1);
                //Atualizar a data do lembrete salvo
                db.atualizarParaProximaDataLembreteComRepeticao(idLembrete)
                        .doOnNext(lembrete -> {
                            //Agendar nova notificação
                            notf.agendarNotificacaoLembrete(lembrete);
                        })
                        .subscribe();
            }
        }
    }
}