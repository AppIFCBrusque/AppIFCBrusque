package com.ifcbrusque.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class SyncReceiver extends BroadcastReceiver {
    public static final String ACTION_ATUALIZAR_NOTICIAS = "com.ifcbrusque.app.ACTION_ATUALIZAR_NOTICIAS";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_ATUALIZAR_NOTICIAS.equals(action)) {
            Intent serviceIntent = SyncService.getStartIntent(context, true, false);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }
}