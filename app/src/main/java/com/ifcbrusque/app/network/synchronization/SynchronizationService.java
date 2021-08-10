package com.ifcbrusque.app.network.synchronization;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.ifcbrusque.app.models.Preview;
import com.ifcbrusque.app.models.PaginaNoticias;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

/*
Serviço utilizado para obter informações da internet (notícias, SIGAA) no fundo e processá-las
 */
public class SynchronizationService extends Service {
    final public static String EXTRA_ATUALIZAR_NOTICIAS = "EXTRA_ATUALIZAR_NOTICIAS";
    final public static String EXTRA_NUMERO_PAGINA_NOTICIAS = "EXTRA_NUMERO_PAGINA_NOTICIAS";

    private PaginaNoticias campus;
    private AppDatabase db;
    private SynchronizationNotification notf;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new PreferencesHelper(this).incrementarVezesServico();//////////////////////////////

        boolean atualizarNoticias = intent.getBooleanExtra(EXTRA_ATUALIZAR_NOTICIAS, false);
        int pagina = intent.getIntExtra(EXTRA_NUMERO_PAGINA_NOTICIAS, 1);

        campus = new PaginaNoticias(this);
        db = AppDatabase.getDbInstance(getApplicationContext());
        notf = new SynchronizationNotification(this);

        if (atualizarNoticias) {
            notf.notificarSincronizacaoNoticias();
            carregarPreviews(pagina)
                    .doOnComplete(() -> {
                        stopSelf();
                    })
                    .subscribe();
        } else {
            stopSelf();
        }

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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Retorna um completable que carrega os previews da página de notícias com o número inserido e armazena os novos
     * @param pagina
     */
    private Completable carregarPreviews(int pagina) {
        return Completable.fromRunnable(() -> {
            List<Preview> previewsNaPagina = new ArrayList<>();
            try {
                previewsNaPagina = campus.getPaginaNoticias(pagina);
            } catch (IOException e) {
                /*
                Se acontecer algum erro, o ultimosPreviewsCarregados vai ser null
                 */
            } catch (ParseException e) {
                ////////////////////////
            }

            if (previewsNaPagina != null && previewsNaPagina.size() > 0) {
                armazenarPreviewsNovos(previewsNaPagina);
            }

        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Armazena os previews novos não salvos no banco de dados e envia a notificação para cada um
     * @param previews
     */
    private void armazenarPreviewsNovos(List<Preview> previews) {
        db.armazenarPreviewsNovos(previews, true)
                .doOnNext(previewsNovos -> {
                    if (previewsNovos.size() > 0) {
                        //Notificar previews novos
                        for (Preview p : previewsNovos) {
                            notf.notificarPreview(p);
                        }
                        //Notificar no observable
                        data.onNext(OBSERVABLE_PREVIEWS_NOVOS);
                    }
                }).subscribe();
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
