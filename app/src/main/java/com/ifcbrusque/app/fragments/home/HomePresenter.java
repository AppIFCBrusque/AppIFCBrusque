package com.ifcbrusque.app.fragments.home;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.helpers.NotificationHelper;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenter {
    private View view;
    private AppDatabase db;
    private PreferencesHelper pref;

    private List<Lembrete> lembretesArmazenados;

    public HomePresenter(View view, AppDatabase db, PreferencesHelper pref) {
        //Iniciar variáveis
        this.view = view;
        this.db = db;
        this.pref = pref;

        lembretesArmazenados = new ArrayList<>();
        carregarLembretesArmazenados(true);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções deste presenter que podem ser utilizadas pela view
     */
    public List<Lembrete> getLembretesArmazenados() {
        return lembretesArmazenados;
    }

    public int getUltimaCategoriaAcessadaHome() {
        return pref.getUltimaCategoriaAcessadaHome();
    }

    public void setUltimaCategoriaAcessadaHome(int categoria) {
        pref.setUltimaCategoriaAcessadaHome(categoria);
    }

    /**
     * Utilizado pelo view para carregar os lembretes do banco de dados
     * Após carregar, atualiza a recycler view com os lembretes retornados
     * @param agendarNotificacoes indica se vai pedir para a view agendar a notificação dos lembretes da lista
     */
    public void carregarLembretesArmazenados(boolean agendarNotificacoes) {
        Completable.fromRunnable(() -> {
            lembretesArmazenados = db.lembreteDao().getAll();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    view.atualizarRecyclerView(lembretesArmazenados, agendarNotificacoes);
                }).subscribe();
    }

    /**
     * Utilizado pela view para definir um lembrete como completo/incompleto
     * Depois de definir, atualiza a recycler view
     * Se for um lembrete que repete, pula para a próxima repetição
     * @param position posição na lista de lembretes do lembrete para alternar o estado
     */
    public void alternarEstadoLembrete(int position) {
        if(lembretesArmazenados.get(position).getTipoRepeticao() == Lembrete.REPETICAO_SEM) {
            //Lembrete que não repete (simplesmente alterar entre completo/incompleto)
            int novoEstado = (lembretesArmazenados.get(position).getEstado() == Lembrete.ESTADO_INCOMPLETO) ? Lembrete.ESTADO_COMPLETO : Lembrete.ESTADO_INCOMPLETO;

            Completable.fromRunnable(() -> {
                db.lembreteDao().alterarEstadoLembrete(lembretesArmazenados.get(position).getId(), novoEstado);
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> {
                        lembretesArmazenados.get(position).setEstado(novoEstado);

                        if(novoEstado == Lembrete.ESTADO_INCOMPLETO) {
                            //Agendar
                            if(new Date().before(lembretesArmazenados.get(position).getDataLembrete())) {
                                view.agendarNotificacaoLembrete(lembretesArmazenados.get(position));
                            }
                        } else {
                            //Desagendar
                            view.desagendarNotificacaoLembrete(lembretesArmazenados.get(position));
                        }

                        view.atualizarRecyclerView(lembretesArmazenados, position, false, false);
                    })
                    .subscribe();
        } else {
            //Lembrete que repete (ir para a próxima repetição)
            view.desagendarNotificacaoLembrete(lembretesArmazenados.get(position));
            view.atualizarParaProximaDataLembreteComRepeticao(lembretesArmazenados.get(position).getId())
                    .doOnNext(lembrete -> {
                        carregarLembretesArmazenados(true);
                        //TODO: Não tem alguma maneira melhor para atualizar somente um de uma vez aqui?
                    }).subscribe();
        }
    }

    /**
     * Utilizado pela view para excluir um lembrete
     * Depois de definir, atualiza a recycler view
     * @param position posição na lista de lembretes do lembrete para excluir
     */
    public void excluirLembrete(int position) {
        Completable.fromRunnable(() -> {
            db.lembreteDao().delete(lembretesArmazenados.get(position));
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    view.desagendarNotificacaoLembrete(lembretesArmazenados.get(position));
                    lembretesArmazenados.remove(position);
                    view.atualizarRecyclerView(lembretesArmazenados, position, false, true);
                })
                .subscribe();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Declarar métodos que serão utilizados por este presenter e definidos na view
     */
    public interface View {
        void atualizarRecyclerView(List<Lembrete> lembretes, boolean agendarNotificacoes);

        void atualizarRecyclerView(List<Lembrete> lembretes, int position, boolean agendarNotificacao, boolean removido);

        void agendarNotificacaoLembrete(Lembrete lembrete);

        void desagendarNotificacaoLembrete(Lembrete lembrete);

        Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(long idLembrete);

        void mostrarToast(String texto);
    }
}
