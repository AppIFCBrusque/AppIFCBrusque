package com.ifcbrusque.app.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "lembrete_table")
public class Lembrete {
    @PrimaryKey(autoGenerate =  true)
    private int id;

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

    private int estado;
    public final static int ESTADO_INCOMPLETO = 1;
    public final static int COMPLETO = 2;

    public Lembrete(int tipo, String titulo, String descricao, Date dataLembrete, long tempoRepeticao, int estado) {
        this.tipo = tipo;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLembrete = dataLembrete;
        this.tempoRepeticao = tempoRepeticao;
        this.estado = estado;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
}
