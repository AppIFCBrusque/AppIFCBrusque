package com.ifcbrusque.app.activities.lembrete;

import android.util.Log;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

public class InserirLembretePresenter {
    private View view;
    private AppDatabase db;
    private PreferencesHelper pref;

    //TODO: Armazenar as variáveis em um lembrete
    private long idLembrete;
    private long idNotificacaoLembrete;
    private int ano, mes, dia, hora, minuto;
    private String titulo, descricao;

    public InserirLembretePresenter(View view, AppDatabase db, PreferencesHelper pref, long idLembrete, long idNotificacaoLembrete) {
        //Iniciar variáveis
        this.view = view;
        this.db = db;
        this.pref = pref;
        this.idLembrete = idLembrete;
        this.idNotificacaoLembrete = idNotificacaoLembrete;

        //Inicializar lembrete
        final Calendar c = Calendar.getInstance();
        if (idLembrete != -1) {
            //Carregar lembrete armazenado no banco de dados
            Observable.defer(() -> {
                return Observable.just(db.lembreteDao().getLembrete(idLembrete));
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(lembrete -> {
                        titulo = lembrete.getTitulo();
                        descricao = lembrete.getDescricao();
                        c.setTime(lembrete.getDataLembrete());
                        ano = c.get(Calendar.YEAR);
                        mes = c.get(Calendar.MONTH);
                        dia = c.get(Calendar.DAY_OF_MONTH);
                        hora = c.get(Calendar.HOUR_OF_DAY);
                        minuto = c.get(Calendar.MINUTE);
                        view.setTitulo(titulo);
                        view.setDescricao(descricao);
                        view.mudarTextoBotaoData(ano, mes, dia);
                        view.mudarTextoBotaoHora(hora, minuto);
                        //TODO: Não tem como utilizar esta parte de cima somente uma vez os dois casos?
                    }).subscribe();
        } else {
            //Definir a data e hora padrão dos botões de data e hora
            //A hora é arredondada para cima. Exemplo: 17:23 -> 18::00; 02:00 -> 03:00
            titulo = "";
            descricao = "";
            c.add(Calendar.HOUR, 1);
            ano = c.get(Calendar.YEAR);
            mes = c.get(Calendar.MONTH);
            dia = c.get(Calendar.DAY_OF_MONTH);
            hora = c.get(Calendar.HOUR_OF_DAY);
            minuto = 0;
            view.mudarTextoBotaoData(ano, mes, dia);
            view.mudarTextoBotaoHora(hora, minuto);
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
        if (titulo.length() > 0) {
            if (!(descricao.length() > 0)) {
                descricao = "";
            }
            final Calendar c = Calendar.getInstance();
            c.set(ano, mes, dia, hora, minuto, 0);
            Lembrete lembrete = new Lembrete(Lembrete.LEMBRETE_PESSOAL, titulo, descricao, c.getTime(), Lembrete.REPETICAO_NAO_REPETIR, Lembrete.ESTADO_INCOMPLETO);

            //Completable para salvar o lembrete
            Completable.fromRunnable(() -> {
                if (idLembrete != -1) {
                    //Atualizar lembrete existente
                    lembrete.setId(idLembrete);
                    lembrete.setIdNotificacao(idNotificacaoLembrete);
                    db.lembreteDao().updateLembrete(lembrete);
                } else {
                    //Salvar lembrete novo
                    idNotificacaoLembrete = pref.getUltimoIdNotificacoes();
                    lembrete.setIdNotificacao(idNotificacaoLembrete);
                    idLembrete = db.lembreteDao().insert(lembrete);
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
        return ano;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }

    /**
     * Executado quando o usuário seleciona uma data no diálogo que aparece ao clicar no botão de data e clica em "OK"
     * Insira as variáveis diretamente obtidas pelo dialogo
     * <p>
     * Armazena os valores e muda o texto do botão
     */
    public void onDataSelecionada(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
        view.mudarTextoBotaoData(this.ano, this.mes, this.dia);
    }

    /**
     * Executado quando o usuário seleciona uma hora no diálogo que aparece ao clicar no botão de hora e clica em "OK"
     * Insira as variáveis diretamente obtidas pelo dialogo
     * <p>
     * Armazena os valores e muda o texto do botão
     */
    public void onTempoSelecionado(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
        view.mudarTextoBotaoHora(this.hora, this.minuto);
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
        this.titulo = titulo;
        this.descricao = descricao;
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
