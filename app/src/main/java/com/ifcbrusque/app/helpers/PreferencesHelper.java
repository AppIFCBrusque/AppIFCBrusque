package com.ifcbrusque.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {
    private SharedPreferences pref;
    SharedPreferences.Editor editor;

    public PreferencesHelper(Context context) {
        pref = context.getSharedPreferences("com.ifcbrusque.app.sharedPreferences", Context.MODE_PRIVATE); //TODO: Salvar a key em outro arquivo e ignorar no git
        editor = pref.edit();
    }

    public void setUsuarioSIGAA(String login, String senha) { //TODO: https://github.com/sveinungkb/encrypted-userprefs
        editor.putString("sig_login", login); //TODO: padronizar valores em outro arquivo
        editor.putString("sig_pass", senha);
        editor.commit();
    }
    public String getLoginSIGAA() {
        return pref.getString("sig_login", "");
    }
    public String getSenhaSIGAA() {
        return pref.getString("sig_pass", "");
    }


    public void setUltimaPagina(Integer pagina) {
        editor.putInt("noticias_ultima_pagina", pagina);
        editor.commit();
    }
    public Integer getUltimaPaginaNoticias() {return pref.getInt("noticias_ultima_pagina", 1);}
}
