package com.ifcbrusque.app.util.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import static com.ifcbrusque.app.util.preferences.PreferenceValues.*;

/*
Classe com funções para utilizar o SharedPreferences
 */
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
        final int vezes = pref.getInt(TESTE_SERVICO_VEZES, 0);
        editor.putInt(TESTE_SERVICO_VEZES, vezes+1);
        editor.commit();
    }
}
