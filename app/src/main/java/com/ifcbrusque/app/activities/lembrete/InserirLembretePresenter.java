package com.ifcbrusque.app.activities.lembrete;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;

import java.util.Calendar;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

public class InserirLembretePresenter {
    private View view;
    private AppDatabase db;

    private int ano, mes, dia, hora, minuto;

    public InserirLembretePresenter(View view, AppDatabase db) {
        this.view = view;
        this.db = db;

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
    private void armazenarLembrete(Lembrete lembrete) {
        db.armazenarLembrete(lembrete)
                .doOnComplete(() -> {
                    view.fecharActivity(true);
                }).subscribe();
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

    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void mudarTextoBotaoData(int ano, int mes, int dia);

        void mudarTextoBotaoHora(int hora, int minuto);

        void mostrarToast(String texto);

        void fecharActivity(boolean lembreteNovoInserido);
    }
}
