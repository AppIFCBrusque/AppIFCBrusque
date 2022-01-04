package com.ifcbrusque.app.data.db;

import android.content.Context;

import com.ifcbrusque.app.data.db.model.AvaliacaoArmazenavel;
import com.ifcbrusque.app.data.db.model.DisciplinaArmazenavel;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.db.model.QuestionarioArmazenavel;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;
import com.ifcbrusque.app.di.ApplicationContext;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;

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
import timber.log.Timber;

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
    public Observable<Lembrete> inserirLembrete(Lembrete lembrete) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().insert(lembrete)))
                .map(id -> {
                    lembrete.setId(id);
                    return lembrete;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarLembrete(Lembrete lembrete) {
        return Completable.fromRunnable(() -> mAppDatabase.lembreteDao().delete(lembrete))
                .subscribeOn(Schedulers.io())
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
        return Completable.fromRunnable(() -> mAppDatabase.lembreteDao().alterarEstadoLembrete(id, novoEstado))
                .subscribeOn(Schedulers.io())
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
            Timber.d("Nova data " + c.get(Calendar.HOUR_OF_DAY) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH));
            lembrete.setDataLembrete(c.getTime());

            mAppDatabase.lembreteDao().atualizarLembrete(lembrete);
            return Observable.just(lembrete);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable inserirDisciplinas(List<Disciplina> disciplinas) {
        return Observable.fromIterable(disciplinas)
                .map(disciplina -> new DisciplinaArmazenavel(disciplina))
                .toList()
                .flatMapCompletable(disciplinasArmazenaveis -> Completable.fromRunnable(() -> {
                    Timber.d("Disciplinas a inserir: %s", disciplinasArmazenaveis.size());
                    mAppDatabase.disciplinaDao().insertAll(disciplinasArmazenaveis);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarDisciplina(Disciplina disciplina) {
        return Observable.just(disciplina)
                .map(d -> new DisciplinaArmazenavel(d))
                .flatMapCompletable(disciplinaArmazenavel -> Completable.fromRunnable(() -> {
                    Timber.d("Disciplina a deletar: %s", disciplinaArmazenavel.getNome());
                    mAppDatabase.disciplinaDao().delete(disciplinaArmazenavel);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Disciplina>> getDisciplinas(String frontEndIdTurma) {
        return Observable.defer(() -> Observable.just(mAppDatabase.disciplinaDao().getDisciplinas(frontEndIdTurma)))
                .flatMap(list -> Observable.fromIterable(list)
                        .map(disciplinaArmazenavel -> disciplinaArmazenavel.getDisciplina())
                        .toList()
                        .toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Disciplina>> getAllDisciplinas() {
        return Observable.defer(() -> Observable.just(mAppDatabase.disciplinaDao().getAll()))
                .flatMap(list -> Observable.fromIterable(list)
                        .map(disciplinaArmazenavel -> disciplinaArmazenavel.getDisciplina())
                        .toList()
                        .toObservable()
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Avaliacao>> getAllAvaliacoes() {
        return Observable.defer(() -> Observable.just(mAppDatabase.avaliacaoDao().getAll()))
                .flatMap(list -> Observable.fromIterable(list)
                        .flatMap(avaliacaoArmazenavel -> {
                            List<DisciplinaArmazenavel> disciplinasDaAvaliacao = mAppDatabase.disciplinaDao().getDisciplinas(avaliacaoArmazenavel.getDisciplinaFrontEndIdTurma());
                            List<Avaliacao> a = new ArrayList<>();

                            if (disciplinasDaAvaliacao.size() > 0) {
                                a.add(avaliacaoArmazenavel.getAvaliacao(disciplinasDaAvaliacao.get(0).getDisciplina()));
                            } else {
                                //Deletar as avaliações sem disciplina correspondente
                                Timber.d("Avaliação sem disciplina correspondente: " + avaliacaoArmazenavel.getDescricao());
                                mAppDatabase.avaliacaoDao().delete(avaliacaoArmazenavel);
                            }
                            return Observable.just(a);
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param avaliacoesParaInserir
     * @return avaliações que não estavam armazenadas
     */
    @Override
    public Observable<List<Avaliacao>> inserirAvaliacoes(List<Avaliacao> avaliacoesParaInserir) {
        return Observable.fromIterable(avaliacoesParaInserir)
                .map(avaliacao -> new AvaliacaoArmazenavel(avaliacao))
                .toList()
                .map(avaliacoesArmazenaveis -> {
                    List<AvaliacaoArmazenavel> avaliacoesNoDB = mAppDatabase.avaliacaoDao().getAll();
                    List<AvaliacaoArmazenavel> avaliacoesNovas = new ArrayList<>();

                    for (AvaliacaoArmazenavel avaliacao : avaliacoesArmazenaveis) {
                        if (avaliacoesNoDB.stream().anyMatch(a -> a.getIdNoSIGAA() == avaliacao.getIdNoSIGAA())) {
                            //Avaliação já armazenada (atualizar)
                            mAppDatabase.avaliacaoDao().atualizarAvaliacao(avaliacao);
                        } else {
                            //Avaliação não armazenada (inserir)
                            mAppDatabase.avaliacaoDao().insert(avaliacao);
                            avaliacoesNovas.add(avaliacao);
                        }
                    }

                    return avaliacoesNovas;
                })
                .flatMapObservable(avaliacoesNovas -> Observable.fromIterable(avaliacoesParaInserir)
                        .filter(avaliacao -> avaliacoesNovas.stream().anyMatch(avaliacaoNova -> avaliacaoNova.getIdNoSIGAA() == avaliacao.getId()))
                        .toList()
                        .toObservable()
                ) //Essa parte serve para remover da lista inicial os itens que não são novos (isso aqui é para não precisar converter do formato do banco de dados para o formato da API)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarAvaliacao(Avaliacao avaliacao) {
        return Observable.just(avaliacao)
                .map(a -> new AvaliacaoArmazenavel(a))
                .flatMapCompletable(avaliacaoArmazenavel -> Completable.fromRunnable(() -> {
                    Timber.d("Avaliação a deletar: %s", avaliacaoArmazenavel.getDescricao());
                    mAppDatabase.avaliacaoDao().delete(avaliacaoArmazenavel);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Tarefa>> getAllTarefas() {
        return Observable.defer(() -> Observable.just(mAppDatabase.tarefaDao().getAll()))
                .flatMap(list -> Observable.fromIterable(list)
                        .flatMap(tarefaArmazenavel -> {
                            List<DisciplinaArmazenavel> disciplinasDaTarefa = mAppDatabase.disciplinaDao().getDisciplinas(tarefaArmazenavel.getDisciplinaFrontEndIdTurma());
                            List<Tarefa> t = new ArrayList<>();

                            if (disciplinasDaTarefa.size() > 0) {
                                t.add(tarefaArmazenavel.getTarefa(disciplinasDaTarefa.get(0).getDisciplina()));
                            } else {
                                //Deletar as tarefas sem disciplina correspondente
                                Timber.d("Tarefa sem disciplina correspondente: " + tarefaArmazenavel.getTitulo());
                                mAppDatabase.tarefaDao().delete(tarefaArmazenavel);
                            }
                            return Observable.just(t);
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param tarefasParaInserir
     * @return tarefas que não estavam armazenadas
     */
    @Override
    public Observable<List<Tarefa>> inserirTarefas(List<Tarefa> tarefasParaInserir) {
        return Observable.fromIterable(tarefasParaInserir)
                .map(tarefa -> new TarefaArmazenavel(tarefa))
                .toList()
                .map(tarefasArmazenaveis -> {
                    List<TarefaArmazenavel> tarefasNoDB = mAppDatabase.tarefaDao().getAll();
                    List<TarefaArmazenavel> tarefasNovas = new ArrayList<>();

                    for (TarefaArmazenavel tarefa : tarefasArmazenaveis) {
                        if (tarefasNoDB.stream().anyMatch(t -> t.getIdNoSIGAA().equals(tarefa.getIdNoSIGAA()))) {
                            //Tarefa já armazenada (atualizar)
                            mAppDatabase.tarefaDao().atualizarTarefa(tarefa);
                        } else {
                            //Tarefa não armazenada (inserir)
                            mAppDatabase.tarefaDao().insert(tarefa);
                            tarefasNovas.add(tarefa);
                        }
                    }

                    return tarefasNovas;
                })
                .flatMapObservable(tarefasNovas -> Observable.fromIterable(tarefasParaInserir)
                        .filter(tarefa -> tarefasNovas.stream().anyMatch(tarefaNova -> tarefaNova.getIdNoSIGAA().equals(tarefa.getId())))
                        .toList()
                        .toObservable()
                ) //Essa parte serve para remover da lista inicial os itens que não são novos (isso aqui é para não precisar converter do formato do banco de dados para o formato da API)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarTarefa(Tarefa tarefa) {
        return Observable.just(tarefa)
                .map(t -> new TarefaArmazenavel(t))
                .flatMapCompletable(tarefaArmazenavel -> Completable.fromRunnable(() -> {
                    Timber.d("Tarefa a deletar: %s", tarefaArmazenavel.getTitulo());
                    mAppDatabase.tarefaDao().delete(tarefaArmazenavel);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Questionario>> getAllQuestionarios() {
        return Observable.defer(() -> Observable.just(mAppDatabase.questionarioDao().getAll()))
                .flatMap(list -> Observable.fromIterable(list)
                        .flatMap(questionarioArmazenavel -> {
                            List<DisciplinaArmazenavel> disciplinasDoQuestionario = mAppDatabase.disciplinaDao().getDisciplinas(questionarioArmazenavel.getDisciplinaFrontEndIdTurma());
                            List<Questionario> q = new ArrayList<>();

                            if (disciplinasDoQuestionario.size() > 0) {
                                q.add(questionarioArmazenavel.getQuestionario(disciplinasDoQuestionario.get(0).getDisciplina()));
                            } else {
                                //Deletar os questionários sem disciplina correspondente
                                Timber.d("Questionário sem disciplina correspondente: " + questionarioArmazenavel.getTitulo());
                                mAppDatabase.questionarioDao().delete(questionarioArmazenavel);
                            }
                            return Observable.just(q);
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @param questionariosParaInserir
     * @return questionários que não estavam armazenados
     */
    @Override
    public Observable<List<Questionario>> inserirQuestionarios(List<Questionario> questionariosParaInserir) {
        return Observable.fromIterable(questionariosParaInserir)
                .map(questionario -> new QuestionarioArmazenavel(questionario))
                .toList()
                .map(questionariosArmazenaveis -> {
                    List<QuestionarioArmazenavel> questionariosNoDB = mAppDatabase.questionarioDao().getAll();
                    List<QuestionarioArmazenavel> questionariosNovos = new ArrayList<>();

                    for (QuestionarioArmazenavel questionario : questionariosArmazenaveis) {
                        if (questionariosNoDB.stream().anyMatch(q -> q.getIdNoSIGAA() == questionario.getIdNoSIGAA())) {
                            //Questionário já armazenado (atualizar)
                            mAppDatabase.questionarioDao().atualizarQuestionario(questionario);
                        } else {
                            //Questionário não armazenado (inserir)
                            mAppDatabase.questionarioDao().insert(questionario);
                            questionariosNovos.add(questionario);
                        }
                    }

                    return questionariosNovos;
                })
                .flatMapObservable(questionariosNovos -> Observable.fromIterable(questionariosParaInserir)
                        .filter(questionario -> questionariosNovos.stream().anyMatch(questionarioNovo -> questionarioNovo.getIdNoSIGAA() == questionario.getId()))
                        .toList()
                        .toObservable()
                ) //Essa parte serve para remover da lista inicial os itens que não são novos (isso aqui é para não precisar converter do formato do banco de dados para o formato da API)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarQuestionario(Questionario questionario) {
        return Observable.just(questionario)
                .map(q -> new QuestionarioArmazenavel(q))
                .flatMapCompletable(questionarioArmazenavel -> Completable.fromRunnable(() -> {
                    Timber.d("Questionario a deletar: %s", questionarioArmazenavel.getTitulo());
                    mAppDatabase.questionarioDao().delete(questionarioArmazenavel);
                }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
