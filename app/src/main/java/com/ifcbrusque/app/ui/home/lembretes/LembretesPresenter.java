package com.ifcbrusque.app.ui.home.lembretes;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.service.SyncService;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;

public class LembretesPresenter<V extends LembretesContract.LembretesView> extends BasePresenter<V> implements LembretesContract.LembretesPresenter<V> {
    @Inject
    public LembretesPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    private void carregarLembretes(boolean agendarNotificacoes) {
        getCompositeDisposable().add(getDataManager()
                .getLembretesArmazenados()
                .doOnNext(lembretes -> {
                    Timber.d("%s lembretes carregados", lembretes.size());
                    getMvpView().setLembretes(lembretes);

                    if (agendarNotificacoes) {
                        getDataManager().agendarNotificacoesLembretesFuturos(lembretes);
                    }
                })
                .subscribe());
    }

    private void atualizarLembrete(Lembrete lembreteAntigo, Lembrete lembreteNovo) {
        List<Lembrete> lembretes = getMvpView().getLembretes();
        lembretes.set(lembretes.indexOf(lembreteAntigo), lembreteNovo);
        getMvpView().setLembretes(lembretes);
    }

    private void anexarDisposableDaSincronizacao() {
        //Se o serviço de sincronização estiver rodando e atualizar os lembretes, ele pode notificar este presenter para atualizar a recycler view
        SyncService.getObservable().subscribe(codigo -> {
            if (codigo == SyncService.OBSERVABLE_ATUALIZAR_RV_LEMBRETES) {
                carregarLembretes(true);
            }
        }, erro -> {
            /* Engolir erro */
        });
    }

    @Override
    public void onViewPronta() {
        int ultimaCategoriaAcessada = getDataManager().getUltimaCategoriaAcessadaHome();
        getMvpView().setCategoria(ultimaCategoriaAcessada);

        carregarLembretes(true);

        anexarDisposableDaSincronizacao();

        //Iniciar sincronização caso já tenha passado mais de um dia desde a última (provavelmente o alarme foi cancelado)
        long horasDesdeUltimaSync = TimeUnit.MILLISECONDS.toHours(new Date().getTime() - getDataManager().getDataUltimaSincronizacaoCompleta().getTime());
        Timber.d("%s horas desde a última sincronização", horasDesdeUltimaSync);
        if (horasDesdeUltimaSync > 24) {
            getDataManager().iniciarSincronizacao();
        } else {
            getDataManager().agendarSincronizacao();
        }
    }

    @Override
    public void onLembreteInserido() {
        carregarLembretes(false);
    }

    @Override
    public void onAlternarEstadoLembreteClick(int position) {
        Lembrete lembrete = (Lembrete) getMvpView().getDadosVisiveis().get(position);

        if (lembrete.getTipoRepeticao() == Lembrete.REPETICAO_SEM) {
            //Lembrete que não repete (simplesmente alterar entre completo/incompleto)
            int novoEstado = (lembrete.getEstado() == Lembrete.ESTADO_INCOMPLETO) ? Lembrete.ESTADO_COMPLETO : Lembrete.ESTADO_INCOMPLETO;

            getCompositeDisposable().add(getDataManager()
                    .alterarEstadoLembrete(lembrete.getId(), novoEstado)
                    .doOnComplete(() -> {
                        Lembrete lembreteNovo = lembrete;
                        lembreteNovo.setEstado(novoEstado);

                        if (novoEstado == Lembrete.ESTADO_INCOMPLETO) {
                            //Agendar
                            if (new Date().before(lembrete.getDataLembrete())) {
                                getDataManager().agendarNotificacaoLembrete(lembrete);
                            }
                        } else {
                            //Desagendar
                            getDataManager().desagendarNotificacaoLembrete(lembrete);
                        }

                        atualizarLembrete(lembrete, lembreteNovo);
                    })
                    .subscribe());
        } else {
            //Lembrete que repete (ir para a próxima repetição)
            getDataManager().desagendarNotificacaoLembrete(lembrete);
            getCompositeDisposable().add(getDataManager()
                    .atualizarParaProximaDataLembreteComRepeticao(lembrete.getId())
                    .doOnNext(lembreteReagendado -> atualizarLembrete(lembrete, lembreteReagendado))
                    .subscribe());
        }
    }

    @Override
    public void onExcluirLembreteClick(int position) {
        Lembrete lembrete = (Lembrete) getMvpView().getDadosVisiveis().get(position);

        getCompositeDisposable().add(getDataManager()
                .deletarLembrete(lembrete)
                .doOnComplete(() -> {
                    getDataManager().desagendarNotificacaoLembrete(lembrete);

                    List<Lembrete> lembretes = getMvpView().getLembretes();
                    lembretes.remove(lembrete);

                    getMvpView().setLembretes(lembretes);
                })
                .subscribe());
    }

    @Override
    public void onCategoriaAlterada(int categoria) {
        getDataManager().setUltimaCategoriaAcessadaHome(categoria);
    }
}
