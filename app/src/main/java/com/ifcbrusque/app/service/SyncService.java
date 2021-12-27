package com.ifcbrusque.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.ifcbrusque.app.App;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.di.component.DaggerServiceComponent;
import com.ifcbrusque.app.di.component.ServiceComponent;

import java.util.Date;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import timber.log.Timber;

import static com.ifcbrusque.app.utils.AppConstants.TAG;

/*
Serviço utilizado para obter informações da internet (notícias, SIGAA) no fundo e processá-las
 */
public class SyncService extends Service {
    final public static String EXTRA_ATUALIZAR_NOTICIAS = "EXTRA_ATUALIZAR_NOTICIAS";

    public static Intent getStartIntent(Context context, boolean atualizarNoticias, boolean atualizarSIGAA) {
        Intent intent = new Intent(context, SyncService.class);

        if(atualizarNoticias) {
            intent.putExtra(SyncService.EXTRA_ATUALIZAR_NOTICIAS, true);
        }

        return intent;
    }

    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceComponent component = DaggerServiceComponent.builder()
                .applicationComponent(((App) getApplication()).getComponent())
                .build();
        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        iniciarSincronizacao(intent.getExtras());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void iniciarSincronizacao(Bundle bundle) {
        mDataManager.notificarSincronizacao(this);
        boolean atualizarNoticias = bundle.getBoolean(EXTRA_ATUALIZAR_NOTICIAS, false);

        if (atualizarNoticias) {
            Timber.d("Sincronizar notícias");
            sincronizarNoticias();
        }
    }

    private void sincronizarNoticias() {
        mDataManager.getPaginaNoticias(1)
                .flatMap(previews -> mDataManager.armazenarPreviewsNovos(previews, true))
                .subscribe(previewsNovos -> {
                    mDataManager.setDataUltimaSincronizacaoAutomaticaNoticias(new Date());
                    Timber.d("Previews novos " + previewsNovos.size());
                    if (previewsNovos.size() > 0) {
                        for (Preview p : previewsNovos) {
                            int idNotificacao = mDataManager.getNovoIdNotificacao();
                            mDataManager.notificarNoticia(p, idNotificacao);
                        }
                        data.onNext(OBSERVABLE_PREVIEWS_NOVOS);
                    }
                }, erro -> {
                    Timber.d("Erro ao carregar a página inicial de notícias");
                    stopSelf();
                }, () -> {
                    stopSelf();
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Utilizado para notificar as activities quando carrega algum resultado
     */
    final public static int OBSERVABLE_PREVIEWS_NOVOS = 1;

    static PublishSubject<Integer> data = PublishSubject.create();

    public static Observable<Integer> getObservable() {
        return data;
    }
}
