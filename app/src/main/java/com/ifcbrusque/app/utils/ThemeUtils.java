package com.ifcbrusque.app.utils;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.prefs.AppPreferencesHelper;
import com.ifcbrusque.app.ui.base.BaseActivity;

public class ThemeUtils {
    private ThemeUtils() {

    }

    public static void aplicarTema(BaseActivity activity) {
        AppPreferencesHelper preferencesHelper = new AppPreferencesHelper(activity);
        activity.setTheme(getResIdTema(preferencesHelper.getPrefTema()));
    }

    public static String getCorEmHex(int cor) {
        return String.format("#%06X", (0xFFFFFF & cor));
    }

    public static int getResIdTema(int idTema) {
        switch (idTema) {
            default:
            case 0:
                return R.style.Theme_IFCBrusque;

            case 2:
                return R.style.Theme_IFCBrusque_Noite;
        }
    }

    public static int getStringResIdTema(String idTema) {
        return getStringResIdTema(Integer.parseInt(idTema));
    }

    public static int getStringResIdTema(int idTema) {
        switch (idTema) {
            default:
            case 0:
                return R.string.tema_dia;

            case 2:
                return R.string.tema_meia_noite;
        }
    }
}
