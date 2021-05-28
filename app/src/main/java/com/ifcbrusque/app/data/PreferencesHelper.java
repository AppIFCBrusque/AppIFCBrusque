package com.ifcbrusque.app.data;

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
        editor.putString("sig_login", login);
        editor.putString("sig_pass", senha);
        editor.commit();
    }

    public String getLoginSIGAA() {
        return pref.getString("sig_login", "");
    }

    public String getSenhaSIGAA() {
        return pref.getString("sig_pass", "");
    }
}