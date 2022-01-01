package com.ifcbrusque.app.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.ifcbrusque.app.data.db.Converters;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.di.ApplicationContext;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.*;
import static com.ifcbrusque.app.utils.AppConstants.PREF_NAME;

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
    public void setDataUltimaSincronizacaoAutomaticaNoticias(Date data) {
        long _data = Converters.dateToTimestamp(data);
        pref.edit().putLong(NOTICIAS_ULTIMA_SINCRONIZACAO, _data).apply();
    }

    @Override
    public Date getDataUltimaSincronizacaoAutomaticaNoticias() {
        return Converters.fromTimestamp(pref.getLong(NOTICIAS_ULTIMA_SINCRONIZACAO, 0));
    }

    @Override
    public void setUltimaPaginaAcessadaNoticias(int pagina) {
        pref.edit().putInt(NOTICIAS_ULTIMA_PAGINA_ACESSADA, pagina).apply();
    }

    @Override
    public int getUltimaPaginaAcessadaNoticias() {
        return pref.getInt(NOTICIAS_ULTIMA_PAGINA_ACESSADA, 1);
    }

    @Override
    public void setPreviewTopoRecyclerViewNoticias(int index) {
        pref.edit().putInt(NOTICIAS_PREVIEW_NO_TOPO, index).apply();
    }

    @Override
    public int getPreviewTopoRecyclerViewNoticias() {
        return pref.getInt(NOTICIAS_PREVIEW_NO_TOPO, 0);
    }

    @Override
    public int getNovoIdNotificacao() {
        final int id = pref.getInt(NOTIFICACOES_ULTIMO_ID, 100);
        pref.edit().putInt(NOTIFICACOES_ULTIMO_ID, id+1).apply();
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
}
