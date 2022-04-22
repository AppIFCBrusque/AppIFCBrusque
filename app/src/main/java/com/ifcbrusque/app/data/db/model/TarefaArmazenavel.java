package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.winterhazel.sigaaforkotlin.entities.Disciplina;
import com.winterhazel.sigaaforkotlin.entities.Tarefa;

import java.util.Date;

@Entity(tableName = "tarefa_table")
public class TarefaArmazenavel {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id_no_sigaa")
    private String idNoSIGAA = "";
    private String titulo, descricao;
    @ColumnInfo(name = "url_arquivo")
    private String urlArquivo;
    @ColumnInfo(name = "data_inicio")
    private Date dataInicio;
    @ColumnInfo(name = "data_fim")
    private Date dataFim;
    private int envios;
    private boolean enviavel, enviada, corrigida, individual;
    private String jId;
    @ColumnInfo(name = "j_id_enviar")
    private String jIdEnviar;
    @ColumnInfo(name = "j_id_visualizar")
    private String jIdVisualizar;
    @ColumnInfo(name = "disciplina_front_end_id_turma")
    private String disciplinaFrontEndIdTurma;

    public TarefaArmazenavel() {

    }

    public TarefaArmazenavel(Tarefa tarefa) {
        idNoSIGAA = tarefa.getId();
        titulo = tarefa.getTitulo();
        descricao = tarefa.getDescricao();
        urlArquivo = tarefa.getUrlArquivo();
        dataInicio = tarefa.getDataInicio();
        dataFim = tarefa.getDataFim();
        envios = tarefa.getEnvios();
        enviavel = tarefa.isEnviavel();
        enviada = tarefa.isEnviada();
        corrigida = tarefa.isCorrigida();
        individual = tarefa.isIndividual();
        disciplinaFrontEndIdTurma = tarefa.getDisciplina().getFrontEndIdTurma();
        jId = tarefa.getJId();
        jIdEnviar = tarefa.getJIdEnviar();
        jIdVisualizar = tarefa.getJIdVisualizar();
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

    public String getUrlArquivo() {
        return urlArquivo;
    }

    public void setUrlArquivo(String urlArquivo) {
        this.urlArquivo = urlArquivo;
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

    public boolean isIndividual() {
        return individual;
    }

    public void setIndividual(boolean individual) {
        this.individual = individual;
    }

    public String getDisciplinaFrontEndIdTurma() {
        return disciplinaFrontEndIdTurma;
    }

    public void setDisciplinaFrontEndIdTurma(String disciplinaFrontEndIdTurma) {
        this.disciplinaFrontEndIdTurma = disciplinaFrontEndIdTurma;
    }

    public String getJId() {
        return jId;
    }

    public void setJId(String jId) {
        this.jId = jId;
    }

    public String getJIdEnviar() {
        return jIdEnviar;
    }

    public void setJIdEnviar(String jIdEnviar) {
        this.jIdEnviar = jIdEnviar;
    }

    public String getJIdVisualizar() {
        return jIdVisualizar;
    }

    public void setJIdVisualizar(String jIdVisualizar) {
        this.jIdVisualizar = jIdVisualizar;
    }

    public Tarefa getTarefa(Disciplina disciplina) {
        return new Tarefa(idNoSIGAA, titulo, descricao, urlArquivo, dataInicio, dataFim, envios, enviavel, enviada, corrigida, individual, jId, jIdEnviar, jIdVisualizar, disciplina);
    }

    public Tarefa getTarefa(DisciplinaArmazenavel disciplinaArmazenavel) {
        return getTarefa(disciplinaArmazenavel.getDisciplina());
    }
}