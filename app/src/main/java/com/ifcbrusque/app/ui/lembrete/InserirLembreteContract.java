package com.ifcbrusque.app.ui.lembrete;

import android.text.TextWatcher;

import com.ifcbrusque.app.ui.base.MvpPresenter;
import com.ifcbrusque.app.ui.base.MvpView;

public interface InserirLembreteContract {
    interface InserirLembreteView extends MvpView {
        void exibirDialogoData(int anoInicial, int mesInicial, int diaInicial);

        void exibirDialogoTempo(int horaInicial, int minutoInicial);

        void exibirDialogoRepeticao();

        String getTitulo();

        String getDescricao();

        String getAnotacoes();

        void setTitulo(String titulo);

        void setDescricao(String descricao);

        void setAnotacoes(String anotacoes);

        void setTextoBotaoData(int ano, int mes, int dia);

        void setTextoBotaoHora(int hora, int minuto);

        void setTextoBotaoRepeticao(int tipoRepeticao);

        void desativarTitulo();

        void desativarDescricao();

        void esconderDescricao();

        void mostrarAnotacoes();

        void desativarBotaoData();

        void desativarBotaoHora();

        void desativarBotaoRepeticao();

        void ativarBotaoSalvar();

        void desativarBotaoSalvar();

        void exibirBotaoArquivo();

        void abrirNavegador(String url);

        void fecharActivity(boolean atualizarRecyclerView);
    }

    interface InserirLembretePresenter<V extends InserirLembreteView> extends MvpPresenter<V> {
        void onViewPronta(long idLembrete);

        void onBotaoArquivoClick();

        void onBotaoDataClick();

        void onBotaoTempoClick();

        void onBotaoRepeticaoClick();

        void onBotaoSalvarClick();

        void onDataSelecionada(int ano, int mes, int dia);

        void onTempoSelecionado(int hora, int minuto);

        void onRepeticaoSelecionada(int tipoRepeticao, int tempoRepeticaoPersonalizada);

        TextWatcher onTextoTituloChanged();
    }
}
