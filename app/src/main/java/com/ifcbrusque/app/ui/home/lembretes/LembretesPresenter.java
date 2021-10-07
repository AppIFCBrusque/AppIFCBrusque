package com.ifcbrusque.app.ui.home.lembretes;

import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.ifcbrusque.app.ui.home.lembretes.LembretesAdapter.TITULO_AMANHA;
import static com.ifcbrusque.app.ui.home.lembretes.LembretesAdapter.TITULO_ATRASADO;
import static com.ifcbrusque.app.ui.home.lembretes.LembretesAdapter.TITULO_HOJE;
import static com.ifcbrusque.app.ui.home.lembretes.LembretesAdapter.TITULO_NESTA_SEMANA;
import static com.ifcbrusque.app.ui.home.lembretes.LembretesAdapter.TITULO_UM_MES;
import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA;

public class LembretesPresenter<V extends LembretesContract.LembretesView> extends BasePresenter<V> implements LembretesContract.LembretesPresenter<V> {
    @Inject
    public LembretesPresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    private void carregarLembretes(boolean agendarNotificacoes) {
        getCompositeDisposable().add(getDataManager()
                .getLembretesArmazenados()
                .doOnNext(lembretes -> {
                    getMvpView().setLembretesNaView(lembretes);

                    if (agendarNotificacoes) {
                        getDataManager().agendarNotificacoesLembretesFuturos(lembretes);
                    }
                })
                .subscribe());
    }

    private void atualizarLembreteDaLista(Lembrete lembreteVelho, Lembrete lembreteAtualizado) {
        List<Lembrete> lembretes = getMvpView().getLembretesNaView();
        lembretes.set(lembretes.indexOf(lembreteVelho), lembreteAtualizado);
        getMvpView().setLembretesNaView(lembretes);
    }

    /**
     * Pega somente os lembretes dos dados da recycler view e adiciona os cabeçalhos
     */
    private void inserirHeadersNaLista() {
        /*
        Esse código tá bem feio, mas eu não encontrei uma maneira melhor para fazer
         */

        //Pegar somente os lembretes
        List<Lembrete> lembretes = getMvpView().getLembretesNaView();
        //Lista que vai ser inserida no recycler view
        List<Object> dados = new ArrayList<>();

        Calendar hoje = Calendar.getInstance(), amanha = Calendar.getInstance(), primeiroDiaProximaSemana = Calendar.getInstance(), daquiUmMes = Calendar.getInstance(), dataLembrete = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_YEAR, 1);
        primeiroDiaProximaSemana.add(Calendar.WEEK_OF_YEAR, 1);
        primeiroDiaProximaSemana.set(Calendar.DAY_OF_WEEK, primeiroDiaProximaSemana.getActualMinimum(Calendar.DAY_OF_WEEK));
        daquiUmMes.add(Calendar.MONTH, 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMATO_DATA);

        for (int i = 0; i < lembretes.size(); i++) {
            Lembrete l = lembretes.get(i);

            //Adicionar cabeçalho
            dataLembrete.setTime(l.getDataLembrete());
            if (hoje.getTime().after(dataLembrete.getTime())) {
                if (!dados.contains(TITULO_ATRASADO)) {
                    dados.add(TITULO_ATRASADO);
                }
            } else if (dataLembrete.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) && dataLembrete.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR)) {
                if (!dados.contains(TITULO_HOJE)) {
                    dados.add(TITULO_HOJE);
                }
            } else if (dataLembrete.get(Calendar.YEAR) == amanha.get(Calendar.YEAR) && dataLembrete.get(Calendar.DAY_OF_YEAR) == amanha.get(Calendar.DAY_OF_YEAR)) {
                if (!dados.contains(TITULO_AMANHA)) {
                    dados.add(TITULO_AMANHA);
                }
            } else if (dataLembrete.before(primeiroDiaProximaSemana)) {
                if (!dados.contains(TITULO_NESTA_SEMANA)) {
                    dados.add(TITULO_NESTA_SEMANA);
                }
            } else if (dataLembrete.before(daquiUmMes)) {
                if (!dados.contains(TITULO_UM_MES)) {
                    dados.add(TITULO_UM_MES);
                }
            } else if (dataLembrete.after(daquiUmMes)) {
                String stringDataLembrete = dateFormat.format(dataLembrete.getTime()).split(" ")[0];
                if (!dados.contains(stringDataLembrete)) {
                    dados.add(stringDataLembrete);
                }
            }

            //Adicionar lembrete
            dados.add(l);
        }

        getMvpView().setDadosNaView(dados);
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
    public void onLembretesAtualizados() {
        inserirHeadersNaLista();
    }

    @Override
    public void onAlternarEstadoLembreteClick(int position) {
        Lembrete lembrete = (Lembrete) getMvpView().getDadosNaView().get(position);

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
                    .doOnNext(lembreteReagendado -> atualizarLembreteDaLista(lembrete, lembreteReagendado))
                    .subscribe());
        }
    }

    @Override
    public void onExcluirLembreteClick(int position) {
        Lembrete lembrete = (Lembrete) getMvpView().getDadosNaView().get(position);

        getCompositeDisposable().add(getDataManager()
                .deletarLembrete(lembrete)
                .doOnComplete(() -> {
                    getDataManager().desagendarNotificacaoLembrete(lembrete);

                    List<Lembrete> lembretes = getMvpView().getLembretesNaView();
                    lembretes.remove(lembretes.indexOf(lembrete));

                    getMvpView().setLembretesNaView(lembretes);
                })
                .subscribe());
    }

    @Override
    public void onCategoriaAlterada(int categoria) {
        getDataManager().setUltimaCategoriaAcessadaHome(categoria);
    }
}
