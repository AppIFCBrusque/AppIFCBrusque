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
import com.imawa.sigaaforkotlin.entities.Avaliacao;
import com.imawa.sigaaforkotlin.entities.Disciplina;
import com.imawa.sigaaforkotlin.entities.Questionario;
import com.imawa.sigaaforkotlin.entities.Tarefa;

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
    private final AppDatabase mAppDatabase;

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
                    if (!previewsNoArmazenamento.stream().anyMatch(_p -> _p.getUrlNoticia().equals(p.getUrlNoticia()))) {
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
    public Observable<List<Lembrete>> getLembrete(Avaliacao avaliacao) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().getLembretes(Long.toString(avaliacao.getId()))))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Lembrete>> getLembrete(Tarefa tarefa) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().getLembretes(tarefa.getId())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Lembrete>> getLembrete(Questionario questionario) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().getLembretes(Long.toString(questionario.getId()))))
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
        return Observable.defer(() -> Observable.just(mAppDatabase.disciplinaDao().getAll()))
                .flatMap(disciplinas -> Observable.just(mAppDatabase.avaliacaoDao().getAll())
                        .flatMapIterable(avaliacoes -> avaliacoes)
                        .map(avaliacaoArmazenavel -> {
                            List<Avaliacao> avaliacao = new ArrayList<>();
                            if (disciplinas.stream().anyMatch(d -> d.getFrontEndIdTurma().equals(avaliacaoArmazenavel.getDisciplinaFrontEndIdTurma()))) {
                                //Avaliação posssui disciplina correspondente salva (dá para criar)
                                Disciplina disciplinaDaAvaliacao = disciplinas.stream().filter(d -> d.getFrontEndIdTurma().equals(avaliacaoArmazenavel.getDisciplinaFrontEndIdTurma())).findFirst().get().getDisciplina();
                                avaliacao.add(avaliacaoArmazenavel.getAvaliacao(disciplinaDaAvaliacao));
                            } else {
                                //Avaliação não posssui disciplina correspondente salva (não dá para criar)
                                Timber.d("Avaliação sem disciplina correspondente: %s", avaliacaoArmazenavel.getDescricao());
                                mAppDatabase.avaliacaoDao().delete(avaliacaoArmazenavel);
                            }
                            return avaliacao; //Se não houver item na lista, não vai ser "pulado" no .toList()
                        })
                        .flatMapIterable(list -> list)
                        .toList()
                        .toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Avaliacao> inserirAvaliacao(Avaliacao avaliacao) {
        return Observable.just(new AvaliacaoArmazenavel(avaliacao))
                .map(avaliacaoArmazenavel -> {
                    mAppDatabase.avaliacaoDao().insert(avaliacaoArmazenavel);
                    return avaliacao;
                })
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
    public Observable<Integer> atualizarAvaliacao(Avaliacao avaliacao) {
        return Observable.just(new AvaliacaoArmazenavel(avaliacao))
                .map(avaliacaoArmazenavel -> mAppDatabase.avaliacaoDao().atualizarAvaliacao(avaliacaoArmazenavel))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TarefaArmazenavel> getTarefaArmazenavel(String id) {
        return Observable.defer(() -> Observable.just(mAppDatabase.tarefaDao().getTarefa(id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<TarefaArmazenavel> getTarefaArmazenavel(Lembrete lembrete) {
        return getTarefaArmazenavel(lembrete.getIdObjetoAssociado());
    }

    @Override
    public Observable<List<Tarefa>> getAllTarefas() {
        return Observable.defer(() -> Observable.just(mAppDatabase.disciplinaDao().getAll()))
                .flatMap(disciplinas -> Observable.just(mAppDatabase.tarefaDao().getAll())
                        .flatMapIterable(tarefas -> tarefas)
                        .map(tarefaArmazenavel -> {
                            List<Tarefa> tarefa = new ArrayList<>();
                            if (disciplinas.stream().anyMatch(d -> d.getFrontEndIdTurma().equals(tarefaArmazenavel.getDisciplinaFrontEndIdTurma()))) {
                                //Tarefa posssui disciplina correspondente salva (dá para criar)
                                Disciplina disciplinaDaTarefa = disciplinas.stream().filter(d -> d.getFrontEndIdTurma().equals(tarefaArmazenavel.getDisciplinaFrontEndIdTurma())).findFirst().get().getDisciplina();
                                tarefa.add(tarefaArmazenavel.getTarefa(disciplinaDaTarefa));
                            } else {
                                //Tarefa não posssui disciplina correspondente salva (não dá para criar)
                                Timber.d("Tarefa sem disciplina correspondente: %s", tarefaArmazenavel.getTitulo());
                                mAppDatabase.tarefaDao().delete(tarefaArmazenavel);
                            }
                            return tarefa; //Se não houver item na lista, não vai ser "pulado" no .toList()
                        })
                        .flatMapIterable(list -> list)
                        .toList()
                        .toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Tarefa> inserirTarefa(Tarefa tarefa) {
        return Observable.just(new TarefaArmazenavel(tarefa))
                .map(tarefaArmazenavel -> {
                    mAppDatabase.tarefaDao().insert(tarefaArmazenavel);
                    return tarefa;
                })
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
    public Observable<Integer> atualizarTarefa(Tarefa tarefa) {
        return Observable.just(new TarefaArmazenavel(tarefa))
                .map(tarefaArmazenavel -> mAppDatabase.tarefaDao().atualizarTarefa(tarefaArmazenavel))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Questionario>> getAllQuestionarios() {
        return Observable.defer(() -> Observable.just(mAppDatabase.disciplinaDao().getAll()))
                .flatMap(disciplinas -> Observable.just(mAppDatabase.questionarioDao().getAll())
                        .flatMapIterable(questionarios -> questionarios)
                        .map(questionarioArmazenavel -> {
                            List<Questionario> questionario = new ArrayList<>();
                            if (disciplinas.stream().anyMatch(d -> d.getFrontEndIdTurma().equals(questionarioArmazenavel.getDisciplinaFrontEndIdTurma()))) {
                                //Questionário posssui disciplina correspondente salva (dá para criar)
                                Disciplina disciplinaDoQuestionario = disciplinas.stream().filter(d -> d.getFrontEndIdTurma().equals(questionarioArmazenavel.getDisciplinaFrontEndIdTurma())).findFirst().get().getDisciplina();
                                questionario.add(questionarioArmazenavel.getQuestionario(disciplinaDoQuestionario));
                            } else {
                                //Questionário não posssui disciplina correspondente salva (não dá para criar)
                                Timber.d("Questionário sem disciplina correspondente: %s", questionarioArmazenavel.getTitulo());
                                mAppDatabase.questionarioDao().delete(questionarioArmazenavel);
                            }
                            return questionario; //Se não houver item na lista, não vai ser "pulado" no .toList()
                        })
                        .flatMapIterable(list -> list)
                        .toList()
                        .toObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Questionario> inserirQuestionario(Questionario questionario) {
        return Observable.just(new QuestionarioArmazenavel(questionario))
                .map(questionarioArmazenavel -> {
                    mAppDatabase.questionarioDao().insert(questionarioArmazenavel);
                    return questionario;
                })
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

    @Override
    public Observable<Integer> atualizarQuestionario(Questionario questionario) {
        return Observable.just(new QuestionarioArmazenavel(questionario))
                .map(questionarioArmazenavel -> mAppDatabase.questionarioDao().atualizarQuestionario(questionarioArmazenavel))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<QuestionarioArmazenavel> getQuestionarioArmazenavel(long id) {
        return Observable.defer(() -> Observable.just(mAppDatabase.questionarioDao().getQuestionario(id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<QuestionarioArmazenavel> getQuestionarioArmazenavel(Lembrete lembrete) {
        return getQuestionarioArmazenavel(Long.parseLong(lembrete.getIdObjetoAssociado()));
    }

    @Override
    public Completable deletarTudoSIGAA() {
        return Completable.fromRunnable(() -> {
            mAppDatabase.avaliacaoDao().deleteAll();
            mAppDatabase.questionarioDao().deleteAll();
            mAppDatabase.tarefaDao().deleteAll();
            mAppDatabase.disciplinaDao().deleteAll();
            mAppDatabase.lembreteDao().deleteAllLembretesSIGAA();
            Timber.d("Itens do SIGAA deletados");
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
