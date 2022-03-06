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

    String getNomeDoUsuarioSIGAA();

    void setNomeDoUsuarioSIGAA(String s);

    void setDataUltimaSincronizacaoAutomaticaNoticias(Date data);

    Date getDataUltimaSincronizacaoAutomaticaNoticias();

    void setDataUltimaSincronizacaoCompleta(Date data);

    Date getDataUltimaSincronizacaoCompleta();

    void setUltimaPaginaAcessadaNoticias(int pagina);

    int getUltimaPaginaAcessadaNoticias();

    int getNovoIdNotificacao();

    int getUltimaCategoriaAcessadaHome();

    void setUltimaCategoriaAcessadaHome(int categoria);

    int getPrefTema();

    boolean getPrefNotificarLembretes();

    boolean getPrefNotificarNoticiasDoCampusNovas();

    boolean getPrefNotificarAvaliacoesNovas();

    boolean getPrefNotificarAvaliacoesAlteradas();

    boolean getPrefNotificarTarefasNovas();

    boolean getPrefNotificarTarefasAlteradas();

    boolean getPrefNotificarQuestionariosNovos();

    boolean getPrefNotificarQuestionariosAlterados();

    boolean getPrefSincronizarSIGAAA();

    void setPrefSincronizarSIGAA(boolean b);
}
