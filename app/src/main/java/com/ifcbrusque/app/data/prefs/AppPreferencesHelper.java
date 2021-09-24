package com.ifcbrusque.app.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.di.ApplicationContext;

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
    public void setUsuarioSIGAA(String login, String senha) {
        pref.edit().putString(SIGAA_LOGIN, login);
        pref.edit().putString(SIGAA_SENHA, senha);
        pref.edit().apply();
    }

    @Override
    public String getLoginSIGAA() {
        return pref.getString(SIGAA_LOGIN, "");
    }

    @Override
    public String getSenhaSIGAA() {
        return pref.getString(SIGAA_SENHA, "");
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
