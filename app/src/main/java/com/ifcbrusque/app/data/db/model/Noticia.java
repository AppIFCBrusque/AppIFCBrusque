package com.ifcbrusque.app.data.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "noticia_table")
public class Noticia {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private final String url;
    private final String titulo;
    @ColumnInfo(name = "html_conteudo")
    private final String htmlConteudo;
    @ColumnInfo(name = "data_noticia")
    private final Date data;
    @ColumnInfo(name = "data_formatada")
    private final String dataFormatada;

    public Noticia(String url, String titulo, String htmlConteudo, Date data, String dataFormatada) {
        this.url = url;
        this.titulo = titulo;
        this.htmlConteudo = htmlConteudo;
        this.data = data;
        this.dataFormatada = dataFormatada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getHtmlConteudo() {
        return htmlConteudo;
    }

    public Date getData() {
        return data;
    }

    public String getDataFormatada() {
        return dataFormatada;
    }
}
