package com.ifcbrusque.app.ui.lembrete;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.DataManager;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.BasePresenter;

import java.util.Calendar;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class InserirLembretePresenter<V extends InserirLembreteContract.InserirLembreteView> extends BasePresenter<V> implements InserirLembreteContract.InserirLembretePresenter<V> {
    private Lembrete mLembrete;
    private Calendar mCalendar = Calendar.getInstance();

    @Inject
    public InserirLembretePresenter(DataManager dataManager, CompositeDisposable compositeDisposable) {
        super(dataManager, compositeDisposable);
    }

    /**
     * Armazena um lembrete com as informações salvas neste presenter e fecha a activity
     * <p>
     * O lembrete precisa ter um título válido. Se não tiver, mostra um toast
     */
    private void salvarLembrete() {
        mLembrete.setTitulo(getMvpView().getTitulo());
        mLembrete.setDescricao(getMvpView().getDescricao());

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
            //Carregar lembrete armazenado no banco de dados
            getCompositeDisposable().add(getDataManager()
                    .getLembrete(idLembrete)
                    .doOnNext(lembrete -> {
                        mLembrete = lembrete;

                        mCalendar.setTime(mLembrete.getDataLembrete());

                        getMvpView().setTitulo(mLembrete.getTitulo());
                        getMvpView().setDescricao(mLembrete.getDescricao());
                        getMvpView().setTextoBotaoData(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
                        getMvpView().setTextoBotaoHora(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
                        getMvpView().setTextoBotaoRepeticao(mLembrete.getTipoRepeticao());

                        if (lembrete.getTipo() != Lembrete.LEMBRETE_PESSOAL) {
                            getMvpView().desativarTitulo();
                            getMvpView().desativarDescricao();
                            getMvpView().desativarBotaoData();
                            getMvpView().desativarBotaoHora();
                            getMvpView().desativarBotaoRepeticao();
                        }
                    })
                    .subscribe());
        } else {
            //Definir a data e hora padrão dos botões de data e hora
            //A hora é arredondada para cima. Exemplo: 17:23 -> 18::00; 02:00 -> 03:00
            mCalendar.add(Calendar.HOUR_OF_DAY, 1);
            mCalendar.set(Calendar.MINUTE, 0);
            mCalendar.set(Calendar.SECOND, 0);

            mLembrete = new Lembrete(Lembrete.LEMBRETE_PESSOAL, "", "", "", mCalendar.getTime(), Lembrete.REPETICAO_SEM, 0, Lembrete.ESTADO_INCOMPLETO, getDataManager().getNovoIdNotificacao());

            getMvpView().setTitulo(mLembrete.getTitulo());
            getMvpView().setDescricao(mLembrete.getDescricao());
            getMvpView().setTextoBotaoData(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
            getMvpView().setTextoBotaoHora(mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
            getMvpView().setTextoBotaoRepeticao(Lembrete.REPETICAO_SEM);
        }
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
}
