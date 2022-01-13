package com.ifcbrusque.app.data.network.noticias;

import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Observable;


public interface PgNoticiasHelper {
    Observable<ArrayList<Preview>> getPaginaNoticias(int numeroPagina);

    Observable<Noticia> getNoticiaWeb(Preview preview);
}
