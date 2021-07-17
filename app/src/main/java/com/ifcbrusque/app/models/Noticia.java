package com.ifcbrusque.app.models;

import java.util.Date;

public class Noticia {
    String url, titulo, htmlConteudo;
    Date data;

    public Noticia(String url, String titulo, String htmlConteudo, Date data) {
        this.url = url;
        this.titulo = titulo;
        this.htmlConteudo = htmlConteudo;
        this.data = data;
    }

    public String getTitulo() {return titulo;}
    public String getHtmlConteudo() {return htmlConteudo;}
    public Date getData() {return data;}
}
