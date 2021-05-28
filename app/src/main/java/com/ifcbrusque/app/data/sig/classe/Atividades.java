package com.ifcbrusque.app.data.sig.classe;

import com.stacked.sigaa_ifc.*;

import java.util.ArrayList;

public class Atividades { //Corresponde às atividades do SIGAA que aparecerão na lista da home
    public Atividades() {}

    private ArrayList<Tarefa> tarefas = new ArrayList<>();
    private ArrayList<Avaliacao> avaliacoes = new ArrayList<>();;
    //private List<Questionario> questionarios;

    public void addTarefas(ArrayList<Tarefa> tarefas) {this.tarefas.addAll(tarefas);}
    public ArrayList<Tarefa> getTarefas() {return tarefas;}

    public void addAvaliacoes(ArrayList<Avaliacao> avaliacoes) {this.avaliacoes.addAll(avaliacoes);}
    public ArrayList<Avaliacao> getAvaliacoes() {return avaliacoes;}
}
