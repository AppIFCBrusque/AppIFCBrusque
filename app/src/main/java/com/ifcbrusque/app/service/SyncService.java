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
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.model.NoInternetException;
import com.ifcbrusque.app.di.component.DaggerServiceComponent;
import com.ifcbrusque.app.di.component.ServiceComponent;
import com.ifcbrusque.app.di.module.ServiceModule;
import com.stacked.sigaa_ifc.Disciplina;

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
    public static final String EXTRA_SINCRONIZACAO_RAPIDA = "EXTRA_SINCRONIZACAO_RAPIDA";

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

        data.onNext(OBSERVABLE_ATUALIZAR_RV_PREVIEWS);
        data.onNext(OBSERVABLE_ATUALIZAR_RV_LEMBRETES);

        mDataManager.agendarSincronizacao();

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    boolean mPrimeiraSincronizacaoNoticias = true;

    int mTarefaAtual = 0;
    int mTotalTarefas = 0;

    final int mTarefasPorDisciplina = 3;

    private void sincronizar() {
        mDataManager.notificarSincronizacao(this);
        mDataManager.setDataUltimaSincronizacaoCompleta(new Date());

        mPrimeiraSincronizacaoNoticias = mDataManager.getPrimeiraSincronizacaoNoticias();

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

                            Timber.d("Notícias novas: %s", previewsNovos.size());
                            if (!mPrimeiraSincronizacaoNoticias) {
                                //Notificar
                                if (previewsNovos.size() > 0) {
                                    for (Preview p : previewsNovos) {
                                        int idNotificacao = mDataManager.getNovoIdNotificacao();
                                        mDataManager.notificarNoticia(p, idNotificacao);
                                    }
                                }
                            } else {
                                mDataManager.setPrimeiraSincronizacaoNoticias(false);
                            }

                            return Completable.complete();
                        }
                );
    }

    private Completable conferirSIGAAConectado() {
        return Observable.defer(() -> {
            Timber.d("Conferindo SIGAA conectado");
            return Observable.just(mDataManager.getPrefSincronizarSIGAAA() && mDataManager.getSIGAAConectado());
        })
                .flatMapCompletable(conectado -> {
                    if (conectado) {
                        //Conferir se as credenciais estão corretas
                        final String login = mDataManager.getLoginSIGAA();
                        final String senha = mDataManager.getSenhaSIGAA();

                        return mDataManager.logarSIGAA(login, senha)
                                .flatMapCompletable(logado -> {
                                    if (logado) {
                                        mTotalTarefas += mDataManager.getUsuarioSIGAA().getDisciplinasAtuais().size() * mTarefasPorDisciplina;
                                        Timber.d("SIGAA logado");
                                        return mDataManager.inserirDisciplinas(mDataManager.getUsuarioSIGAA().getDisciplinasAtuais());
                                    } else {
                                        Toast.makeText(this, R.string.erro_servico_sigaa_dados_invalidos, Toast.LENGTH_SHORT).show();
                                        mDataManager.setSIGAAConectado(false); // Desativa a sincronização do SIGAA até o usuário relogar manualmente
                                        return Completable.complete();
                                    }
                                });
                    } else {
                        return Completable.complete();
                    }
                });
    }

    private Completable carregarDisciplina(Disciplina disciplina) {
        mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);

        //TODO: O código dessa função provavelmente pode ser reduzido, pois ele meio que somente repete o mesmo código 3 vezes

        /*
        Avaliações
         */
        return mDataManager.getAvaliacoesDisciplinaSIGAA(disciplina)
                .flatMap(avaliacoesNoSIGAA -> mDataManager.getAllAvaliacoes()
                        .flatMap(avaliacoesNoBD -> Observable.fromIterable(avaliacoesNoSIGAA)
                                .flatMap(avaliacaoNoSIGAA -> {
                                    if (avaliacoesNoBD.stream().anyMatch(abd -> abd.getId() == avaliacaoNoSIGAA.getId())) {
                                        //Avaliação já armazenada no BD (atualizar)
                                        Timber.d("Avaliação já existente: %s", avaliacaoNoSIGAA.getDescricao());
                                        return mDataManager.atualizarAvaliacao(avaliacaoNoSIGAA)
                                                .flatMap(colunasAtualizadas -> mDataManager.getLembrete(avaliacaoNoSIGAA)
                                                        .flatMapCompletable(lembretesArmazenados -> {
                                                            if (lembretesArmazenados.size() > 0) {
                                                                Lembrete lembreteArmazenado = lembretesArmazenados.get(0);

                                                                //O colunasAtualizadas não está funcionando para conferir o número de colunas atualizadas (sempre retorna 1)
                                                                //Por causa disso, eu estou conferindo se algum parâmetro do lembrete é diferente da tarefa no SIGAA
                                                                boolean notificar = false;
                                                                if (!avaliacaoNoSIGAA.getDescricao().equals(lembreteArmazenado.getTitulo()) || avaliacaoNoSIGAA.getData().getTime() != lembreteArmazenado.getDataLembrete().getTime()) {
                                                                    notificar = true;
                                                                }

                                                                //Atualizar o lembrete para as informações no SIGAA, mas manter o estado de completo definido pelo usuário

                                                                if (avaliacaoNoSIGAA.getData().before(new Date())) {
                                                                    //Definir como completo caso a data tenha passado
                                                                    lembreteArmazenado.setEstado(Lembrete.ESTADO_COMPLETO);
                                                                } else {
                                                                    //Ainda não passou a data da avaliação
                                                                    if (lembreteArmazenado.getDataLembrete().before(avaliacaoNoSIGAA.getData())) {
                                                                        //Caso a data foi prorrogada e não tenha sido enviado, definir como incompleto
                                                                        lembreteArmazenado.setEstado(Lembrete.ESTADO_INCOMPLETO);
                                                                        mDataManager.agendarNotificacaoLembrete(lembreteArmazenado);
                                                                    }
                                                                }

                                                                lembreteArmazenado.setTitulo(avaliacaoNoSIGAA.getDescricao());
                                                                lembreteArmazenado.setDataLembrete(avaliacaoNoSIGAA.getData());

                                                                if (notificar) {
                                                                    mDataManager.notificarAvaliacaoAlterada(avaliacaoNoSIGAA, lembreteArmazenado, mDataManager.getNovoIdNotificacao());
                                                                }

                                                                return mDataManager.atualizarLembrete(lembreteArmazenado);
                                                            } else {
                                                                return Completable.complete();
                                                            }
                                                        })
                                                        .toObservable()
                                                        .map(x -> avaliacaoNoSIGAA));
                                    } else {
                                        //Avaliação ainda não armazenada (inserir)
                                        Timber.d("Avaliação nova: %s", avaliacaoNoSIGAA.getDescricao());
                                        return mDataManager.inserirAvaliacao(avaliacaoNoSIGAA)
                                                .flatMap(avaliacaoNova -> {
                                                    if (avaliacaoNova.getData().after(new Date())) {
                                                        //Criar um lembrete
                                                        Lembrete lembrete = new Lembrete(avaliacaoNova, mDataManager.getNovoIdNotificacao());
                                                        return mDataManager.inserirLembrete(lembrete)
                                                                .map(lembreteComID -> {
                                                                    if (lembreteComID.getEstado() == Lembrete.ESTADO_INCOMPLETO) {
                                                                        //Notificar novo item
                                                                        mDataManager.notificarAvaliacaoNova(avaliacaoNova, lembreteComID, mDataManager.getNovoIdNotificacao());
                                                                        //Agendar a notificação do lembrete
                                                                        mDataManager.agendarNotificacaoLembrete(lembreteComID);
                                                                    }
                                                                    return avaliacaoNova;
                                                                });
                                                    } else {
                                                        return Observable.just(avaliacaoNova);
                                                    }
                                                });
                                    }
                                })
                                .toList()
                                .toObservable()))
                .flatMap(listaAvaliacoes -> {
                    mTarefaAtual++;
                    mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);
                    return mDataManager.getTarefasDisciplinaSIGAA(disciplina);
                })
                /*
                Tarefas
                 */
                .flatMap(tarefasNoSIGAA -> mDataManager.getAllTarefas()
                        .flatMap(tarefasNoBD -> Observable.fromIterable(tarefasNoSIGAA)
                                .flatMap(tarefaNoSIGAA -> {
                                    if (tarefasNoBD.stream().anyMatch(tbd -> tbd.getId().equals(tarefaNoSIGAA.getId()))) {
                                        //Tarefa já armazenada no BD (atualizar)
                                        Timber.d("Tarefa já existente: %s", tarefaNoSIGAA.getTitulo());
                                        return mDataManager.atualizarTarefa(tarefaNoSIGAA)
                                                .flatMap(colunasAtualizadas -> mDataManager.getLembrete(tarefaNoSIGAA)
                                                        .flatMapCompletable(lembretesArmazenados -> {
                                                            if (lembretesArmazenados.size() > 0) {
                                                                Lembrete lembreteArmazenado = lembretesArmazenados.get(0);

                                                                //O colunasAtualizadas não está funcionando para conferir o número de colunas atualizadas (sempre retorna 1)
                                                                //Por causa disso, eu estou conferindo se algum parâmetro do lembrete é diferente da tarefa no SIGAA
                                                                boolean notificar = false;
                                                                if (!tarefaNoSIGAA.getTitulo().equals(lembreteArmazenado.getTitulo()) || !tarefaNoSIGAA.getDescricao().equals(lembreteArmazenado.getDescricao()) || tarefaNoSIGAA.getFim().getTime() != lembreteArmazenado.getDataLembrete().getTime()) {
                                                                    notificar = true;
                                                                }

                                                                //Atualizar o lembrete para as informações no SIGAA, mas manter o estado de completo definido pelo usuário

                                                                if (tarefaNoSIGAA.isEnviada()) {
                                                                    //Caso o item foi enviado no SIGAA, definir o lembrete como completo
                                                                    lembreteArmazenado.setEstado(Lembrete.ESTADO_COMPLETO);
                                                                    mDataManager.desagendarNotificacaoLembrete(lembreteArmazenado);
                                                                } else {
                                                                    if (tarefaNoSIGAA.getFim().before(new Date())) {
                                                                        //Definir como completo caso a data tenha passado
                                                                        lembreteArmazenado.setEstado(Lembrete.ESTADO_COMPLETO);
                                                                    } else {
                                                                        //Ainda não passou a data da tarefa
                                                                        if (lembreteArmazenado.getDataLembrete().before(tarefaNoSIGAA.getFim())) {
                                                                            //Caso a data foi prorrogada e não tenha sido enviado, definir como incompleto
                                                                            lembreteArmazenado.setEstado(Lembrete.ESTADO_INCOMPLETO);
                                                                            mDataManager.agendarNotificacaoLembrete(lembreteArmazenado);
                                                                        }
                                                                    }
                                                                }

                                                                lembreteArmazenado.setTitulo(tarefaNoSIGAA.getTitulo());
                                                                lembreteArmazenado.setDescricao(tarefaNoSIGAA.getDescricao());
                                                                lembreteArmazenado.setDataLembrete(tarefaNoSIGAA.getFim());

                                                                if (notificar) {
                                                                    mDataManager.notificarTarefaAlterada(tarefaNoSIGAA, lembreteArmazenado, mDataManager.getNovoIdNotificacao());
                                                                }

                                                                return mDataManager.atualizarLembrete(lembreteArmazenado);
                                                            } else {
                                                                return Completable.complete();
                                                            }
                                                        })
                                                        .toObservable()
                                                        .map(x -> tarefaNoSIGAA));
                                    } else {
                                        //Tarefa ainda não armazenada (inserir)
                                        Timber.d("Tarefa nova: %s", tarefaNoSIGAA.getTitulo());
                                        return mDataManager.inserirTarefa(tarefaNoSIGAA)
                                                .flatMap(tarefaNova -> {
                                                    if (tarefaNova.getFim().after(new Date())) {
                                                        //Criar um lembrete
                                                        Lembrete lembrete = new Lembrete(tarefaNova, mDataManager.getNovoIdNotificacao());
                                                        return mDataManager.inserirLembrete(lembrete)
                                                                .map(lembreteComID -> {
                                                                    if (lembreteComID.getEstado() == Lembrete.ESTADO_INCOMPLETO) {
                                                                        //Notificar novo item
                                                                        mDataManager.notificarTarefaNova(tarefaNova, lembreteComID, mDataManager.getNovoIdNotificacao());
                                                                        //Agendar a notificação do lembrete
                                                                        mDataManager.agendarNotificacaoLembrete(lembreteComID);
                                                                    }
                                                                    return tarefaNova;
                                                                });
                                                    } else {
                                                        return Observable.just(tarefaNova);
                                                    }
                                                });
                                    }
                                })
                                .toList()
                                .toObservable()))
                .flatMap(listaTarefas -> {
                    mTarefaAtual++;
                    mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);
                    return mDataManager.getQuestionariosDisciplinaSIGAA(disciplina);
                })
                /*
                Questionários
                 */
                .flatMap(questionariosNoSIGAA -> mDataManager.getAllQuestionarios()
                        .flatMap(questionariosNoBD -> Observable.fromIterable(questionariosNoSIGAA)
                                .flatMap(questionarioNoSIGAA -> {
                                    if (questionariosNoBD.stream().anyMatch(qbd -> qbd.getId() == questionarioNoSIGAA.getId())) {
                                        //Questionário já armazenado no BD (atualizar)
                                        Timber.d("Questionário já existente: %s", questionarioNoSIGAA.getTitulo());
                                        return mDataManager.atualizarQuestionario(questionarioNoSIGAA)
                                                .flatMap(colunasAtualizadas -> mDataManager.getLembrete(questionarioNoSIGAA)
                                                        .flatMapCompletable(lembretesArmazenados -> {
                                                            if (lembretesArmazenados.size() > 0) {
                                                                Lembrete lembreteArmazenado = lembretesArmazenados.get(0);

                                                                //O colunasAtualizadas não está funcionando para conferir o número de colunas atualizadas (sempre retorna 1)
                                                                //Por causa disso, eu estou conferindo se algum parâmetro do lembrete é diferente da tarefa no SIGAA
                                                                boolean notificar = false;
                                                                if (!questionarioNoSIGAA.getTitulo().equals(lembreteArmazenado.getTitulo()) || questionarioNoSIGAA.getDataFim().getTime() != lembreteArmazenado.getDataLembrete().getTime()) {
                                                                    notificar = true;
                                                                }

                                                                //Atualizar o lembrete para as informações no SIGAA, mas manter o estado de completo definido pelo usuário

                                                                if (questionarioNoSIGAA.isEnviado()) {
                                                                    //Caso o item foi enviado no SIGAA, definir o lembrete como completo
                                                                    lembreteArmazenado.setEstado(Lembrete.ESTADO_COMPLETO);
                                                                    mDataManager.desagendarNotificacaoLembrete(lembreteArmazenado);
                                                                } else {
                                                                    if (questionarioNoSIGAA.getDataFim().before(new Date())) {
                                                                        //Definir como completo caso a data tenha passado
                                                                        lembreteArmazenado.setEstado(Lembrete.ESTADO_COMPLETO);
                                                                    } else {
                                                                        //Ainda não passou a data do questionário
                                                                        if (lembreteArmazenado.getDataLembrete().before(questionarioNoSIGAA.getDataFim())) {
                                                                            //Caso a data foi prorrogada e não tenha sido enviado, definir como incompleto
                                                                            lembreteArmazenado.setEstado(Lembrete.ESTADO_INCOMPLETO);
                                                                            mDataManager.agendarNotificacaoLembrete(lembreteArmazenado);
                                                                        }
                                                                    }
                                                                }

                                                                lembreteArmazenado.setTitulo(questionarioNoSIGAA.getTitulo());
                                                                lembreteArmazenado.setDataLembrete(questionarioNoSIGAA.getDataFim());

                                                                if (notificar) {
                                                                    mDataManager.notificarQuestionarioAlterado(questionarioNoSIGAA, lembreteArmazenado, mDataManager.getNovoIdNotificacao());
                                                                }

                                                                return mDataManager.atualizarLembrete(lembreteArmazenado);
                                                            } else {
                                                                return Completable.complete();
                                                            }
                                                        })
                                                        .toObservable()
                                                        .map(x -> questionarioNoSIGAA));
                                    } else {
                                        //Questionário ainda não armazenado (inserir)
                                        Timber.d("Questionário novo: %s", questionarioNoSIGAA.getTitulo());
                                        return mDataManager.inserirQuestionario(questionarioNoSIGAA)
                                                .flatMap(questionarioNovo -> {
                                                    if (questionarioNovo.getDataFim().after(new Date())) {
                                                        //Criar um lembrete
                                                        Lembrete lembrete = new Lembrete(questionarioNovo, mDataManager.getNovoIdNotificacao());
                                                        return mDataManager.inserirLembrete(lembrete)
                                                                .map(lembreteComID -> {
                                                                    if (lembreteComID.getEstado() == Lembrete.ESTADO_INCOMPLETO) {
                                                                        //Notificar novo item
                                                                        mDataManager.notificarQuestionarioNovo(questionarioNovo, lembreteComID, mDataManager.getNovoIdNotificacao());
                                                                        //Agendar a notificação do lembrete
                                                                        mDataManager.agendarNotificacaoLembrete(lembreteComID);
                                                                    }
                                                                    return questionarioNovo;
                                                                });
                                                    } else {
                                                        return Observable.just(questionarioNovo);
                                                    }
                                                });
                                    }
                                })
                                .toList()
                                .toObservable()))
                .flatMapCompletable(listaQuestionarios -> {
                    mTarefaAtual++;
                    mDataManager.notificarSincronizacaoSIGAA(this, disciplina, mTarefaAtual, mTotalTarefas);
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
    final public static int OBSERVABLE_ATUALIZAR_RV_PREVIEWS = 1;
    final public static int OBSERVABLE_ATUALIZAR_RV_LEMBRETES = 2;

    static final PublishSubject<Integer> data = PublishSubject.create();

    public static Observable<Integer> getObservable() {
        return data;
    }
}
