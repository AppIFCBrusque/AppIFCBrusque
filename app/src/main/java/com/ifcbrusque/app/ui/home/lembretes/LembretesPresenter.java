package com.ifcbrusque.app.ui.home.lembretes;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.service.SyncService;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import timber.log.Timber;

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
                    Timber.d("%s lembretes carregados", lembretes.size());
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

        Calendar hoje = Calendar.getInstance();
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_YEAR, 1);
        amanha.set(Calendar.HOUR_OF_DAY, 23);
        amanha.set(Calendar.MINUTE, 59);
        amanha.set(Calendar.SECOND, 59);
        amanha.set(Calendar.MILLISECOND, 999);
        Calendar daquiUmMes = Calendar.getInstance();
        daquiUmMes.add(Calendar.MONTH, 1);

        String tituloAtrasado = getMvpView().getString(R.string.secao_atrasado);
        String tituloHoje = getMvpView().getString(R.string.secao_hoje);
        String tituloAmanha = getMvpView().getString(R.string.secao_amanha);
        String tituloEmXDias = getMvpView().getString(R.string.secao_em_dias);
        String tituloApos1Mes = getMvpView().getString(R.string.secao_apos_um_mes);
        String tituloAposXMeses = getMvpView().getString(R.string.secao_apos_meses);

        for (int i = 0; i < lembretes.size(); i++) {
            Lembrete lembrete = lembretes.get(i);

            //Adicionar cabeçalho
            Calendar dataLembrete = Calendar.getInstance();
            dataLembrete.setTime(lembrete.getDataLembrete());

            if (dataLembrete.after(daquiUmMes)) {
                //Meses
                int meses = dataLembrete.get(Calendar.MONTH) - hoje.get(Calendar.MONTH);
                String titulo;
                if (meses == 1) {
                    titulo = tituloApos1Mes;
                } else {
                    titulo = String.format(tituloAposXMeses, meses);
                }

                if (!dados.contains(titulo)) {
                    dados.add(titulo);
                }
            } else if (dataLembrete.after(amanha)) {
                //Em x dias
                int dias = dataLembrete.get(Calendar.DAY_OF_YEAR) - hoje.get(Calendar.DAY_OF_YEAR);
                String titulo = String.format(tituloEmXDias, dias);
                if (!dados.contains(titulo)) {
                    dados.add(titulo);
                }
            } else if (dataLembrete.get(Calendar.YEAR) == amanha.get(Calendar.YEAR) && dataLembrete.get(Calendar.DAY_OF_YEAR) == amanha.get(Calendar.DAY_OF_YEAR)) {
                //Amanhã
                if (!dados.contains(tituloAmanha)) {
                    dados.add(tituloAmanha);
                }
            } else if (dataLembrete.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) && dataLembrete.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR)) {
                //Hoje
                if (!dados.contains(tituloHoje)) {
                    dados.add(tituloHoje);
                }
            } else if (hoje.getTime().after(dataLembrete.getTime())) {
                //Atrasado
                if (!dados.contains(tituloAtrasado)) {
                    dados.add(tituloAtrasado);
                }
            }

            //Adicionar lembrete
            dados.add(lembrete);
        }

        getMvpView().setDadosNaView(dados);
    }

    @Override
    public void onViewPronta() {
        int ultimaCategoriaAcessada = getDataManager().getUltimaCategoriaAcessadaHome();
        getMvpView().atualizarCategoriaRecyclerView(ultimaCategoriaAcessada);

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
                    lembretes.remove(lembrete);

                    getMvpView().setLembretesNaView(lembretes);
                })
                .subscribe());
    }

    @Override
    public void onCategoriaAlterada(int categoria) {
        getDataManager().setUltimaCategoriaAcessadaHome(categoria);
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
}
