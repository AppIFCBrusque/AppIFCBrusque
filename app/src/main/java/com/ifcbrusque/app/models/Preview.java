package com.ifcbrusque.app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "preview_table")
public class Preview {
    @PrimaryKey(autoGenerate =  true)
    private int id;

    private String titulo;
    private String descricao;
    @ColumnInfo(name = "data_noticia")
    private Date dataNoticia;
    @ColumnInfo(name = "url_noticia")
    private String urlNoticia;
    @ColumnInfo(name = "url_imagem_preview")
    private String urlImagemPreview;

    public Preview(String titulo, String descricao, String urlImagemPreview, String urlNoticia, Date dataNoticia) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.urlImagemPreview = urlImagemPreview;
        this.urlNoticia = urlNoticia;
        this.dataNoticia = dataNoticia;
    };

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getTitulo() {return titulo;}
    public String getDescricao() {return descricao;}
    public String getUrlImagemPreview() {return urlImagemPreview;}
    public Date getDataNoticia() {return dataNoticia;}
    public String getUrlNoticia() {return urlNoticia;}
}
