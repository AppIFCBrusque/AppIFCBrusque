package com.ifcbrusque.app.data.noticias.classe;

import java.util.Date;

public class Preview {
    private String titulo, descricao, urlImagemPreview, data, urlNoticia;

    public Preview(String titulo, String descricao, String urlImagemPreview, String urlNoticia, String data) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.urlImagemPreview = urlImagemPreview;
        this.urlNoticia = urlNoticia;
        this.data = data;
    };

    public String getTitulo() {return titulo;}
    public String getDescricao() {return descricao;}
    public String getUrlImagemPreview() {return urlImagemPreview;}
    public String getData() {return data;}
    public String getUrlNoticia() {return urlNoticia;}
}
