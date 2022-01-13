package com.ifcbrusque.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class SyncReceiver extends BroadcastReceiver {
    public static final String ACTION_SINCRONIZACAO_COMPLETA = "com.ifcbrusque.app.ACTION_SINCRONIZACAO_COMPLETA";
    public static final String ACTION_SINCRONIZACAO_RAPIDA = "com.ifcbrusque.app.ACTION_SINCRONIZACAO_RAPIDA";
    public static final String ACTION_FINALIZAR_SERVICO = "com.ifcbrusque.app.ACTION_FINALIZAR_SERVICO";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Intent serviceIntent;
        switch (action) {
            case ACTION_SINCRONIZACAO_COMPLETA:
                serviceIntent = SyncService.getStartIntent(context, false);
                context.stopService(serviceIntent);
                ContextCompat.startForegroundService(context, serviceIntent);
                break;

            case ACTION_SINCRONIZACAO_RAPIDA:
                serviceIntent = SyncService.getStartIntent(context, true);
                context.stopService(serviceIntent);
                ContextCompat.startForegroundService(context, serviceIntent);
                break;

            case ACTION_FINALIZAR_SERVICO:
                serviceIntent = SyncService.getStartIntent(context, false);
                context.stopService(serviceIntent);
                break;
        }
    }
}