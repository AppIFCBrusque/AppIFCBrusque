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

    @ColumnInfo(name = "tipo_repeticao")
    private int tipoRepeticao;
    public final static int REPETICAO_SEM = 0;
    public final static int REPETICAO_HORA = 1;
    public final static int REPETICAO_DIA = 2;
    public final static int REPETICAO_SEMANA = 3;
    public final static int REPETICAO_MES = 4;
    public final static int REPETICAO_ANO = 5;
    @ColumnInfo(name = "tempo_repeticao_personalizada")
    private long tempoRepeticaoPersonalizada;

    private int estado;
    public final static int ESTADO_INCOMPLETO = 1;
    public final static int ESTADO_COMPLETO = 2;

    public Lembrete(int tipo, String titulo, String descricao, Date dataLembrete, int tipoRepeticao, long tempoRepeticaoPersonalizada, int estado) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLembrete = dataLembrete;
        this.tipoRepeticao = tipoRepeticao;
        this.tempoRepeticaoPersonalizada = tempoRepeticaoPersonalizada;
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
    public int getTipoRepeticao() {return tipoRepeticao;}
    public void setTipoRepeticao(int tipoRepeticao) {this.tipoRepeticao = tipoRepeticao;}
    public long getTempoRepeticaoPersonalizada() {return tempoRepeticaoPersonalizada;}
    public void setTempoRepeticaoPersonalizada(long tempoRepeticaoPersonalizada) {this.tempoRepeticaoPersonalizada = tempoRepeticaoPersonalizada;}
    public int getEstado() {return estado;}
    public void setEstado(int estado) {this.estado = estado;}
}
