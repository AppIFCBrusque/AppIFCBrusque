package com.ifcbrusque.app.util;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.data.Converters;
import com.ifcbrusque.app.models.Lembrete;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.ifcbrusque.app.activities.MainActivity.TAG;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_DESCRICAO;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_TITULO;

/*
Classe com funções relacionadas às notificações
 */
public class NotificationHelper {
    public static String CHANNEL_ID = "ifcbrusque_notificacoes";

    private NotificationHelper() { }

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
        Intent intent = new Intent(context, AppBroadcastReceiver.class);
        intent.setAction(AppBroadcastReceiver.ATUALIZAR_NOTICIAS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_HALF_DAY, AlarmManager.INTERVAL_HALF_DAY, pendingIntent);

        Log.d(TAG, "definirSincronizacaoPeriodicaNoticias: alarme da sincronização das notícias definido");
    }

    public static void agendarNotificacaoLembrete(Context context, Lembrete lembrete) {
        Intent intent = new Intent(context, AppBroadcastReceiver.class);
        intent.setAction(AppBroadcastReceiver.NOTIFICAR_LEMBRETE);

        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, lembrete.getId());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO, lembrete.getIdNotificacao());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_TITULO, lembrete.getTitulo());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_DESCRICAO, lembrete.getDescricao());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Math.toIntExact(lembrete.getIdNotificacao()), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        AlarmManager alarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarm.setExact(AlarmManager.RTC_WAKEUP, Converters.dateToTimestamp(lembrete.getDataLembrete()), pendingIntent);

        Log.d(TAG, "definirNotificacaoLembrete: alarme do lembrete definido. ID=" + lembrete.getId() + ", ID_NOTIFICACAO=" + lembrete.getIdNotificacao());
    }

    public static void notificarLembrete(Context context, Bundle bundle) {
        long idLembrete = bundle.getLong(EXTRAS_LEMBRETE_ID, -1);
        long idNotificacao = bundle.getLong(EXTRAS_LEMBRETE_ID_NOTIFICACAO, 9999);
        String titulo = bundle.getString(EXTRAS_LEMBRETE_TITULO, "");
        String descricao = bundle.getString(EXTRAS_LEMBRETE_DESCRICAO, "");

        //InserirLembreteActivity para abrir
        Intent intentInserirLembrete = new Intent(context, InserirLembreteActivity.class);
        intentInserirLembrete.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, idLembrete);
        intentInserirLembrete.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO, idNotificacao);
        //Abrir a InserirLembreteActivity como uma nova tarefa (não atrapalha a aba principal do aplicativo)
        intentInserirLembrete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Math.toIntExact(idNotificacao), intentInserirLembrete, PendingIntent.FLAG_UPDATE_CURRENT);

        //Criar notificação
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setSubText("Lembretes")
                .setContentTitle(titulo)
                .setContentIntent(pendingIntent);
        //Se não houver descrição, é utilizado um formato reduzido de notificação
        if(!descricao.equals("")) {
            notificationBuilder.setContentText(descricao);
        }

        //Notificar
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(Math.toIntExact(idNotificacao), notificationBuilder.build());
    }
}
