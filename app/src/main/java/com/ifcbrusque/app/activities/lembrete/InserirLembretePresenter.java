package com.ifcbrusque.app.activities.lembrete;

import java.util.Calendar;

public class InserirLembretePresenter {
    private View view;

    private int ano, mes, dia, hora, minuto;

    public InserirLembretePresenter(View view) {
        this.view = view;

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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void onDataSelecionada(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
        view.mudarTextoBotaoData(this.ano, this.mes, this.dia);
    }

    public void onTempoSelecionado(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
        view.mudarTextoBotaoHora(this.hora, this.minuto);
    }

    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void mudarTextoBotaoData(int ano, int mes, int dia);

        void mudarTextoBotaoHora(int hora, int minuto);
    }
}
