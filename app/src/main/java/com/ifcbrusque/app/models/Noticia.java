package com.ifcbrusque.app.models;

import java.util.Date;

public class Noticia {
    String titulo, html;
    Date data;

    public Noticia(String titulo, String html, Date data) {
        this.titulo = titulo;
        this.html = html;
        this.data = data;
    }

    public String getTitulo() {return titulo;}
    public String getHtml() {return html;}
    public Date getData() {return data;}
}
