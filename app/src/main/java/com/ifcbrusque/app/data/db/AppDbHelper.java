package com.ifcbrusque.app.data.db;

import android.content.Context;
import android.util.Log;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.di.ApplicationContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.utils.AppConstants.TAG;

@Singleton
public class AppDbHelper implements DbHelper {
    private AppDatabase mAppDatabase;

    @Inject
    public AppDbHelper(@ApplicationContext Context context) {
        mAppDatabase = AppDatabase.getDbInstance(context.getApplicationContext());
    }

    @Override
    public Observable<List<Preview>> getPreviewsArmazenados() {
        return Observable.defer(() -> Observable.just(mAppDatabase.previewDao().getAll()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Preview>> armazenarPreviewsNovos(List<Preview> previews, boolean retornarPreviewsNovos) {
        return Observable.defer(() -> {
            List<Preview> previewsNoArmazenamento = mAppDatabase.previewDao().getAll(); //Atualizar para o mais recente

            List<Preview> previewsNovos = new ArrayList<>();
            if (previews.size() > 0) {
                for (Preview p : previews) { //Encontrar os previews não armazenados
                    if (!previewsNoArmazenamento.stream().filter(_p -> _p.getUrlNoticia().equals(p.getUrlNoticia())).findFirst().isPresent()) {
                        previewsNovos.add(p);
                    }
                }

                if (previewsNovos.size() > 0) {
                    mAppDatabase.previewDao().insertAll(previewsNovos);
                    previewsNoArmazenamento = mAppDatabase.previewDao().getAll();
                }
            }

            if (retornarPreviewsNovos) return Observable.just(previewsNovos);
            else return Observable.just(previewsNoArmazenamento);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Noticia> getNoticia(String url) {
        return Observable.defer(() -> Observable.just(mAppDatabase.noticiaDao().getNoticia(url)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Long> inserirNoticia(Noticia noticia) {
        return Observable.defer(() -> Observable.just(mAppDatabase.noticiaDao().insert(noticia)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Lembrete> getLembrete(long id) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().getLembrete(id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Lembrete>> getLembretesArmazenados() {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().getAll()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Long> inserirLembrete(Lembrete lembrete) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().insert(lembrete)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarLembrete(Lembrete lembrete) {
        return Completable.fromRunnable(() -> {
            mAppDatabase.lembreteDao().delete(lembrete);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable atualizarLembrete(Lembrete lembrete) {
        return Completable.fromRunnable(() -> mAppDatabase.lembreteDao().atualizarLembrete(lembrete))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable alterarEstadoLembrete(long id, int novoEstado) {
        return Completable.fromRunnable(() -> {
            mAppDatabase.lembreteDao().alterarEstadoLembrete(id, novoEstado);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Atualiza a data de notificação para um lembrete com repetição
     * Se a repetição for de hora em hora, atualiza a data de notificação do lembrete para 1 hora depois da primeira notificação
     * Não atualiza para um momento antes ou igual a hora atual. Em vez disso, continua adicionando o intervalo até resultar em um momento posterior ao atual
     *
     * @return observable com o lembrete armazenado (com a data nova)
     */
    @Override
    public Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(long idLembrete) {
        final Calendar c = Calendar.getInstance();

        return Observable.defer(() -> {
            Lembrete lembrete = mAppDatabase.lembreteDao().getLembrete(idLembrete);

            c.setTime(lembrete.getDataLembrete());
            //Para evitar spammar um monte de notificação de lembretes com a data antes, apenas prossegue quando a data nova é maior que a atual
            while (c.getTime().compareTo(lembrete.getDataLembrete()) == 0 || c.getTime().before(new Date())) {
                switch (lembrete.getTipoRepeticao()) {
                    case Lembrete.REPETICAO_HORA:
                        c.add(Calendar.HOUR_OF_DAY, 1);
                        break;

                    case Lembrete.REPETICAO_DIA:
                        c.add(Calendar.DAY_OF_MONTH, 1);
                        break;

                    case Lembrete.REPETICAO_SEMANA:
                        c.add(Calendar.DAY_OF_MONTH, 7);
                        break;

                    case Lembrete.REPETICAO_MES:
                        c.add(Calendar.MONTH, 1);
                        break;

                    case Lembrete.REPETICAO_ANO:
                        c.add(Calendar.YEAR, 1);
                        break;
                }
            }
            Log.d(TAG, "atualizarDataLembreteComRepeticao: nova data " + c.get(Calendar.HOUR_OF_DAY) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH));
            lembrete.setDataLembrete(c.getTime());

            mAppDatabase.lembreteDao().atualizarLembrete(lembrete);
            return Observable.just(lembrete);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
