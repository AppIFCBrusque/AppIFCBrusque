package com.ifcbrusque.app.data.notification;

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

import androidx.core.app.NotificationCompat;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.ApplicationContext;
import com.ifcbrusque.app.service.SyncReceiver;
import com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.ui.noticia.NoticiaActivity;
import com.ifcbrusque.app.data.db.Converters;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.service.SyncService;
import com.squareup.picasso.Picasso;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static android.content.Context.ALARM_SERVICE;
import static com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_DESCRICAO;
import static com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID;
import static com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO;
import static com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_TITULO;
import static com.ifcbrusque.app.utils.AppConstants.NOTF_CHANNEL_ID;

/*
Classe com funções relacionadas às notificações
 */
@Singleton
public class AppNotificationHelper implements NotificationHelper {
    public static final int NOTF_SINCRONIZACAO_ID = 1;

    private final Context mContext;
    private final NotificationManager mNotificationManager;
    private final AlarmManager mAlarmManager;

    public static final int ICONE_LEMBRETE = R.drawable.ic_notifications_black_24dp;
    public static final int ICONE_SINCRONIZACAO = R.drawable.outline_sync_black_24;
    public static final int ICONE_NOTICIAS = R.drawable.outline_feed_black_24;
    public static final int ICONE_SIGAA = R.drawable.ic_notifications_black_24dp;
    public static final int ICONE_CANCELAR = R.drawable.outline_clear_black_24;

    @Inject
    public AppNotificationHelper(@ApplicationContext Context context) {
        mContext = context;
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
        mAlarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
    }

    /**
     * Cria um canal para as notificações (necessário API 26+)
     * Código copiado da documentação do Android
     */
    @Override
    public void criarCanalNotificacoes() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = mContext.getString(R.string.channel_title);
            String description = mContext.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTF_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    private PendingIntent criarPendingIntentNotificacaoLembrete(Lembrete lembrete) {
        Intent intent = new Intent(mContext, NotificationReceiver.class);
        intent.setAction(NotificationReceiver.ACTION_NOTIFICAR_LEMBRETE);

        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, lembrete.getId());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO, lembrete.getIdNotificacao());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_TITULO, lembrete.getTitulo());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_DESCRICAO, lembrete.getDescricao());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_TIPO_REPETICAO, lembrete.getTipo());
        intent.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_TEMPO_REPETICAO_PERSONALIZADA, lembrete.getTempoRepeticaoPersonalizada());

        return PendingIntent.getBroadcast(mContext, Math.toIntExact(lembrete.getIdNotificacao()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Cria e envia a notificação de um lembrete
     * Utilizado pelo agendarNotificacaoLembrete()
     *
     * @param bundle bundle contendo as informações do lembrete
     */
    @Override
    public void notificarLembrete(Bundle bundle) {
        long idLembrete = bundle.getLong(EXTRAS_LEMBRETE_ID, -1);
        int idNotificacao = Math.toIntExact(bundle.getLong(EXTRAS_LEMBRETE_ID_NOTIFICACAO, 9999));
        String titulo = bundle.getString(EXTRAS_LEMBRETE_TITULO, "");
        String descricao = bundle.getString(EXTRAS_LEMBRETE_DESCRICAO, "");

        final PendingIntent pendingIntent = getPendingIntentLembrete(idLembrete, idNotificacao);

        //Criar notificação
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_LEMBRETE)
                .setSubText("Lembretes")
                .setContentTitle(titulo)
                .setContentIntent(pendingIntent);
        //Se não houver descrição, é utilizado um formato reduzido de notificação
        if (!descricao.equals("")) {
            notificationBuilder.setContentText(descricao);
        }

        //Notificar
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    private PendingIntent getPendingIntentLembrete(long idLembrete, long idNotificacao) {
        Intent intentInserirLembrete = new Intent(mContext, InserirLembreteActivity.class);
        intentInserirLembrete.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID, idLembrete);
        intentInserirLembrete.putExtra(InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO, idNotificacao);
        intentInserirLembrete.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Abrir a InserirLembreteActivity como uma nova tarefa (não atrapalha a aba principal do aplicativo)
        return PendingIntent.getActivity(mContext, (int) idNotificacao, intentInserirLembrete, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Agenda a notificação de um lembrete (para a data dele) através do notificarLembrete()
     *
     * @param lembrete lembrete a ser notificado
     */
    @Override
    public void agendarNotificacaoLembrete(Lembrete lembrete) {
        PendingIntent pendingIntent = criarPendingIntentNotificacaoLembrete(lembrete);

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, Converters.dateToTimestamp(lembrete.getDataLembrete()), pendingIntent);

        Timber.d("Alarme do lembrete definido | ID=" + lembrete.getId() + ", ID_NOTIFICACAO=" + lembrete.getIdNotificacao());
    }

    @Override
    public void agendarNotificacaoLembreteSeFuturo(Lembrete lembrete) {
        if (lembrete.getEstado() == Lembrete.ESTADO_INCOMPLETO && new Date().before(lembrete.getDataLembrete())) {
            agendarNotificacaoLembrete(lembrete);
        }
    }

    @Override
    public void agendarNotificacoesLembretesFuturos(List<Lembrete> lembretes) {
        List<Lembrete> lembretesFuturosIncompletos = lembretes.stream().filter(l -> l.getEstado() == Lembrete.ESTADO_INCOMPLETO && new Date().before(l.getDataLembrete())).collect(Collectors.toList());

        for (Lembrete lembrete : lembretesFuturosIncompletos) {
            agendarNotificacaoLembrete(lembrete);
        }
    }

    /**
     * Desagenda a notificação de um lembrete
     *
     * @param lembrete lembrete a ser desagendado
     */
    @Override
    public void desagendarNotificacaoLembrete(Lembrete lembrete) {
        PendingIntent pendingIntent = criarPendingIntentNotificacaoLembrete(lembrete);

        //Cancelar a notificação agendada
        mAlarmManager.cancel(pendingIntent);

        //Remover a notificação (quando já notificado)
        mNotificationManager.cancel(Math.toIntExact(lembrete.getIdNotificacao()));

        Timber.d("Alarme cancelado | ID_NOTIFICACAO=%s", lembrete.getIdNotificacao());
    }

    private PendingIntent criarPendingIntentNotificacaoNoticia(Preview preview, int idNotificacao) {
        //NoticiaActivity para abrir
        Intent intent = NoticiaActivity.getStartIntent(mContext, preview);
        //Abrir a NoticiaActivity como uma nova tarefa (não atrapalha a aba principal do aplicativo)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(mContext, idNotificacao, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Cria e envia a notificação de uma notícia
     */
    @Override
    public void notificarNoticia(Preview preview, int idNotificacao) {
        PendingIntent pendingIntent = criarPendingIntentNotificacaoNoticia(preview, idNotificacao);

        //Notificação
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_NOTICIAS)
                .setSubText("Notícias")
                .setContentTitle(preview.getTitulo())
                .setContentText(preview.getDescricao())
                .setContentIntent(pendingIntent);

        //Imagem da notificação
        if (preview.getUrlImagemPreview().length() > 0) {
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
                        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
                    }).subscribe();
        } else {
            //Não possui imagem -> utilizar estilo de texto longo
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(preview.getDescricao()));
        }

        //Notificar
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    private PendingIntent getPendingIntentSync() {
        Intent intent = new Intent(mContext, SyncReceiver.class);
        intent.setAction(SyncReceiver.ACTION_SINCRONIZACAO_COMPLETA);
        return PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    @Override
    public void iniciarSincronizacao() {
        PendingIntent pendingIntent = getPendingIntentSync();
        mAlarmManager.cancel(pendingIntent);
        mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
    }

    @Override
    public void agendarSincronizacao() {
        PendingIntent pendingIntent = getPendingIntentSync();
        mAlarmManager.cancel(pendingIntent);

        //Agendar para a próxima meia noite
        final Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Timber.d("Sincronização agendada");
    }

    /**
     * Cria e exibe a notificação de que o serviço de sincronização está rodando
     */
    @Override
    public void notificarSincronizacao(SyncService service) {
        Intent intentParar = new Intent(service, SyncReceiver.class);
        intentParar.setAction(SyncReceiver.ACTION_FINALIZAR_SERVICO);
        PendingIntent parar = PendingIntent.getBroadcast(service, (int) System.currentTimeMillis(), intentParar, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setContentTitle(service.getString(R.string.sincronizacao))
                .setContentText(service.getString(R.string.sincronizacao_inicio))
                .setSmallIcon(ICONE_SINCRONIZACAO)
                .addAction(ICONE_CANCELAR, service.getText(R.string.cancel), parar)
                .build();

        service.startForeground(NOTF_SINCRONIZACAO_ID, notification);
    }

    @Override
    public void notificarSincronizacaoNoticias(SyncService service, int tarefaAtual, int totalTarefas) {
        Intent intentParar = new Intent(service, SyncReceiver.class);
        intentParar.setAction(SyncReceiver.ACTION_FINALIZAR_SERVICO);
        PendingIntent parar = PendingIntent.getBroadcast(service, (int) System.currentTimeMillis(), intentParar, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setContentTitle(service.getString(R.string.sincronizacao) + " (" + tarefaAtual + "/" + totalTarefas + ")")
                .setContentText(service.getText(R.string.title_noticias).toString().toUpperCase())
                .setSmallIcon(ICONE_SINCRONIZACAO)
                .setProgress(totalTarefas, tarefaAtual, false)
                .addAction(ICONE_CANCELAR, service.getText(R.string.cancel), parar)
                .build();

        service.startForeground(NOTF_SINCRONIZACAO_ID, notification);
    }

    @Override
    public void notificarSincronizacaoSIGAA(SyncService service, Disciplina disciplina, int tarefaAtual, int totalTarefas) {
        Intent intentParar = new Intent(service, SyncReceiver.class);
        intentParar.setAction(SyncReceiver.ACTION_FINALIZAR_SERVICO);
        PendingIntent parar = PendingIntent.getBroadcast(service, (int) System.currentTimeMillis(), intentParar, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setContentTitle(service.getString(R.string.sincronizacao) + " (" + tarefaAtual + "/" + totalTarefas + ")")
                .setContentText(disciplina.getNome())
                .setSmallIcon(ICONE_SINCRONIZACAO)
                .setProgress(totalTarefas, tarefaAtual, false)
                .addAction(ICONE_CANCELAR, service.getText(R.string.cancel), parar)
                .build();

        service.startForeground(NOTF_SINCRONIZACAO_ID, notification);
    }

    @Override
    public void notificarAvaliacaoNova(Avaliacao avaliacao, Lembrete lembrete, int idNotificacao) {
        final PendingIntent pendingIntent = getPendingIntentLembrete(lembrete.getId(), lembrete.getIdNotificacao());
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_SIGAA)
                .setContentTitle(String.format(mContext.getString(R.string.avaliacao_nova), avaliacao.getDisciplina().getNome()))
                .setContentText(avaliacao.getDescricao())
                .setSubText(mContext.getText(R.string.sigaa))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(avaliacao.getDescricao()));
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    @Override
    public void notificarAvaliacaoAlterada(Avaliacao avaliacao, Lembrete lembrete, int idNotificacao) {
        final PendingIntent pendingIntent = getPendingIntentLembrete(lembrete.getId(), lembrete.getIdNotificacao());
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_SIGAA)
                .setContentTitle(String.format(mContext.getString(R.string.avaliacao_alterada), avaliacao.getDisciplina().getNome()))
                .setContentText(avaliacao.getDescricao())
                .setSubText(mContext.getText(R.string.sigaa))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(avaliacao.getDescricao()));
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    @Override
    public void notificarTarefaNova(Tarefa tarefa, Lembrete lembrete, int idNotificacao) {
        final PendingIntent pendingIntent = getPendingIntentLembrete(lembrete.getId(), lembrete.getIdNotificacao());
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_SIGAA)
                .setContentTitle(String.format(mContext.getString(R.string.tarefa_nova), tarefa.getDisciplina().getNome()))
                .setContentText(tarefa.getTitulo())
                .setSubText(mContext.getText(R.string.sigaa))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(tarefa.getDescricao()));
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    @Override
    public void notificarTarefaAlterada(Tarefa tarefa, Lembrete lembrete, int idNotificacao) {
        final PendingIntent pendingIntent = getPendingIntentLembrete(lembrete.getId(), lembrete.getIdNotificacao());
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_SIGAA)
                .setContentTitle(String.format(mContext.getString(R.string.tarefa_alterada), tarefa.getDisciplina().getNome()))
                .setContentText(tarefa.getTitulo())
                .setSubText(mContext.getText(R.string.sigaa))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(tarefa.getDescricao()));
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    @Override
    public void notificarQuestionarioNovo(Questionario questionario, Lembrete lembrete, int idNotificacao) {
        final PendingIntent pendingIntent = getPendingIntentLembrete(lembrete.getId(), lembrete.getIdNotificacao());
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_SIGAA)
                .setContentTitle(String.format(mContext.getString(R.string.questionario_novo), questionario.getDisciplina().getNome()))
                .setContentText(questionario.getTitulo())
                .setSubText(mContext.getText(R.string.sigaa))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(questionario.getTitulo()));
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }

    @Override
    public void notificarQuestionarioAlterado(Questionario questionario, Lembrete lembrete, int idNotificacao) {
        final PendingIntent pendingIntent = getPendingIntentLembrete(lembrete.getId(), lembrete.getIdNotificacao());
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTF_CHANNEL_ID)
                .setChannelId(NOTF_CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(ICONE_SIGAA)
                .setContentTitle(String.format(mContext.getString(R.string.questionario_alterado), questionario.getDisciplina().getNome()))
                .setContentText(questionario.getTitulo())
                .setSubText(mContext.getText(R.string.sigaa))
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(questionario.getTitulo()));
        mNotificationManager.notify(idNotificacao, notificationBuilder.build());
    }
}
