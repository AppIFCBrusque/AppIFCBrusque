package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.stacked.sigaa_ifc.Disciplina;
import com.stacked.sigaa_ifc.Questionario;

import java.util.Date;

@Entity(tableName = "questionario_table")
public class QuestionarioArmazenavel {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id_no_sigaa")
    private long idNoSIGAA;
    private String titulo;
    private boolean enviado;
    @ColumnInfo(name = "data_inicio")
    private Date dataInicio;
    @ColumnInfo(name = "data_fim")
    private Date dataFim;
    @ColumnInfo(name = "disciplina_front_end_id_turma")
    private String disciplinaFrontEndIdTurma;

    public QuestionarioArmazenavel() {

    }

    public QuestionarioArmazenavel(Questionario questionario) {
        idNoSIGAA = questionario.getId();
        titulo = questionario.getTitulo();
        enviado = questionario.isEnviado();
        dataInicio = questionario.getDataInicio();
        dataFim = questionario.getDataFim();
        disciplinaFrontEndIdTurma = questionario.getDisciplina().getPostArgs()[2];
    }

    public long getIdNoSIGAA() {
        return idNoSIGAA;
    }

    public void setIdNoSIGAA(long idNoSIGAA) {
        this.idNoSIGAA = idNoSIGAA;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
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

    public String getDisciplinaFrontEndIdTurma() {
        return disciplinaFrontEndIdTurma;
    }

    public void setDisciplinaFrontEndIdTurma(String disciplinaFrontEndIdTurma) {
        this.disciplinaFrontEndIdTurma = disciplinaFrontEndIdTurma;
    }

    public Questionario getQuestionario(Disciplina disciplina) {
        return new Questionario(idNoSIGAA, titulo, enviado, dataInicio, dataFim, disciplina);
    }

    public Questionario getQuestionario(DisciplinaArmazenavel disciplina) {
        return getQuestionario(disciplina.getDisciplina());
    }
}
