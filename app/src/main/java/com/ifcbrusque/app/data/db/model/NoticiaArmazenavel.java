package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.imawa.sigaaforkotlin.entities.Noticia;

import java.util.Date;

/**
 * Classe para armazenar uma notícia do SIGAA
 */
@Entity(tableName = "noticia_sigaa_table")
public class NoticiaArmazenavel {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id_no_sigaa")
    private int idNoSIGAA = 0;
    private String titulo;
    @ColumnInfo(name = "html_conteudo")
    private String htmlConteudo;
    @ColumnInfo(name = "j_id_jsp")
    private String jIdJsp;
    @ColumnInfo(name = "j_id_jsp_completo")
    private String jIdJspCompleto;
    @ColumnInfo(name = "disciplina_nome")
    private String disciplinaNome;
    @ColumnInfo(name = "disciplina_front_end_id_turma")
    private String disciplinaFrontEndIdTurma;
    private Date data;

    public NoticiaArmazenavel() {

    }

    public NoticiaArmazenavel(Noticia noticia) {
        this.idNoSIGAA = noticia.getId();
        this.titulo = noticia.getTitulo();
        this.htmlConteudo = noticia.getHtmlConteudo();
        this.jIdJsp = noticia.getJIdJsp();
        this.jIdJspCompleto = noticia.getJIdJspCompleto();
        this.disciplinaNome = noticia.getDisciplina().getNome();
        this.disciplinaFrontEndIdTurma = noticia.getDisciplina().getFrontEndIdTurma();
        this.data = noticia.getData();
    }

    public int getIdNoSIGAA() {
        return idNoSIGAA;
    }

    public void setIdNoSIGAA(int idNoSIGAA) {
        this.idNoSIGAA = idNoSIGAA;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDisciplinaNome() {
        return disciplinaNome;
    }

    public void setDisciplinaNome(String disciplinaNome) {
        this.disciplinaNome = disciplinaNome;
    }

    public String getDisciplinaFrontEndIdTurma() {
        return disciplinaFrontEndIdTurma;
    }

    public void setDisciplinaFrontEndIdTurma(String disciplinaFrontEndIdTurma) {
        this.disciplinaFrontEndIdTurma = disciplinaFrontEndIdTurma;
    }

    public String getJIdJsp() {
        return jIdJsp;
    }

    public void setJIdJsp(String jIdJsp) {
        this.jIdJsp = jIdJsp;
    }

    public String getJIdJspCompleto() {
        return jIdJspCompleto;
    }

    public void setJIdJspCompleto(String jIdJspCompleto) {
        this.jIdJspCompleto = jIdJspCompleto;
    }

    public String getHtmlConteudo() {
        return htmlConteudo;
    }

    public void setHtmlConteudo(String htmlConteudo) {
        this.htmlConteudo = htmlConteudo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
