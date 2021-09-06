package com.ifcbrusque.app.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.ifcbrusque.app.util.service.SynchronizationService;

import static com.ifcbrusque.app.util.NotificationHelper.notificarLembrete;

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
        } else if (ATUALIZAR_NOTICIAS.equals(action)) {
            //Conferir a primeira página do campus e notificar notícias novas
            Intent serviceIntent = new Intent(context, SynchronizationService.class);
            serviceIntent.putExtra(SynchronizationService.EXTRA_ATUALIZAR_NOTICIAS, true);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}