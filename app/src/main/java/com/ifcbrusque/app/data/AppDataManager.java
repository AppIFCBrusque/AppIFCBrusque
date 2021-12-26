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
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
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
    public void setUsuarioSIGAA(String login, String senha) {
        mPreferencesHelper.setUsuarioSIGAA(login, senha);
    }

    @Override
    public String getLoginSIGAA() {
        return mPreferencesHelper.getLoginSIGAA();
    }

    @Override
    public String getSenhaSIGAA() {
        return mPreferencesHelper.getSenhaSIGAA();
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
    public Observable<ArrayList<Nota>> getNotasDisciplina(Disciplina disciplina) {
        return mNetworkHelper.getNotasDisciplina(disciplina);
    }
}
