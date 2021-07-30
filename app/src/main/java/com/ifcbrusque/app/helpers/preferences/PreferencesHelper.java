package com.ifcbrusque.app.helpers.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static com.ifcbrusque.app.activities.MainActivity.TAG;
import static com.ifcbrusque.app.helpers.preferences.PreferenceValues.*;

public class PreferencesHelper {
    private SharedPreferences pref;
    SharedPreferences.Editor editor;

    public PreferencesHelper(Context context) {
        pref = context.getSharedPreferences("com.ifcbrusque.app.sharedPreferences", Context.MODE_PRIVATE); //TODO: Salvar a key em outro arquivo e ignorar no git
        editor = pref.edit();
    }

    public void setUsuarioSIGAA(String login, String senha) { //TODO: https://github.com/sveinungkb/encrypted-userprefs
        editor.putString(SIGAA_LOGIN, login); //TODO: padronizar valores em outro arquivo
        editor.putString(SIGAA_SENHA, senha);
        editor.commit();
    }
    public String getLoginSIGAA() {
        return pref.getString(SIGAA_LOGIN, "");
    }
    public String getSenhaSIGAA() {
        return pref.getString(SIGAA_SENHA, "");
    }


    public void setUltimaPagina(Integer pagina) {
        editor.putInt(NOTICIAS_ULTIMA_PAGINA_ACESSADA, pagina);
        editor.commit();
    }
    public Integer getUltimaPaginaNoticias() {
        return pref.getInt(NOTICIAS_ULTIMA_PAGINA_ACESSADA, 1);
    }

    public void setPreviewTopo(Integer index) {
        editor.putInt(NOTICIAS_PREVIEW_NO_TOPO, index);
        editor.commit();
    }
    public Integer getPreviewTopo() {
        return pref.getInt(NOTICIAS_PREVIEW_NO_TOPO, 0);
    }

    /**
     * Retorna e automaticamente incrementa um id para as notificações
     */
    public Integer getUltimoIdNotificacoes() {
        final int id = pref.getInt(NOTIFICACOES_ULTIMO_ID, 100);

        editor.putInt(NOTIFICACOES_ULTIMO_ID, id+1);
        editor.commit();

        return id;
    }

    /*
    Utilizado para testes
     */
    public void incrementarVezesServico() {
        final int vezes = pref.getInt(NOTIFICACOES_ULTIMO_ID, 0);
        editor.putInt(NOTIFICACOES_ULTIMO_ID, vezes+1);
        editor.commit();

        Log.d(TAG, "incrementarVezesServico: serviço incrementado");
    }
}
