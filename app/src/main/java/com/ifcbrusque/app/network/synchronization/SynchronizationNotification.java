package com.ifcbrusque.app.network.synchronization;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.noticia.NoticiaActivity;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.ifcbrusque.app.models.Preview;
import com.squareup.picasso.Picasso;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_DATA;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_TITULO;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_URL;
import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.NOTICIA_URL_IMAGEM_PREVIEW;
import static com.ifcbrusque.app.data.Converters.dateToTimestamp;
import static com.ifcbrusque.app.helpers.NotificationsHelper.CHANNEL_ID;
import static com.ifcbrusque.app.helpers.NotificationsHelper.criarCanalNotificacoes;

public class SynchronizationNotification {
    final private int ID_NOTICIA_SERVICO = 1;

    private Context context;
    private NotificationManagerCompat notificationManager;
    private PreferencesHelper pref;

    public SynchronizationNotification(Context context) {
        this.context = context;
        notificationManager = NotificationManagerCompat.from(context);
        pref = new PreferencesHelper(context);
        criarCanalNotificacoes(context);
    }

    public void notificarSincronizacaoNoticias() {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Notícias")
                .setSmallIcon(R.drawable.outline_sync_black_24)
                .build();

        ((SynchronizationService) context).startForeground(ID_NOTICIA_SERVICO, notification);
    }

    /**
     * Cria a notificação de um preview e a envia
     */
    public void notificarPreview(Preview preview) {
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
}
