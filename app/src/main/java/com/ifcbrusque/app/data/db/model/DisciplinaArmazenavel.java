package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.stacked.sigaa_ifc.Disciplina;

@Entity(tableName = "disciplina_table")
public class DisciplinaArmazenavel {
    @ColumnInfo(name = "id_no_sigaa")
    private String idNoSIGAA;
    private String nome, periodo;
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "front_end_id_turma")
    private String frontEndIdTurma = "";
    @ColumnInfo(name = "form_acessar_turma_virtual")
    private String form_acessarTurmaVirtual;
    @ColumnInfo(name = "form_acessar_turma_virtual_full")
    private String form_acessarTurmaVirtual_full;
    @ColumnInfo(name = "pagina_todas_turmas_virtuais")
    private boolean paginaTodasTurmasVirtuais;

    public DisciplinaArmazenavel() {

    }

    public DisciplinaArmazenavel(Disciplina disciplina) {
        idNoSIGAA = disciplina.getId();
        nome = disciplina.getNome();
        periodo = disciplina.getPeriodo();
        paginaTodasTurmasVirtuais = disciplina.isRetiradoDaPaginaTodasTurmasVirtuais();
        form_acessarTurmaVirtual = disciplina.getPostArgs()[0];
        form_acessarTurmaVirtual_full = disciplina.getPostArgs()[1];
        frontEndIdTurma = disciplina.getPostArgs()[2];
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

    public String getForm_acessarTurmaVirtual() {
        return form_acessarTurmaVirtual;
    }

    public void setForm_acessarTurmaVirtual(String form_acessarTurmaVirtual) {
        this.form_acessarTurmaVirtual = form_acessarTurmaVirtual;
    }

    public String getForm_acessarTurmaVirtual_full() {
        return form_acessarTurmaVirtual_full;
    }

    public void setForm_acessarTurmaVirtual_full(String form_acessarTurmaVirtual_full) {
        this.form_acessarTurmaVirtual_full = form_acessarTurmaVirtual_full;
    }

    public boolean isPaginaTodasTurmasVirtuais() {
        return paginaTodasTurmasVirtuais;
    }

    public void setPaginaTodasTurmasVirtuais(boolean paginaTodasTurmasVirtuais) {
        this.paginaTodasTurmasVirtuais = paginaTodasTurmasVirtuais;
    }

    public Disciplina getDisciplina() {
        return new Disciplina(paginaTodasTurmasVirtuais, periodo, nome, form_acessarTurmaVirtual, form_acessarTurmaVirtual_full, frontEndIdTurma);
    }
}
