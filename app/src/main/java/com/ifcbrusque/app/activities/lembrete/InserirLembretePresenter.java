package com.ifcbrusque.app.activities.lembrete;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import java.util.Calendar;

public class InserirLembretePresenter {
    private View view;
    private AppDatabase db;

    private int ano, mes, dia, hora, minuto;

    public InserirLembretePresenter(View view, AppDatabase db) {
        //Iniciar variáveis
        this.view = view;
        this.db = db;

        //Definir a data e hora padrão dos botões de data e hora
        //A hora é arredondada para cima. Exemplo: 17:23 -> 18::00; 02:00 -> 03:00
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.HOUR, 1);
        ano = c.get(Calendar.YEAR);
        mes = c.get(Calendar.MONTH);
        dia = c.get(Calendar.DAY_OF_MONTH);
        hora = c.get(Calendar.HOUR_OF_DAY);
        minuto = 0;

        view.mudarTextoBotaoData(ano, mes, dia);
        view.mudarTextoBotaoHora(hora, minuto);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas somente por este presenter
     */

    /**
     * Armazena o lembrete inserido e fecha a activity
     */
    private void armazenarLembrete(Lembrete lembrete) {
        db.armazenarLembrete(lembrete)
                .doOnComplete(() -> {
                    view.fecharActivity(true);
                }).subscribe();
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
     *
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
     *
     * Armazena os valores e muda o texto do botão
     */
    public void onTempoSelecionado(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
        view.mudarTextoBotaoHora(this.hora, this.minuto);
    }

    /**
     * Executado quando o usuário clica no botão de inserir lembrete (botão redondo no canto inferior direito)
     * A data e a hora já estão salvas neste presenter, então é necessário inserir apenas o título e a descrição do lembrete, que são salvas nas views
     *
     * Confere se o lembrete possui um título válido. Se possuir, chama a função para armazenar o lembrete. Senão, exibe uma mensagem através do toast
     *
     * @param titulo título do lembrete
     * @param descricao descrição do lembrete
     */
    public void onCliqueInserir(String titulo, String descricao) {
        if(titulo.length() > 0) {
            if(!(descricao.length() > 0)) {
                descricao = "";
            }

            final Calendar c = Calendar.getInstance();
            c.set(ano, mes, dia, hora, minuto);

            Lembrete lembrete = new Lembrete(Lembrete.LEMBRETE_PESSOAL, titulo, descricao, c.getTime(), Lembrete.REPETICAO_NAO_REPETIR, Lembrete.ESTADO_INCOMPLETO);
            armazenarLembrete(lembrete);
        } else {
            view.mostrarToast("Informe um título ao lembrete");
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Declarar métodos que serão utilizados por este presenter e definidos na view
     */
    public interface View {
        void mudarTextoBotaoData(int ano, int mes, int dia);

        void mudarTextoBotaoHora(int hora, int minuto);

        void mostrarToast(String texto);

        void fecharActivity(boolean lembreteNovoInserido);
    }
}
