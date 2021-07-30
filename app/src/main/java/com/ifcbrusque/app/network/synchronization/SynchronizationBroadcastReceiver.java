package com.ifcbrusque.app.network.synchronization;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

/*
Utilizado para iniciar o SynchronizationService
 */
public class SynchronizationBroadcastReceiver extends BroadcastReceiver {
    public static final String ATUALIZAR_NOTICIAS = "com.ifcbrusque.app.ATUALIZAR_NOTICIAS";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ATUALIZAR_NOTICIAS.equals(action)) {
            //Conferir a primeira página e notificar notícias novas
            Intent serviceIntent = new Intent(context, SynchronizationService.class);
            serviceIntent.putExtra(SynchronizationService.EXTRA_ATUALIZAR_NOTICIAS, true);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}