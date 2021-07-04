package com.ifcbrusque.app.data.noticias.classe;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "preview_table")
public class Preview {
    @PrimaryKey(autoGenerate =  true)
    private int id;

    private String titulo;
    private String descricao;
    @ColumnInfo(name = "data_noticia")
    private String dataNoticia;
    @ColumnInfo(name = "url_noticia")
    private String urlNoticia;
    @ColumnInfo(name = "url_imagem_preview")
    private String urlImagemPreview;

    public Preview(String titulo, String descricao, String urlImagemPreview, String urlNoticia, String dataNoticia) {
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
    public String getDataNoticia() {return dataNoticia;}
    public String getUrlNoticia() {return urlNoticia;}
}
