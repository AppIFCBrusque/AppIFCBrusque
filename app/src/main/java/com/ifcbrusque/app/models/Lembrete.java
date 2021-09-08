package com.ifcbrusque.app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "lembrete_table")
public class Lembrete {
    @PrimaryKey(autoGenerate =  true)
    private long id;

    @ColumnInfo(name = "id_notificacao")
    private long idNotificacao;

    private int tipo;
    public final static int LEMBRETE_PESSOAL = 1;
    public final static int LEMBRETE_TAREFA = 2;
    public final static int LEMBRETE_QUESTIONARIO = 3;

    private String titulo;
    private String descricao;
    @ColumnInfo(name = "data_lembrete")
    private Date dataLembrete;

    @ColumnInfo(name = "tempo_repeticao")
    private long tempoRepeticao;
    public final static int REPETICAO_NAO_REPETIR = -1;

    private int estado;
    public final static int ESTADO_INCOMPLETO = 1;
    public final static int ESTADO_COMPLETO = 2;

    public Lembrete(int tipo, String titulo, String descricao, Date dataLembrete, long tempoRepeticao, int estado) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLembrete = dataLembrete;
        this.tempoRepeticao = tempoRepeticao;
        this.estado = estado;
    }

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public long getIdNotificacao() {return idNotificacao;}
    public void setIdNotificacao(long idNotificacao) {this.idNotificacao = idNotificacao;}

    public int getTipo() {return tipo;}
    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public String getDescricao() {return descricao;}
    public void setDescricao(String descricao) {this.descricao = descricao;}
    public Date getDataLembrete() {return dataLembrete;}
    public void setDataLembrete(Date dataLembrete) {this.dataLembrete = dataLembrete;}
    public long getTempoRepeticao() {return tempoRepeticao;}
    public int getEstado() {return estado;}
    public void setEstado(int estado) {this.estado = estado;}
}
