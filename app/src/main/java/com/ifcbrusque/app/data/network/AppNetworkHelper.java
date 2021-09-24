package com.ifcbrusque.app.data.network;

import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.network.noticias.AppPgNoticiasHelper;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;

@Singleton
public class AppNetworkHelper implements NetworkHelper {
    private AppPgNoticiasHelper mPgNoticiasHelper;

    @Inject
    public AppNetworkHelper(AppPgNoticiasHelper noticiasHelper) {
        mPgNoticiasHelper = noticiasHelper;
    }

    @Override
    public Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina) {
        return mPgNoticiasHelper.getPaginaNoticias(numeroPagina);
    }

    @Override
    public Observable<Noticia> getNoticiaWeb(Preview preview) {
        return mPgNoticiasHelper.getNoticiaWeb(preview);
    }
}
