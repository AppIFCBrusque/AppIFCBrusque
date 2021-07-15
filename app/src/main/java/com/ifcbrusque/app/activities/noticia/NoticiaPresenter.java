package com.ifcbrusque.app.activities.noticia;

import android.os.Bundle;

import java.util.Date;

import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.Converters.*;

public class NoticiaPresenter  {
    private View view;

    private String tituloNoticia, urlNoticia, urlImagemPreview;
    private Date dataNoticia;

    public NoticiaPresenter(NoticiaPresenter.View view, Bundle bundle) {
        this.view = view;

        this.tituloNoticia = bundle.getString(NOTICIA_TITULO);
        this.dataNoticia = fromTimestamp(bundle.getLong(NOTICIA_DATA));
        this.urlNoticia = bundle.getString(NOTICIA_URL);
        this.urlImagemPreview = bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW);
    }

    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void carregarWebView(String url);
    }
}
