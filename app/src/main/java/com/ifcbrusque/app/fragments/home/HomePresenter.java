package com.ifcbrusque.app.fragments.home;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;

import java.util.ArrayList;
import java.util.List;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
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
     * @param position posição na lista de lembretes do lembrete para alternar o estado
     */
    public void alternarEstadoLembrete(int position) {
        int novoEstado = (lembretesArmazenados.get(position).getEstado() == Lembrete.ESTADO_INCOMPLETO) ? Lembrete.ESTADO_COMPLETO : Lembrete.ESTADO_INCOMPLETO;

        Completable.fromRunnable(() -> {
            db.lembreteDao().alterarEstadoLembrete(lembretesArmazenados.get(position).getId(), novoEstado);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    lembretesArmazenados.get(position).setEstado(novoEstado);
                    view.atualizarRecyclerView(lembretesArmazenados, position, false, false);
                })
                .subscribe();
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

        void mostrarToast(String texto);
    }
}
