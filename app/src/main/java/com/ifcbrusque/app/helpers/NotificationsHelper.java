package com.ifcbrusque.app.helpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.network.synchronization.SynchronizationBroadcastReceiver;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.ifcbrusque.app.activities.MainActivity.TAG;

/*
Classe com funções relacionadas às notificações
 */
public class NotificationsHelper {
    public static String CHANNEL_ID = "ifcbrusque_notificacoes";

    private NotificationsHelper() { }

    /**
     * Cria um canal para as notificações (necessário API 26+)
     * Código copiado da documentação do Android
     */
    public static void criarCanalNotificacoes(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_title);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void definirSincronizacaoPeriodicaNoticias(Context context) {
        Intent intent = new Intent(context, SynchronizationBroadcastReceiver.class);
        intent.setAction(SynchronizationBroadcastReceiver.ATUALIZAR_NOTICIAS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);

        Log.d(TAG, "definirSincronizacaoPeriodicaNoticias: alarme da sincronização das notícias definido");
    }
}
