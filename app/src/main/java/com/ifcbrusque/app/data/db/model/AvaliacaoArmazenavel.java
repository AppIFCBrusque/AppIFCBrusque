package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.imawa.sigaaforkotlin.entities.Avaliacao;
import com.imawa.sigaaforkotlin.entities.Disciplina;

import java.util.Date;

@Entity(tableName = "avaliacao_table")
public class AvaliacaoArmazenavel {
    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "id_no_sigaa")
    private long idNoSIGAA = 0;
    private String descricao;
    private Date dia;
    private String hora;
    @ColumnInfo(name = "disciplina_front_end_id_turma")
    private String disciplinaFrontEndIdTurma;

    public AvaliacaoArmazenavel() {

    }

    public AvaliacaoArmazenavel(Avaliacao avaliacao) {
        idNoSIGAA = avaliacao.getId();
        descricao = avaliacao.getDescricao();
        dia = avaliacao.getDia();
        hora = avaliacao.getHora();
        disciplinaFrontEndIdTurma = avaliacao.getDisciplina().getFrontEndIdTurma();
    }

    public long getIdNoSIGAA() {
        return idNoSIGAA;
    }

    public void setIdNoSIGAA(long idNoSIGAA) {
        this.idNoSIGAA = idNoSIGAA;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getDia() {
        return dia;
    }

    public void setDia(Date dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getDisciplinaFrontEndIdTurma() {
        return disciplinaFrontEndIdTurma;
    }

    public void setDisciplinaFrontEndIdTurma(String disciplinaFrontEndIdTurma) {
        this.disciplinaFrontEndIdTurma = disciplinaFrontEndIdTurma;
    }

    public Avaliacao getAvaliacao(Disciplina disciplina) {
        return new Avaliacao(idNoSIGAA, descricao, dia, hora, disciplina);
    }

    public Avaliacao getAvaliacao(DisciplinaArmazenavel disciplina) {
        return getAvaliacao(disciplina.getDisciplina());
    }
}
