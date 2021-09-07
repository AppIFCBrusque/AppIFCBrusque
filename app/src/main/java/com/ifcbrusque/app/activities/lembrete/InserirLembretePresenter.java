package com.ifcbrusque.app.activities.lembrete;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InserirLembretePresenter {
    private View view;
    private AppDatabase db;
    private PreferencesHelper pref;
    private final Calendar c = Calendar.getInstance();

    private Lembrete lembrete;

    public InserirLembretePresenter(View view, AppDatabase db, PreferencesHelper pref, long idLembrete) {
        //Iniciar variáveis
        this.view = view;
        this.db = db;
        this.pref = pref;

        //Inicializar lembrete
        if (idLembrete != -1) {
            //Carregar lembrete armazenado no banco de dados
            Observable.defer(() -> {
                return Observable.just(db.lembreteDao().getLembrete(idLembrete));
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(lembreteArmazenado -> {
                        //public Lembrete(int tipo, String titulo, String descricao, Date dataLembrete, long tempoRepeticao, int estado) {
                        lembrete = lembreteArmazenado;
                        view.setTitulo(lembrete.getTitulo());
                        view.setDescricao(lembrete.getDescricao());
                        c.setTime(lembrete.getDataLembrete());
                        view.mudarTextoBotaoData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        view.mudarTextoBotaoHora(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                    }).subscribe();
        } else {
            //Definir a data e hora padrão dos botões de data e hora
            //A hora é arredondada para cima. Exemplo: 17:23 -> 18::00; 02:00 -> 03:00
            c.add(Calendar.HOUR_OF_DAY, 1); //TODO: antes tava HOUR. se der merda, voltar
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            lembrete = new Lembrete(Lembrete.LEMBRETE_PESSOAL, "", "", c.getTime(), Lembrete.REPETICAO_NAO_REPETIR, Lembrete.ESTADO_INCOMPLETO);

            view.mudarTextoBotaoData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            view.mudarTextoBotaoHora(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas somente por este presenter
     */

    /**
     * Armazena um lembrete com as informações salvas neste presenter e fecha a activity
     *
     * O lembrete precisa ter um título válido. Se não tiver, mostra um toast
     */
    private void salvarLembrete() {
        if (lembrete.getTitulo().length() > 0) {
            if (!(lembrete.getDescricao().length() > 0)) {
                lembrete.setDescricao("");
            }

            //Completable para salvar o lembrete
            Completable.fromRunnable(() -> {
                if (lembrete.getId() != 0) {
                    //Atualizar lembrete existente
                    db.lembreteDao().updateLembrete(lembrete);
                } else {
                    //Salvar lembrete novo
                    //Id da notificação
                    long idNotificacaoLembrete = pref.getUltimoIdNotificacoes();
                    lembrete.setIdNotificacao(idNotificacaoLembrete);
                    //Salvar lembrete e atualizar id
                    long idLembrete = db.lembreteDao().insert(lembrete);
                    lembrete.setId(idLembrete);
                }

                //Agendar notificação
                if(lembrete.getEstado() == Lembrete.ESTADO_INCOMPLETO && new Date().before(lembrete.getDataLembrete())) {
                    view.agendarNotificacaoLembrete(lembrete);
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        view.fecharActivity(true);
                    })
                    .subscribe();
        } else {
            view.mostrarToast("Informe um título ao lembrete");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções deste presenter que podem ser utilizadas pela view
     */
    public int getAno() {
        return c.get(Calendar.YEAR);
    }

    public int getMes() {
        return c.get(Calendar.MONTH);
    }

    public int getDia() {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public int getHora() {
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinuto() {
        return c.get(Calendar.MINUTE);
    }

    /**
     * Executado quando o usuário seleciona uma data no diálogo que aparece ao clicar no botão de data e clica em "OK"
     * Insira as variáveis diretamente obtidas pelo dialogo
     * <p>
     * Armazena os valores e muda o texto do botão
     */
    public void onDataSelecionada(int ano, int mes, int dia) {
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes);
        c.set(Calendar.DAY_OF_MONTH, dia);
        lembrete.setDataLembrete(c.getTime());
        view.mudarTextoBotaoData(ano, mes, dia);
    }

    /**
     * Executado quando o usuário seleciona uma hora no diálogo que aparece ao clicar no botão de hora e clica em "OK"
     * Insira as variáveis diretamente obtidas pelo dialogo
     * <p>
     * Armazena os valores e muda o texto do botão
     */
    public void onTempoSelecionado(int hora, int minuto) {
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, minuto);
        lembrete.setDataLembrete(c.getTime());
        view.mudarTextoBotaoHora(hora, minuto);
    }

    /**
     * Executado quando o usuário clica no botão de inserir lembrete (botão redondo no canto inferior direito)
     * Atualiza o título e a descrição salvos no presenter
     * <p>
     * Chama a função para armazenar o lembrete com as informações deste presenter
     *
     * @param titulo    título do lembrete
     * @param descricao descrição do lembrete
     */
    public void onCliqueInserir(String titulo, String descricao) {
        lembrete.setTitulo(titulo);
        lembrete.setDescricao(descricao);
        salvarLembrete();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Declarar métodos que serão utilizados por este presenter e definidos na view
     */
    public interface View {
        void mudarTextoBotaoData(int ano, int mes, int dia);

        void mudarTextoBotaoHora(int hora, int minuto);

        void setTitulo(String titulo);

        void setDescricao(String descricao);

        void agendarNotificacaoLembrete(Lembrete lembrete);

        void mostrarToast(String texto);

        void fecharActivity(boolean lembreteNovoInserido);
    }
}
