package com.ifcbrusque.app.data.prefs;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.HOME_ULTIMA_CATEGORIA_ACESSADA_ID;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.NOTICIAS_ULTIMA_PAGINA_ACESSADA;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.NOTICIAS_ULTIMA_SINCRONIZACAO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.NOTIFICACOES_ULTIMO_ID;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_AVALIACOES_ALTERADAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_AVALIACOES_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_LEMBRETES;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_NOTICIAS_DO_CAMPUS_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_QUESTIONARIOS_ALTERADOS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_QUESTIONARIOS_NOVOS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_TAREFAS_ALTERADAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_TAREFAS_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_SINCRONIZAR_NOTICIAS_CAMPUS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_SINCRONIZAR_SIGAA;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_TEMA;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PRIMEIRA_INICIALIZACAO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PRIMEIRA_SINCRONIZACAO_NOTICIAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_CONECTADO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_CURSO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_LOGIN;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_NOME_DO_USUARIO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_SENHA;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_URL_AVATAR;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SYNC_ULTIMA_DATA;
import static com.ifcbrusque.app.utils.AppConstants.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import com.ifcbrusque.app.data.db.Converters;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.di.ApplicationContext;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

/*
Classe com funções para utilizar o SharedPreferences
 */
@Singleton
public class AppPreferencesHelper implements PreferencesHelper {
    private final SharedPreferences pref;

    @Inject
    public AppPreferencesHelper(@ApplicationContext Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public boolean getPrimeiraInicializacao() {
        return pref.getBoolean(PRIMEIRA_INICIALIZACAO, true);
    }

    @Override
    public void setPrimeiraInicializacao(boolean b) {
        pref.edit().putBoolean(PRIMEIRA_INICIALIZACAO, b).apply();
    }

    @Override
    public boolean getPrimeiraSincronizacaoNoticias() {
        return pref.getBoolean(PRIMEIRA_SINCRONIZACAO_NOTICIAS, true);
    }

    @Override
    public void setPrimeiraSincronizacaoNoticias(boolean b) {
        pref.edit().putBoolean(PRIMEIRA_SINCRONIZACAO_NOTICIAS, b).apply();
    }

    @Override
    public boolean getSIGAAConectado() {
        return pref.getBoolean(SIGAA_CONECTADO, false);
    }

    @Override
    public void setSIGAAConectado(boolean b) {
        pref.edit().putBoolean(SIGAA_CONECTADO, b).apply();
    }

    @Override
    public String getLoginSIGAA() {
        return pref.getString(SIGAA_LOGIN, "");
    }

    @Override
    public void setLoginSIGAA(String login) {
        pref.edit().putString(SIGAA_LOGIN, login).apply();
    }

    @Override
    public String getSenhaSIGAA() {
        return pref.getString(SIGAA_SENHA, "");
    }

    @Override
    public void setSenhaSIGAA(String senha) {
        pref.edit().putString(SIGAA_SENHA, senha).apply();
    }

    @Override
    public String getNomeDoUsuarioSIGAA() {
        return pref.getString(SIGAA_NOME_DO_USUARIO, "");
    }

    @Override
    public void setNomeDoUsuarioSIGAA(String s) {
        pref.edit().putString(SIGAA_NOME_DO_USUARIO, s).apply();
    }

    @Override
    public String getUrlAvatarSIGAA() {
        return pref.getString(SIGAA_URL_AVATAR, "");
    }

    @Override
    public void setUrlAvatarSIGAA(String urlAvatarSIGAA) {
        pref.edit().putString(SIGAA_URL_AVATAR, urlAvatarSIGAA).apply();
    }

    @Override
    public String getCursoSIGAA() {
        return pref.getString(SIGAA_CURSO, "");
    }

    @Override
    public void setCursoSIGAA(String curso) {
        pref.edit().putString(SIGAA_CURSO, curso).apply();
    }

    @Override
    public Date getDataUltimaSincronizacaoAutomaticaNoticias() {
        return Converters.fromTimestamp(pref.getLong(NOTICIAS_ULTIMA_SINCRONIZACAO, 0));
    }

    @Override
    public void setDataUltimaSincronizacaoAutomaticaNoticias(Date data) {
        long _data = Converters.dateToTimestamp(data);
        pref.edit().putLong(NOTICIAS_ULTIMA_SINCRONIZACAO, _data).apply();
    }

    @Override
    public Date getDataUltimaSincronizacaoCompleta() {
        return Converters.fromTimestamp(pref.getLong(SYNC_ULTIMA_DATA, 0));
    }

    @Override
    public void setDataUltimaSincronizacaoCompleta(Date data) {
        long _data = Converters.dateToTimestamp(data);
        pref.edit().putLong(SYNC_ULTIMA_DATA, _data).apply();
    }

    @Override
    public int getUltimaPaginaAcessadaNoticias() {
        return pref.getInt(NOTICIAS_ULTIMA_PAGINA_ACESSADA, 1);
    }

    @Override
    public void setUltimaPaginaAcessadaNoticias(int pagina) {
        pref.edit().putInt(NOTICIAS_ULTIMA_PAGINA_ACESSADA, pagina).apply();
    }

    @Override
    public int getNovoIdNotificacao() {
        final int id = pref.getInt(NOTIFICACOES_ULTIMO_ID, 100);
        pref.edit().putInt(NOTIFICACOES_ULTIMO_ID, id + 1).apply();
        return id;
    }

    @Override
    public int getUltimaCategoriaAcessadaHome() {
        return pref.getInt(HOME_ULTIMA_CATEGORIA_ACESSADA_ID, Lembrete.ESTADO_INCOMPLETO);
    }

    @Override
    public void setUltimaCategoriaAcessadaHome(int categoria) {
        pref.edit().putInt(HOME_ULTIMA_CATEGORIA_ACESSADA_ID, categoria).apply();
    }

    @Override
    public int getPrefTema() {
        return Integer.parseInt(pref.getString(PREF_TEMA, "0"));
    }

    @Override
    public boolean getPrefNotificarLembretes() {
        return pref.getBoolean(PREF_NOTIFICAR_LEMBRETES, true);
    }

    @Override
    public boolean getPrefNotificarNoticiasDoCampusNovas() {
        return pref.getBoolean(PREF_NOTIFICAR_NOTICIAS_DO_CAMPUS_NOVAS, true);
    }

    @Override
    public boolean getPrefNotificarAvaliacoesNovas() {
        return pref.getBoolean(PREF_NOTIFICAR_AVALIACOES_NOVAS, true);
    }

    @Override
    public boolean getPrefNotificarAvaliacoesAlteradas() {
        return pref.getBoolean(PREF_NOTIFICAR_AVALIACOES_ALTERADAS, true);
    }

    @Override
    public boolean getPrefNotificarTarefasNovas() {
        return pref.getBoolean(PREF_NOTIFICAR_TAREFAS_NOVAS, true);
    }

    @Override
    public boolean getPrefNotificarTarefasAlteradas() {
        return pref.getBoolean(PREF_NOTIFICAR_TAREFAS_ALTERADAS, true);
    }

    @Override
    public boolean getPrefNotificarQuestionariosNovos() {
        return pref.getBoolean(PREF_NOTIFICAR_QUESTIONARIOS_NOVOS, true);
    }

    @Override
    public boolean getPrefNotificarQuestionariosAlterados() {
        return pref.getBoolean(PREF_NOTIFICAR_QUESTIONARIOS_ALTERADOS, true);
    }

    @Override
    public boolean getPrefSincronizarSIGAAA() {
        return pref.getBoolean(PREF_SINCRONIZAR_SIGAA, false);
    }

    @Override
    public void setPrefSincronizarSIGAA(boolean b) {
        pref.edit().putBoolean(PREF_SINCRONIZAR_SIGAA, b).apply();
    }

    @Override
    public boolean getPrefSincronizarNoticiasCampus() {
        return pref.getBoolean(PREF_SINCRONIZAR_NOTICIAS_CAMPUS, true);
    }

    @Override
    public void setPrefSincronizarNoticiasCampus(boolean b) {
        pref.edit().putBoolean(PREF_SINCRONIZAR_NOTICIAS_CAMPUS, b);
    }
}
