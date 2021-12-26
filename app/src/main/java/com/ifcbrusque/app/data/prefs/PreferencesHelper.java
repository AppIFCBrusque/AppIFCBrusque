package com.ifcbrusque.app.data.prefs;

import java.util.Date;

public interface PreferencesHelper {
    void setUsuarioSIGAA(String login, String senha);

    String getLoginSIGAA();

    String getSenhaSIGAA();

    void setDataUltimaSincronizacaoAutomaticaNoticias(Date data);

    Date getDataUltimaSincronizacaoAutomaticaNoticias();

    void setUltimaPaginaAcessadaNoticias(int pagina);

    int getUltimaPaginaAcessadaNoticias();

    void setPreviewTopoRecyclerViewNoticias(int index);

    int getPreviewTopoRecyclerViewNoticias();

    int getNovoIdNotificacao();

    int getUltimaCategoriaAcessadaHome();

    void setUltimaCategoriaAcessadaHome(int categoria);
}
