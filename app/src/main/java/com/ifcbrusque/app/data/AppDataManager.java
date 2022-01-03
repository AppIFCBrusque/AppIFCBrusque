package com.ifcbrusque.app.data;

import android.os.Bundle;

import com.ifcbrusque.app.data.db.DbHelper;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.NetworkHelper;
import com.ifcbrusque.app.data.notification.NotificationHelper;
import com.ifcbrusque.app.data.prefs.PreferencesHelper;
import com.ifcbrusque.app.service.SyncService;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;
import com.stacked.sigaa_ifc.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Singleton
public class AppDataManager implements DataManager {
    private final DbHelper mDbHelper;
    private final NetworkHelper mNetworkHelper;
    private final NotificationHelper mNotificationHelper;
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public AppDataManager(DbHelper dbHelper, NetworkHelper networkHelper, NotificationHelper notificationHelper, PreferencesHelper preferencesHelper) {
        mDbHelper = dbHelper;
        mNetworkHelper = networkHelper;
        mNotificationHelper = notificationHelper;
        mPreferencesHelper = preferencesHelper;
    }

    @Override
    public Observable<List<Preview>> getPreviewsArmazenados() {
        return mDbHelper.getPreviewsArmazenados();
    }

    @Override
    public Observable<List<Preview>> armazenarPreviewsNovos(List<Preview> previews, boolean retornarPreviewsNovos) {
        return mDbHelper.armazenarPreviewsNovos(previews, retornarPreviewsNovos);
    }

    @Override
    public Observable<Noticia> getNoticia(String url) {
        return mDbHelper.getNoticia(url);
    }

    @Override
    public Observable<Long> inserirNoticia(Noticia noticia) {
        return mDbHelper.inserirNoticia(noticia);
    }

    @Override
    public Observable<Lembrete> getLembrete(long id) {
        return mDbHelper.getLembrete(id);
    }

    @Override
    public Observable<List<Lembrete>> getLembretesArmazenados() {
        return mDbHelper.getLembretesArmazenados();
    }

    @Override
    public Observable<Long> inserirLembrete(Lembrete lembrete) {
        return mDbHelper.inserirLembrete(lembrete);
    }

    @Override
    public Completable deletarLembrete(Lembrete lembrete) {
        return mDbHelper.deletarLembrete(lembrete);
    }

    @Override
    public Completable atualizarLembrete(Lembrete lembrete) {
        return mDbHelper.atualizarLembrete(lembrete);
    }

    @Override
    public Completable alterarEstadoLembrete(long id, int novoEstado) {
        return mDbHelper.alterarEstadoLembrete(id, novoEstado);
    }

    @Override
    public Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(long idLembrete) {
        return mDbHelper.atualizarParaProximaDataLembreteComRepeticao(idLembrete);
    }

    @Override
    public Completable inserirDisciplinas(List<Disciplina> disciplinas) {
        return mDbHelper.inserirDisciplinas(disciplinas);
    }

    @Override
    public Completable deletarDisciplina(Disciplina disciplina) {
        return mDbHelper.deletarDisciplina(disciplina);
    }

    @Override
    public Observable<List<Disciplina>> getDisciplinas(String frontEndIdTurma) {
        return mDbHelper.getDisciplinas(frontEndIdTurma);
    }

    @Override
    public Observable<List<Disciplina>> getAllDisciplinas() {
        return mDbHelper.getAllDisciplinas();
    }

    @Override
    public Observable<List<Avaliacao>> getAllAvaliacoes() {
        return mDbHelper.getAllAvaliacoes();
    }

    @Override
    public Observable<List<Avaliacao>> inserirAvaliacoes(List<Avaliacao> avaliacoes) {
        return mDbHelper.inserirAvaliacoes(avaliacoes);
    }

    @Override
    public Completable deletarAvaliacao(Avaliacao avaliacao) {
        return mDbHelper.deletarAvaliacao(avaliacao);
    }

    @Override
    public Observable<List<Tarefa>> getAllTarefas() {
        return mDbHelper.getAllTarefas();
    }

    @Override
    public Observable<List<Tarefa>> inserirTarefas(List<Tarefa> tarefas) {
        return mDbHelper.inserirTarefas(tarefas);
    }

    @Override
    public Completable deletarTarefa(Tarefa tarefa) {
        return mDbHelper.deletarTarefa(tarefa);
    }

    @Override
    public Observable<List<Questionario>> getAllQuestionarios() {
        return mDbHelper.getAllQuestionarios();
    }

    @Override
    public Observable<List<Questionario>> inserirQuestionarios(List<Questionario> questionarios) {
        return mDbHelper.inserirQuestionarios(questionarios);
    }

    @Override
    public Completable deletarQuestionario(Questionario questionario) {
        return deletarQuestionario(questionario);
    }

    @Override
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return mNetworkHelper.getPaginaNoticias(numeroPagina);
    }

    @Override
    public Observable<Noticia> getNoticiaWeb(Preview preview) {
        return mNetworkHelper.getNoticiaWeb(preview);
    }

    @Override
    public void criarCanalNotificacoes() {
        mNotificationHelper.criarCanalNotificacoes();
    }

    @Override
    public void notificarLembrete(Bundle bundle) {
        mNotificationHelper.notificarLembrete(bundle);
    }

    @Override
    public void agendarNotificacaoLembrete(Lembrete lembrete) {
        mNotificationHelper.agendarNotificacaoLembrete(lembrete);
    }

    @Override
    public void agendarNotificacaoLembreteSeFuturo(Lembrete lembrete) {
        mNotificationHelper.agendarNotificacaoLembreteSeFuturo(lembrete);
    }

    @Override
    public void agendarNotificacoesLembretesFuturos(List<Lembrete> lembretes) {
        mNotificationHelper.agendarNotificacoesLembretesFuturos(lembretes);
    }

    @Override
    public void desagendarNotificacaoLembrete(Lembrete lembrete) {
        mNotificationHelper.desagendarNotificacaoLembrete(lembrete);
    }

    @Override
    public void notificarNoticia(Preview preview, int idNotificacao) {
        mNotificationHelper.notificarNoticia(preview, idNotificacao);
    }

    @Override
    public void agendarSincronizacaoPeriodicaNoticias() {
        mNotificationHelper.agendarSincronizacaoPeriodicaNoticias();
    }

    @Override
    public void notificarSincronizacao(SyncService service) {
        mNotificationHelper.notificarSincronizacao(service);
    }

    @Override
    public void notificarSincronizacaoNoticias(SyncService service, int tarefaAtual, int totalTarefas) {
        mNotificationHelper.notificarSincronizacaoNoticias(service, tarefaAtual, totalTarefas);
    }

    @Override
    public void notificarSincronizacaoSIGAA(SyncService service, Disciplina disciplina, int tarefaAtual, int totalTarefas) {
        mNotificationHelper.notificarSincronizacaoSIGAA(service, disciplina, tarefaAtual, totalTarefas);
    }

    @Override
    public void notificarAvaliacaoNova(Avaliacao avaliacao) {
        mNotificationHelper.notificarAvaliacaoNova(avaliacao);
    }

    @Override
    public void notificarTarefaNova(Tarefa tarefa) {
        mNotificationHelper.notificarTarefaNova(tarefa);
    }

    @Override
    public void notificarQuestionarioNovo(Questionario questionario) {
        mNotificationHelper.notificarQuestionarioNovo(questionario);
    }

    @Override
    public boolean getPrimeiraInicializacao() {
        return mPreferencesHelper.getPrimeiraInicializacao();
    }

    @Override
    public void setPrimeiraInicializacao(boolean b) {
        mPreferencesHelper.setPrimeiraInicializacao(b);
    }

    @Override
    public boolean getSIGAAConectado() {
        return mPreferencesHelper.getSIGAAConectado();
    }

    @Override
    public void setSIGAAConectado(boolean b) {
        mPreferencesHelper.setSIGAAConectado(b);
    }

    @Override
    public String getLoginSIGAA() {
        return mPreferencesHelper.getLoginSIGAA();
    }

    @Override
    public void setLoginSIGAA(String login) {
        mPreferencesHelper.setLoginSIGAA(login);
    }

    @Override
    public String getSenhaSIGAA() {
        return mPreferencesHelper.getSenhaSIGAA();
    }

    @Override
    public void setSenhaSIGAA(String senha) {
        mPreferencesHelper.setSenhaSIGAA(senha);
    }

    @Override
    public void setDataUltimaSincronizacaoAutomaticaNoticias(Date data) {
        mPreferencesHelper.setDataUltimaSincronizacaoAutomaticaNoticias(data);
    }

    @Override
    public Date getDataUltimaSincronizacaoAutomaticaNoticias() {
        return mPreferencesHelper.getDataUltimaSincronizacaoAutomaticaNoticias();
    }

    @Override
    public void setUltimaPaginaAcessadaNoticias(int pagina) {
        mPreferencesHelper.setUltimaPaginaAcessadaNoticias(pagina);
    }

    @Override
    public int getUltimaPaginaAcessadaNoticias() {
        return mPreferencesHelper.getUltimaPaginaAcessadaNoticias();
    }

    @Override
    public void setPreviewTopoRecyclerViewNoticias(int index) {
        mPreferencesHelper.setPreviewTopoRecyclerViewNoticias(index);
    }

    @Override
    public int getPreviewTopoRecyclerViewNoticias() {
        return mPreferencesHelper.getPreviewTopoRecyclerViewNoticias();
    }

    @Override
    public int getNovoIdNotificacao() {
        return mPreferencesHelper.getNovoIdNotificacao();
    }

    @Override
    public int getUltimaCategoriaAcessadaHome() {
        return mPreferencesHelper.getUltimaCategoriaAcessadaHome();
    }

    @Override
    public void setUltimaCategoriaAcessadaHome(int categoria) {
        mPreferencesHelper.setUltimaCategoriaAcessadaHome(categoria);
    }

    @Override
    public Usuario getUsuarioSIGAA() {
        return mNetworkHelper.getUsuarioSIGAA();
    }

    @Override
    public Observable<Boolean> logarSIGAA(String usuario, String senha) {
        return mNetworkHelper.logarSIGAA(usuario, senha);
    }

    @Override
    public Observable<ArrayList<Nota>> getNotasDisciplinaSIGAA(Disciplina disciplina) {
        return mNetworkHelper.getNotasDisciplinaSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Avaliacao>> getAvaliacoesDisciplinaSIGAA(Disciplina disciplina) {
        return mNetworkHelper.getAvaliacoesDisciplinaSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Tarefa>> getTarefasDisciplinaSIGAA(Disciplina disciplina) {
        return mNetworkHelper.getTarefasDisciplinaSIGAA(disciplina);
    }

    @Override
    public Observable<ArrayList<Questionario>> getQuestionariosDisciplinaSIGAA(Disciplina disciplina) {
        return mNetworkHelper.getQuestionariosDisciplinaSIGAA(disciplina);
    }
}
