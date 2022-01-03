package com.ifcbrusque.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ifcbrusque.app.App;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.di.component.DaggerServiceComponent;
import com.ifcbrusque.app.di.component.ServiceComponent;
import com.ifcbrusque.app.di.module.ServiceModule;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import timber.log.Timber;

/*
Serviço utilizado para obter informações da internet (notícias, SIGAA) no fundo e processá-las
 */
public class SyncService extends Service {
    public static String EXTRA_SINCRONIZACAO_RAPIDA = "EXTRA_SINCRONIZACAO_RAPIDA";

    public static Intent getStartIntent(Context context, boolean sincronizacaoRapida) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra(EXTRA_SINCRONIZACAO_RAPIDA, sincronizacaoRapida);
        return intent;
    }

    @Inject
    DataManager mDataManager;
    @Inject
    CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceComponent component = DaggerServiceComponent.builder()
                .serviceModule(new ServiceModule(this))
                .applicationComponent(((App) getApplication()).getComponent())
                .build();
        component.inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sincronizar();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mCompositeDisposable.dispose();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    int mTarefaAtual = 0;
    int mTotalTarefas = 0;

    int tarefasPorDisciplina = 3;

    private void sincronizar() {
        mDataManager.notificarSincronizacao(this);

        mCompositeDisposable.add(conferirSIGAAConectado()
                .andThen(carregarNoticias())
                .andThen(carregarSIGAA())
                .subscribe(
                        () -> {
                            Timber.d("Sincronização finalizada");
                            //TODO: Atualizar a agenda
                            stopSelf();
                        },
                        erro -> lidarComErro(erro)));
    }

    private Completable carregarNoticias() {
        return Observable.defer(() -> {
            Timber.d("Carregando notícias");
            mTotalTarefas++;

            mDataManager.notificarSincronizacaoNoticias(this, mTarefaAtual, mTotalTarefas);

            return mDataManager.getPaginaNoticias(1);
        })
                .flatMap(previewsPaginaInicial -> mDataManager.armazenarPreviewsNovos(previewsPaginaInicial, true))
                .flatMapCompletable(previewsNovos -> {
                            mTarefaAtual++;
                            mDataManager.setDataUltimaSincronizacaoAutomaticaNoticias(new Date());

                            //Notificar os novos
                            Timber.d("Notícias novas: " + previewsNovos.size());
                            if (previewsNovos.size() > 0) {
                                for (Preview p : previewsNovos) {
                                    int idNotificacao = mDataManager.getNovoIdNotificacao();
                                    mDataManager.notificarNoticia(p, idNotificacao);
                                }
                                data.onNext(OBSERVABLE_PREVIEWS_NOVOS);
                            }

                            return Completable.complete();
                        }
                );
    }

    private Completable conferirSIGAAConectado() {
        return Observable.defer(() -> {
            Timber.d("Conferindo SIGAA conectado");
            return Observable.just(mDataManager.getSIGAAConectado());
        })
                .flatMap(conectado -> {
                    if (conectado) {
                        //Conferir se as credenciais estão corretas
                        final String login = mDataManager.getLoginSIGAA();
                        final String senha = mDataManager.getSenhaSIGAA();
                        return mDataManager.logarSIGAA(login, senha);
                    } else {
                        return Observable.just(false);
                    }
                })
                .flatMapCompletable(logado -> {
                    if (logado) {
                        mTotalTarefas += mDataManager.getUsuarioSIGAA().getDisciplinasAtuais().size() * tarefasPorDisciplina;
                        Timber.d("SIGAA logado");
                    } else {
                        Toast.makeText(this, R.string.erro_servico_sigaa_dados_invalidos, Toast.LENGTH_SHORT).show();
                        mDataManager.setSIGAAConectado(false); //Desativa a sincronização do SIGAA
                    }
                    return Completable.complete();
                });
    }

    private Completable carregarDisciplina(Disciplina disciplina) {
        mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);

        return mDataManager.getAvaliacoesDisciplinaSIGAA(disciplina)
                .flatMap(avaliacoes -> mDataManager.inserirAvaliacoes(avaliacoes))
                .flatMap(avaliacoesNovas -> {
                    for (Avaliacao a : avaliacoesNovas) {
                        mDataManager.notificarAvaliacaoNova(a, mDataManager.getNovoIdNotificacao());
                    }

                    mTarefaAtual++;
                    mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);

                    return mDataManager.getTarefasDisciplinaSIGAA(disciplina);
                })
                .flatMap(tarefas -> mDataManager.inserirTarefas(tarefas))
                .flatMap(tarefasNovas -> {
                    for (Tarefa t : tarefasNovas) {
                        mDataManager.notificarTarefaNova(t, mDataManager.getNovoIdNotificacao());
                    }

                    mTarefaAtual++;
                    mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);

                    return mDataManager.getQuestionariosDisciplinaSIGAA(disciplina);
                })
                .flatMap(questionarios -> mDataManager.inserirQuestionarios(questionarios))
                .flatMapCompletable(questionariosNovos -> {
                    for (Questionario q : questionariosNovos) {
                        mDataManager.notificarQuestionarioNovo(q, mDataManager.getNovoIdNotificacao());
                    }

                    mTarefaAtual++;
                    return Completable.complete();
                });
    }

    private Completable carregarSIGAA() {
        return Observable.defer(() -> Observable.just(mDataManager.getSIGAAConectado()))
                .flatMap(conectado -> {
                    //Iniciar sincronização do SIGAA
                    if (mDataManager.getSIGAAConectado()) {
                        return Observable.fromArray(mDataManager.getUsuarioSIGAA().getDisciplinasAtuais());
                    } else {
                        return Observable.fromArray(new ArrayList<Disciplina>()); //Não vai ter disciplinas para processar
                    }
                })
                .flatMapIterable(disciplinas -> disciplinas)
                .concatMapCompletable(disciplina -> carregarDisciplina(disciplina));
    }

    private void lidarComErro(Throwable e) {
        Timber.d("Erro durante a sincronização: %s | %s", e.getClass(), e.getMessage());

        if (e.getClass() == NoInternetException.class || e.getClass() == UnknownHostException.class || e.getClass() == IOException.class || e.getClass() == SocketTimeoutException.class) {
            stopSelf();
        }
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
