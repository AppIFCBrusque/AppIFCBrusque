package com.ifcbrusque.app.models;

public class Noticia {
    String titulo, html, data;

    public Noticia(String titulo, String html, String data) {
        this.titulo = titulo;
        this.html = html;
        this.data = data;
    }

    public String getTitulo() {return titulo;}
    public String getHtml() {return html;}
    public String getData() {return data;}
}
