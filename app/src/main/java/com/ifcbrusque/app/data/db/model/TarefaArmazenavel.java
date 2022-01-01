package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Tarefa;

import java.util.Date;

@Entity(tableName = "tarefa_table")
public class TarefaArmazenavel {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id_no_sigaa")
    private String idNoSIGAA = "";

    private String titulo, descricao;
    @ColumnInfo(name = "url_download")
    private String urlDownload;
    @ColumnInfo(name = "data_inicio")
    private Date dataInicio;
    @ColumnInfo(name = "data_fim")
    private Date dataFim;
    private int envios;
    private boolean enviavel, enviada, corrigida;
    @ColumnInfo(name = "disciplina_front_end_id_turma")
    private String disciplinaFrontEndIdTurma;
    private String j_id;
    @ColumnInfo(name = "j_id_enviar")
    private String j_idEnviar;
    @ColumnInfo(name = "j_id_visualizar")
    private String j_idVisualizar;

    public TarefaArmazenavel() {

    }

    public TarefaArmazenavel(Tarefa tarefa) {
        idNoSIGAA = tarefa.getId();
        titulo = tarefa.getTitulo();
        descricao = tarefa.getDescricao();
        urlDownload = tarefa.getUrlArquivo();
        dataInicio = tarefa.getInicio();
        dataFim = tarefa.getFim();
        envios = tarefa.getEnvios();
        enviavel = tarefa.isEnviavel();
        enviada = tarefa.isEnviada();
        corrigida = tarefa.isCorrigida();
        disciplinaFrontEndIdTurma = tarefa.getDisciplina().getPostArgs()[2];
        j_id = tarefa.getJ_Id();
        j_idEnviar = tarefa.getJ_IdEnviar();
        j_idVisualizar = tarefa.getJ_idVisualizar();
    }

    public String getIdNoSIGAA() {
        return idNoSIGAA;
    }

    public void setIdNoSIGAA(String idNoSIGAA) {
        this.idNoSIGAA = idNoSIGAA;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlDownload() {
        return urlDownload;
    }

    public void setUrlDownload(String urlDownload) {
        this.urlDownload = urlDownload;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public int getEnvios() {
        return envios;
    }

    public void setEnvios(int envios) {
        this.envios = envios;
    }

    public boolean isEnviavel() {
        return enviavel;
    }

    public void setEnviavel(boolean enviavel) {
        this.enviavel = enviavel;
    }

    public boolean isEnviada() {
        return enviada;
    }

    public void setEnviada(boolean enviada) {
        this.enviada = enviada;
    }

    public boolean isCorrigida() {
        return corrigida;
    }

    public void setCorrigida(boolean corrigida) {
        this.corrigida = corrigida;
    }

    public String getDisciplinaFrontEndIdTurma() {
        return disciplinaFrontEndIdTurma;
    }

    public void setDisciplinaFrontEndIdTurma(String disciplinaFrontEndIdTurma) {
        this.disciplinaFrontEndIdTurma = disciplinaFrontEndIdTurma;
    }

    public String getJ_id() {
        return j_id;
    }

    public void setJ_id(String j_id) {
        this.j_id = j_id;
    }

    public String getJ_idEnviar() {
        return j_idEnviar;
    }

    public void setJ_idEnviar(String j_idEnviar) {
        this.j_idEnviar = j_idEnviar;
    }

    public String getJ_idVisualizar() {
        return j_idVisualizar;
    }

    public void setJ_idVisualizar(String j_idVisualizar) {
        this.j_idVisualizar = j_idVisualizar;
    }

    public Tarefa getTarefa(DisciplinaArmazenavel disciplinaArmazenavel) {
        Tarefa t = new Tarefa(disciplinaArmazenavel.getDisciplina(), titulo, descricao, dataInicio, dataFim, envios, enviavel, enviada, corrigida);
        t.setUrlArquivo(urlDownload);
        t.setId(idNoSIGAA);
        t.setJ_Id(j_id);
        t.setIdEnvio(j_idEnviar);
        t.setIdVisualizacao(j_idVisualizar);
        return t;
    }

    public Tarefa getTarefa(Disciplina disciplina) {
        Tarefa t = new Tarefa(disciplina, titulo, descricao, dataInicio, dataFim, envios, enviavel, enviada, corrigida);
        t.setUrlArquivo(urlDownload);
        t.setId(idNoSIGAA);
        t.setJ_Id(j_id);
        t.setIdEnvio(j_idEnviar);
        t.setIdVisualizacao(j_idVisualizar);
        return t;
    }
}