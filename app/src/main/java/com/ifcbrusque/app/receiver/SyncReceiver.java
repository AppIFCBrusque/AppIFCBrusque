package com.ifcbrusque.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.ifcbrusque.app.service.SyncService;

public class SyncReceiver extends BroadcastReceiver {
    public static final String SYNC_COMPLETA = "com.ifcbrusque.app.SYNC_COMPLETA";
    public static final String SYNC_RAPIDA = "com.ifcbrusque.app.SYNC_RAPIDA";
    public static final String FINALIZAR_SYNC = "com.ifcbrusque.app.FINALIZAR_SYNC";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent;

        switch (action) {
            case SYNC_COMPLETA:
                serviceIntent = SyncService.getStartIntent(context, false);
                context.stopService(serviceIntent);
                ContextCompat.startForegroundService(context, serviceIntent);
                break;

            case SYNC_RAPIDA:
                serviceIntent = SyncService.getStartIntent(context, true);
                context.stopService(serviceIntent);
                ContextCompat.startForegroundService(context, serviceIntent);
                break;

            case FINALIZAR_SYNC:
                serviceIntent = SyncService.getStartIntent(context, false);
                context.stopService(serviceIntent);
                break;
        }
    }
}