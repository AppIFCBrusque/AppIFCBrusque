package com.ifcbrusque.app.util.helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.activities.noticia.NoticiaActivity;
import com.ifcbrusque.app.data.Converters;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.models.Preview;
import com.ifcbrusque.app.util.AppBroadcastReceiver;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;
import com.ifcbrusque.app.util.service.SynchronizationService;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.content.Context.ALARM_SERVICE;
import static com.ifcbrusque.app.activities.MainActivity.TAG;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_DESCRICAO;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_TITULO;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_DATA;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_TITULO;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_URL;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_URL_IMAGEM_PREVIEW;
import static com.ifcbrusque.app.data.Converters.dateToTimestamp;

/*
Classe com funções relacionadas às notificações
 */
public class NotificationHelper {
    public static String CHANNEL_ID = "ifcbrusque_notificacoes";
    public static int ID_SINCRONIZACAO = 1;

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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Agenda a notificação de um lembrete (para a data dele) através do notificarLembrete()
     * @param lembrete lembrete a ser notificado
     */
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
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Cria e envia a notificação de um lembrete
     * Utilizado pelo agendarNotificacaoLembrete()
     * @param bundle bundle contendo as informações do lembrete
     */
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

    /**
     * Cria e envia a notificação de uma notícia
     */
    public static void notificarNoticia(Context context, Preview preview) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        PreferencesHelper pref = new PreferencesHelper(context);

        final int id = pref.getUltimoIdNotificacoes();

        //NoticiaActivity para abrir
        Intent intent = new Intent(context, NoticiaActivity.class);
        intent.putExtra(NOTICIA_TITULO, preview.getTitulo());
        intent.putExtra(NOTICIA_DATA, dateToTimestamp(preview.getDataNoticia()));
        intent.putExtra(NOTICIA_URL, preview.getUrlNoticia());
        intent.putExtra(NOTICIA_URL_IMAGEM_PREVIEW, preview.getUrlImagemPreview());
        // Abrir a NoticiaActivity como uma nova tarefa (não atrapalha a aba principal do aplicativo)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Notificação
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.outline_feed_black_24)
                .setSubText("Notícias")
                .setContentTitle(preview.getTitulo())
                .setContentText(preview.getDescricao())
                .setContentIntent(pendingIntent);

        //Imagem da notificação
        if(preview.getUrlImagemPreview().length() > 0) {
            //Possui imagem -> criar observable para carregar e atualizar
            Observable.defer(() -> {
                Bitmap bmp = Picasso.get().load(preview.getUrlImagemPreview()).get();
                return Observable.just(bmp);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(bitmap -> {
                        notificationBuilder
                                .setLargeIcon(bitmap)
                                .setStyle(new NotificationCompat.BigPictureStyle()
                                        .bigPicture(bitmap)
                                        .bigLargeIcon(null));
                        notificationManager.notify(id, notificationBuilder.build());
                    }).subscribe();
        } else {
            //Não possui imagem -> utilizar estilo de texto longo
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(preview.getDescricao()));
        }

        //Notificar
        notificationManager.notify(id, notificationBuilder.build());
    }

    /**
     * Cria e exibe a notificação de que o serviço de sincronização está rodando
     */
    public static void notificarSincronizacao(Context context) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Notícias")
                .setSmallIcon(R.drawable.outline_sync_black_24)
                .build();

        ((SynchronizationService) context).startForeground(ID_SINCRONIZACAO, notification);
    }
}
