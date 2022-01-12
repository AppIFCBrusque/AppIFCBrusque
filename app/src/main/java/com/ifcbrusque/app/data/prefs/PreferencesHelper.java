package com.ifcbrusque.app.data.prefs;

import java.util.Date;

public interface PreferencesHelper {
    boolean getPrimeiraInicializacao();

    void setPrimeiraInicializacao(boolean b);

    boolean getPrimeiraSincronizacaoNoticias();

    void setPrimeiraSincronizacaoNoticias(boolean b);

    boolean getSIGAAConectado();

    void setSIGAAConectado(boolean b);

    String getLoginSIGAA();

    void setLoginSIGAA(String login);

    String getSenhaSIGAA();

    void setSenhaSIGAA(String senha);

    void setDataUltimaSincronizacaoAutomaticaNoticias(Date data);

    Date getDataUltimaSincronizacaoAutomaticaNoticias();

    void setDataUltimaSincronizacaoCompleta(Date data);

    Date getDataUltimaSincronizacaoCompleta();

    void setUltimaPaginaAcessadaNoticias(int pagina);

    int getUltimaPaginaAcessadaNoticias();

    void setPreviewTopoRecyclerViewNoticias(int index);

    int getPreviewTopoRecyclerViewNoticias();

    int getNovoIdNotificacao();

    int getUltimaCategoriaAcessadaHome();

    void setUltimaCategoriaAcessadaHome(int categoria);
}
