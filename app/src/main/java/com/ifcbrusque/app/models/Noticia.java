package com.ifcbrusque.app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "noticia_table")
public class Noticia {
    @PrimaryKey(autoGenerate =  true)
    private int id;

    private String url;
    private String titulo;
    @ColumnInfo(name = "html_conteudo")
    private String htmlConteudo;
    @ColumnInfo(name = "data_noticia")
    private Date data;

    public Noticia(String url, String titulo, String htmlConteudo, Date data) {
        this.url = url;
        this.titulo = titulo;
        this.htmlConteudo = htmlConteudo;
        this.data = data;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getUrl() {return url;}
    public String getTitulo() {return titulo;}
    public String getHtmlConteudo() {return htmlConteudo;}
    public Date getData() {return data;}
}
