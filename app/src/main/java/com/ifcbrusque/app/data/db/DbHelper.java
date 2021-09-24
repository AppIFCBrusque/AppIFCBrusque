package com.ifcbrusque.app.data.db;

import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface DbHelper {
    Observable<List<Preview>> getPreviewsArmazenados();

    Observable<List<Preview>> armazenarPreviewsNovos(List<Preview> previews, boolean retornarPreviewsNovos);

    Observable<Noticia> getNoticia(String url);

    Observable<Long> inserirNoticia(Noticia noticia);

    Observable<Lembrete> getLembrete(long id);

    Observable<List<Lembrete>> getLembretesArmazenados();

    Observable<Long> inserirLembrete(Lembrete lembrete);

    Completable deletarLembrete(Lembrete lembrete);

    Completable atualizarLembrete(Lembrete lembrete);

    //TODO: Este método de baixo não é meio inutil?
    Completable alterarEstadoLembrete(long id, int novoEstado);

    Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(long idLembrete);
}
