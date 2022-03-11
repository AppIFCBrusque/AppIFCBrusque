package com.ifcbrusque.app.ui.lembrete;

import android.text.Editable;
import android.text.TextWatcher;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class InserirLembretePresenter<V extends InserirLembreteContract.InserirLembreteView> extends BasePresenter<V> implements InserirLembreteContract.InserirLembretePresenter<V> {
    private Lembrete mLembrete;

    //Objeto armazenado associado ao lembrete
    private TarefaArmazenavel mTarefa;

    private final Calendar mCalendar = Calendar.getInstance();

    @Inject
    public InserirLembretePresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    private void criarLembreteNovo() {
        //Definir a data e hora padrão dos botões de data e hora
        //A hora é arredondada para cima. Exemplo: 17:23 -> 18::00; 02:00 -> 03:00
        mCalendar.add(Calendar.HOUR_OF_DAY, 1);
        mCalendar.set(Calendar.MINUTE, 0);
        mCalendar.set(Calendar.SECOND, 0);

        mLembrete = new Lembrete(Lembrete.LEMBRETE_PESSOAL, "", "", "", "", "", mCalendar.getTime(), Lembrete.REPETICAO_SEM, 0, Lembrete.ESTADO_INCOMPLETO, getDataManager().getNovoIdNotificacao());

        getMvpView().setTitulo(mLembrete.getTitulo());
        getMvpView().esconderDescricao();
        getMvpView().setAnotacoes(mLembrete.getAnotacoes());
        getMvpView().mostrarAnotacoes();
        getMvpView().setTextoBotaoData(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        getMvpView().setTextoBotaoHora(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
        getMvpView().setTextoBotaoRepeticao(Lembrete.REPETICAO_SEM);
    }

    private void carregarLembreteArmazenado(long idLembrete) {
        getCompositeDisposable().add(getDataManager()
                .getLembrete(idLembrete)
                .flatMapCompletable(lembrete -> {
                    mLembrete = lembrete;

                    mCalendar.setTime(mLembrete.getDataLembrete());

                    getMvpView().setTitulo(mLembrete.getTitulo());
                    getMvpView().setDescricao(mLembrete.getDescricao());
                    getMvpView().setAnotacoes(mLembrete.getAnotacoes());
                    getMvpView().setTextoBotaoData(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                    getMvpView().setTextoBotaoHora(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
                    getMvpView().setTextoBotaoRepeticao(mLembrete.getTipoRepeticao());

                    if (lembrete.getTipo() == Lembrete.LEMBRETE_PESSOAL) {
                        //Lembrete pessoal
                        getMvpView().esconderDescricao();
                        getMvpView().mostrarAnotacoes();
                        return Completable.complete();
                    } else {
                        //Lembrete do SIGAA
                        getMvpView().setTextoTipoLembrete(Lembrete.getIdDaStringTipo(lembrete.getTipo()));
                        getMvpView().setTextoDisciplina(lembrete.getNomeDisciplina());
                        getMvpView().exibirTipoLembrete();
                        getMvpView().exibirDisciplina();
                        //Impedir o usuário de editar as informações do lembrete e mostrar o campo de anotações
                        getMvpView().desativarTitulo();
                        getMvpView().desativarDescricao();
                        getMvpView().mostrarAnotacoes();
                        getMvpView().desativarBotaoData();
                        getMvpView().desativarBotaoHora();
                        getMvpView().desativarBotaoRepeticao();


                        //Carregar o objeto associado
                        switch (lembrete.getTipo()) {
                            case Lembrete.LEMBRETE_TAREFA:
                                return getDataManager().getTarefaArmazenavel(lembrete)
                                        .flatMapCompletable(tarefaArmazenavel -> {
                                            mTarefa = tarefaArmazenavel;

                                            if (mTarefa.getUrlDownload().length() > 0) {
                                                getMvpView().exibirBotaoArquivo();
                                            }

                                            return Completable.complete();
                                        });

                            default:
                                return Completable.complete();
                        }
                    }
                })
                .subscribe());
    }

    /**
     * Armazena um lembrete com as informações salvas neste presenter e fecha a activity
     * <p>
     * O lembrete precisa ter um título válido. Se não tiver, mostra um toast
     */
    private void salvarLembrete() {
        mLembrete.setTitulo(getMvpView().getTitulo().trim());
        mLembrete.setDescricao(getMvpView().getDescricao().trim());
        mLembrete.setAnotacoes(getMvpView().getAnotacoes().trim());

        if (mLembrete.getTitulo().length() > 0) {

            if (mLembrete.getId() != 0) {
                //Atualizar lembrete existente
                mLembrete.setEstado(Lembrete.ESTADO_INCOMPLETO);

                getDataManager().agendarNotificacaoLembreteSeFuturo(mLembrete);

                getCompositeDisposable().add(getDataManager()
                        .atualizarLembrete(mLembrete)
                        .doOnComplete(() -> getMvpView().fecharActivity(true))
                        .subscribe());
            } else {
                //Armazenar lembrete novo
                getCompositeDisposable().add(getDataManager()
                        .inserirLembrete(mLembrete)
                        .map(lembrete -> {
                            getDataManager().agendarNotificacaoLembreteSeFuturo(lembrete);
                            return true;
                        })
                        .doOnComplete(() -> getMvpView().fecharActivity(true))
                        .subscribe());

            }
        } else {
            getMvpView().showMessage(R.string.erro_titulo_lembrete_vazio);
        }
    }

    @Override
    public void onViewPronta(long idLembrete) {
        if (idLembrete != -1) {
            carregarLembreteArmazenado(idLembrete);
        } else {
            criarLembreteNovo();
        }
    }

    @Override
    public void onBotaoArquivoClick() {
        getMvpView().abrirNavegador(mTarefa.getUrlDownload());
    }

    @Override
    public void onBotaoDataClick() {
        getMvpView().exibirDialogoData(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onBotaoTempoClick() {
        getMvpView().exibirDialogoTempo(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
    }

    @Override
    public void onBotaoRepeticaoClick() {
        getMvpView().exibirDialogoRepeticao();
    }

    @Override
    public void onBotaoSalvarClick() {
        salvarLembrete();
    }

    /**
     * Executado quando o usuário seleciona uma data no diálogo que aparece ao clicar no botão de data e clica em "OK"
     * Insira as variáveis diretamente obtidas pelo dialogo
     * <p>
     * Armazena os valores e muda o texto do botão
     */
    @Override
    public void onDataSelecionada(int ano, int mes, int dia) {
        mCalendar.set(Calendar.YEAR, ano);
        mCalendar.set(Calendar.MONTH, mes);
        mCalendar.set(Calendar.DAY_OF_MONTH, dia);
        mLembrete.setDataLembrete(mCalendar.getTime());

        getMvpView().setTextoBotaoData(ano, mes, dia);
    }

    /**
     * Executado quando o usuário seleciona uma hora no diálogo que aparece ao clicar no botão de hora e clica em "OK"
     * Insira as variáveis diretamente obtidas pelo dialogo
     * <p>
     * Armazena os valores e muda o texto do botão
     */
    @Override
    public void onTempoSelecionado(int hora, int minuto) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hora);
        mCalendar.set(Calendar.MINUTE, minuto);
        mLembrete.setDataLembrete(mCalendar.getTime());

        getMvpView().setTextoBotaoHora(hora, minuto);
    }

    /**
     * Executado quando o usuário seleciona uma opção de repetição
     * Define os valores do lembrete e muda o texto do botão
     *
     * @param tipoRepeticao               tipo de repetição (utilize os valores definidos na classe Lembrete)
     * @param tempoRepeticaoPersonalizada (intervalo utilizado para a repetição personalizada)
     */
    @Override
    public void onRepeticaoSelecionada(int tipoRepeticao, int tempoRepeticaoPersonalizada) {
        mLembrete.setTipoRepeticao(tipoRepeticao);
        mLembrete.setTempoRepeticaoPersonalizada(tempoRepeticaoPersonalizada);

        getMvpView().setTextoBotaoRepeticao(tipoRepeticao);
    }

    @Override
    public TextWatcher onTextoTituloChanged() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /* */
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Desativar o botão de salvar caso o título esteja vazio
                if (charSequence.length() == 0) {
                    getMvpView().desativarBotaoSalvar();
                } else {
                    getMvpView().ativarBotaoSalvar();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /* */
            }
        };
    }
}
