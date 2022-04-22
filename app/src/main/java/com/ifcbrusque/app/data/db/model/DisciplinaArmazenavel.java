package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.winterhazel.sigaaforkotlin.entities.Disciplina;

@Entity(tableName = "disciplina_table")
public class DisciplinaArmazenavel {
    @ColumnInfo(name = "id_no_sigaa")
    private String idNoSIGAA;
    private String nome, periodo;
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "front_end_id_turma")
    private String frontEndIdTurma = "";
    @ColumnInfo(name = "form_acessar_turma_virtual")
    private String formAcessarTurmaVirtual;
    @ColumnInfo(name = "form_acessar_turma_virtual_completo")
    private String formAcessarTurmaVirtualCompleto;

    public DisciplinaArmazenavel() {

    }

    public DisciplinaArmazenavel(Disciplina disciplina) {
        idNoSIGAA = disciplina.getId();
        nome = disciplina.getNome();
        periodo = disciplina.getPeriodo();
        formAcessarTurmaVirtual = disciplina.getFormAcessarTurmaVirtual();
        formAcessarTurmaVirtualCompleto = disciplina.getFormAcessarTurmaVirtualCompleto();
        frontEndIdTurma = disciplina.getFrontEndIdTurma();
    }

    public String getIdNoSIGAA() {
        return idNoSIGAA;
    }

    public void setIdNoSIGAA(String idNoSIGAA) {
        this.idNoSIGAA = idNoSIGAA;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getFrontEndIdTurma() {
        return frontEndIdTurma;
    }

    public void setFrontEndIdTurma(String frontEndIdTurma) {
        this.frontEndIdTurma = frontEndIdTurma;
    }

    public String getFormAcessarTurmaVirtual() {
        return formAcessarTurmaVirtual;
    }

    public void setFormAcessarTurmaVirtual(String formAcessarTurmaVirtual) {
        this.formAcessarTurmaVirtual = formAcessarTurmaVirtual;
    }

    public String getFormAcessarTurmaVirtualCompleto() {
        return formAcessarTurmaVirtualCompleto;
    }

    public void setFormAcessarTurmaVirtualCompleto(String formAcessarTurmaVirtualCompleto) {
        this.formAcessarTurmaVirtualCompleto = formAcessarTurmaVirtualCompleto;
    }

    public Disciplina getDisciplina() {
        return new Disciplina(idNoSIGAA, nome, periodo, formAcessarTurmaVirtual, formAcessarTurmaVirtualCompleto, frontEndIdTurma);
    }
}
