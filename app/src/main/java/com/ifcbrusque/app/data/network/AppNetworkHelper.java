package com.ifcbrusque.app.data.network;

import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.noticias.AppPgNoticiasHelper;
import com.ifcbrusque.app.data.network.sigaa.AppSIGAAHelper;
import com.ifcbrusque.app.data.network.sigaa.SIGAAHelper;
import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Nota;
import com.stacked.sigaa_ifc.Usuario;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class AppNetworkHelper implements NetworkHelper {
    private AppPgNoticiasHelper mPgNoticiasHelper;
    private SIGAAHelper mSIGAAHelper;

    @Inject
    public AppNetworkHelper(AppPgNoticiasHelper noticiasHelper, AppSIGAAHelper SIGAAHelper) {
        mPgNoticiasHelper = noticiasHelper;
        mSIGAAHelper = SIGAAHelper;
    }

    @Override
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return mPgNoticiasHelper.getPaginaNoticias(numeroPagina);
    }

    @Override
    public Observable<Noticia> getNoticiaWeb(Preview preview) {
        return mPgNoticiasHelper.getNoticiaWeb(preview);
    }

    @Override
    public Usuario getUsuarioSIGAA() {
        return mSIGAAHelper.getUsuarioSIGAA();
    }

    @Override
    public Observable<Boolean> logarSIGAA(String usuario, String senha) {
        return mSIGAAHelper.logarSIGAA(usuario, senha);
    }

    @Override
    public Observable<ArrayList<Nota>> getNotasDisciplina(Disciplina disciplina) {
        return mSIGAAHelper.getNotasDisciplina(disciplina);
    }
}
