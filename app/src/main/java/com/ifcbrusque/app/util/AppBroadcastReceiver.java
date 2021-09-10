package com.ifcbrusque.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.helpers.DatabaseHelper;
import com.ifcbrusque.app.util.service.SynchronizationService;

import static com.ifcbrusque.app.util.helpers.NotificationHelper.agendarNotificacaoLembrete;
import static com.ifcbrusque.app.util.helpers.NotificationHelper.notificarLembrete;
import static com.ifcbrusque.app.util.helpers.NotificationHelper.notificarSincronizacao;

/*
Utilizado para receber comandos (enquanto o aplicativo está aberto ou fechado) e executar algo
 */
public class AppBroadcastReceiver extends BroadcastReceiver {
    public static final String NOTIFICAR_LEMBRETE = "com.ifcbrusque.app.NOTIFICAR_LEMBRETE";
    public static final String ATUALIZAR_NOTICIAS = "com.ifcbrusque.app.ATUALIZAR_NOTICIAS";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (NOTIFICAR_LEMBRETE.equals(action)) {
            //Notificar algum lembrete
            notificarLembrete(context, intent.getExtras());

            //Reagendar lembretes com repetição
            int tipoRepeticao = intent.getExtras().getInt(InserirLembreteActivity.EXTRAS_LEMBRETE_TIPO_REPETICAO, Lembrete.REPETICAO_SEM);
            if(tipoRepeticao != Lembrete.REPETICAO_SEM) {
                long idLembrete = intent.getExtras().getLong(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, -1);
                //Atualizar a data do lembrete salvo
                DatabaseHelper.atualizarParaProximaDataLembreteComRepeticao(context.getApplicationContext(), idLembrete)
                .doOnNext(lembrete -> {
                    //Agendar nova notificação
                    agendarNotificacaoLembrete(context, lembrete);
                })
                .subscribe();
            }
        } else if (ATUALIZAR_NOTICIAS.equals(action)) {
            //Conferir a primeira página do campus e notificar notícias novas
            Intent serviceIntent = new Intent(context, SynchronizationService.class);
            serviceIntent.putExtra(SynchronizationService.EXTRA_ATUALIZAR_NOTICIAS, true);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}