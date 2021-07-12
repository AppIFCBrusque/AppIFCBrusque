package com.ifcbrusque.app.helpers;

import android.content.Context;

import com.ifcbrusque.app.helpers.PreferencesHelper;
import com.ifcbrusque.app.models.Atividades;
import com.stacked.sigaa_ifc.*;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SIGAAHelper { //TODO: Arrumar, comentar, static
    private Sessao sessao = new Sessao("https://sig.ifc.edu.br/");
    private Context context;

    boolean conectado = false;

    public SIGAAHelper(Context context) {
        this.context = context;
    }

    public Observable<Boolean> logar() {
        return Observable.fromCallable(() -> {
            PreferencesHelper pref = new PreferencesHelper(context);
            return sessao.login(pref.getLoginSIGAA(), pref.getSenhaSIGAA());
        })
                .subscribeOn(Schedulers.io());
    }

    Sessao s = new Sessao("https://sig.ifc.edu.br/");

    /*

    Esse código tá horrível, mas as conexões com o SIGAA funcionariam tipo assim

     */
    public Observable<Atividades> getTodasAtividades() {
        //TODO: Arrumar isso e o login. Acho que seria melhor ir retornando vários menores em vez de tudo de uma vez
        return Observable.fromCallable(() -> {
            if(!conectado) {//TODO ARRUMAR
                PreferencesHelper pref = new PreferencesHelper(context);
                conectado = sessao.login(pref.getLoginSIGAA(), pref.getSenhaSIGAA());
            }

            Atividades atv = new Atividades();
            ArrayList<Disciplina> falhas = new ArrayList<>();

            for (Disciplina d : sessao.getUsuario().getDisciplinasAtuais()) { //Pegar avaliações e tarefas de cada disciplina & adicionar na lista de atividades
                if (conectado) {
                    try {
                        ArrayList<Avaliacao> avaliacoes = sessao.disciplinaPegarAvaliacoes(d);
                        ArrayList<Tarefa> tarefas = sessao.disciplinaPegarTarefas(d);
                        if (avaliacoes.size() > 0) atv.addAvaliacoes(avaliacoes);
                        if (tarefas.size() > 0) atv.addTarefas(tarefas);
                    } catch (Exception e) {
                        falhas.add(d); //Conferir essa matéria depois
                        //Conferir se é possível se conectar com o SIGAA (manutenção/sem internet) (retorna ExcecaoSIGAA se não for possível conectar com o SIGAA)
                        if (!sessao.conferirUsuarioLogado()) {
                            conectado = false;
                            //TODO: Relogar (retorna ExcecaoSIGAA se não for possível conectar com o SIGAA)

                        }
                    }
                }
            }

            //TODO falhas
            for (Disciplina f : falhas) {
                System.out.println(f.getNome());
            }

            return atv;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()); //TODO Definir o erro na activity
    }
}
