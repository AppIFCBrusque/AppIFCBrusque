package com.ifcbrusque.app.data.db;

import android.content.Context;

import com.ifcbrusque.app.data.db.model.AvaliacaoArmazenavel;
import com.ifcbrusque.app.data.db.model.DisciplinaArmazenavel;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;
import com.ifcbrusque.app.di.ApplicationContext;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
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
    public Observable<Long> inserirLembrete(Lembrete lembrete) {
        return Observable.defer(() -> Observable.just(mAppDatabase.lembreteDao().insert(lembrete)))
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
        List<DisciplinaArmazenavel> disciplinasArmazenaveis = new ArrayList<>();
        for (Disciplina d : disciplinas) {
            disciplinasArmazenaveis.add(new DisciplinaArmazenavel(d));
        }
        Timber.d("Disciplinas a inserir: " + disciplinasArmazenaveis.size());

        return Completable.fromRunnable(() -> mAppDatabase.disciplinaDao().insertAll(disciplinasArmazenaveis))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarDisciplina(Disciplina disciplina) {
        DisciplinaArmazenavel d = new DisciplinaArmazenavel(disciplina);

        return Completable.fromRunnable(() -> mAppDatabase.disciplinaDao().delete(d))
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
    public Completable inserirTarefas(List<Tarefa> tarefas) {
        Timber.d("Tarefas a inserir: " + tarefas.size());
        List<TarefaArmazenavel> tarefasArmazenaveis = new ArrayList<>();
        for (Tarefa t : tarefas) {
            tarefasArmazenaveis.add(new TarefaArmazenavel(t));
        }

        return Completable.fromRunnable(() -> mAppDatabase.tarefaDao().insertAll(tarefasArmazenaveis))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarTarefa(Tarefa tarefa) {
        TarefaArmazenavel t = new TarefaArmazenavel(tarefa);

        return Completable.fromRunnable(() -> mAppDatabase.tarefaDao().delete(t))
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
                                //Deletar as tarefas sem disciplina correspondente
                                Timber.d("Avaliação sem disciplina correspondente: " + avaliacaoArmazenavel.getDescricao());
                                mAppDatabase.avaliacaoDao().delete(avaliacaoArmazenavel);
                            }
                            return Observable.just(a);
                        })
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable inserirAvaliacoes(List<Avaliacao> avaliacoes) {
        Timber.d("Avaliações a inserir: " + avaliacoes.size());
        List<AvaliacaoArmazenavel> avaliacoesArmazenaveis = new ArrayList<>();
        for (Avaliacao a : avaliacoes) {
            avaliacoesArmazenaveis.add(new AvaliacaoArmazenavel(a));
        }

        return Completable.fromRunnable(() -> mAppDatabase.avaliacaoDao().insertAll(avaliacoesArmazenaveis)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Completable deletarAvaliacao(Avaliacao avaliacao) {
        AvaliacaoArmazenavel a = new AvaliacaoArmazenavel(avaliacao);

        return Completable.fromRunnable(() -> mAppDatabase.avaliacaoDao().delete(a))
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
}
