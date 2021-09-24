package com.ifcbrusque.app.ui.home.lembretes;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LembretesPresenter<V extends LembretesContract.LembretesView> extends BasePresenter<V> implements LembretesContract.LembretesPresenter<V> {
    @Inject
    public LembretesPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    private void carregarLembretes(boolean agendarNotificacoes) {
        getCompositeDisposable().add(getDataManager()
                .getLembretesArmazenados()
                .doOnNext(lembretes -> {
                    getMvpView().atualizarRecyclerView(lembretes);

                    if(agendarNotificacoes) {
                        getDataManager().agendarNotificacoesLembretesFuturos(lembretes);
                    }
                })
                .subscribe());
    }

    private void atualizarLembreteDaLista(Lembrete lembreteVelho, Lembrete lembreteAtualizado) {
        List<Lembrete> lembretesNaView = getMvpView().getLembretesNaView();
        lembretesNaView.set(lembretesNaView.indexOf(lembreteVelho), lembreteAtualizado);
        getMvpView().atualizarRecyclerView(lembretesNaView);
    }

    @Override
    public void onViewPronta() {
        int ultimaCategoriaAcessada = getDataManager().getUltimaCategoriaAcessadaHome();
        getMvpView().atualizarCategoriaRecyclerView(ultimaCategoriaAcessada);

        carregarLembretes(true);
    }

    @Override
    public void onLembreteInserido() {
        carregarLembretes(false);
    }

    @Override
    public void onAlternarEstadoLembreteClick(int position) {
        Lembrete lembrete = getMvpView().getLembretesNaView().get(position);

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

                        atualizarLembreteDaLista(lembrete, lembreteNovo);
                    })
                    .subscribe());
        } else {
            //Lembrete que repete (ir para a próxima repetição)
            getDataManager().desagendarNotificacaoLembrete(lembrete);
            getCompositeDisposable().add(getDataManager()
                    .atualizarParaProximaDataLembreteComRepeticao(lembrete.getId())
                    .doOnNext(lembreteReagendado -> {
                        atualizarLembreteDaLista(lembrete, lembreteReagendado);
                    })
                    .subscribe());
        }
    }

    @Override
    public void onExcluirLembreteClick(int position) {
        Lembrete lembrete = getMvpView().getLembretesNaView().get(position);

        getCompositeDisposable().add(getDataManager()
                .deletarLembrete(lembrete)
                .doOnComplete(() -> {
                    getDataManager().desagendarNotificacaoLembrete(lembrete);

                    List<Lembrete> lembretes = getMvpView().getLembretesNaView();
                    lembretes.remove(position);
                    getMvpView().atualizarRecyclerView(lembretes);
                })
                .subscribe());
    }

    @Override
    public void onCategoriaAlterada(int categoria) {
        getDataManager().setUltimaCategoriaAcessadaHome(categoria);
    }
}
