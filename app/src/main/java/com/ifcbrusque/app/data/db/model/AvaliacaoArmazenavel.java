package com.ifcbrusque.app.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Disciplina;

import java.util.Date;

@Entity(tableName = "avaliacao_table")
public class AvaliacaoArmazenavel {
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id_no_sigaa")
    private long idNoSIGAA = 0;
    private String descricao;
    private Date data;
    @ColumnInfo(name = "disciplina_front_end_id_turma")
    private String disciplinaFrontEndIdTurma;

    public AvaliacaoArmazenavel() {

    }

    public AvaliacaoArmazenavel(Avaliacao avaliacao) {
        idNoSIGAA = avaliacao.getId();
        descricao = avaliacao.getDescricao();
        data = avaliacao.getData();
        disciplinaFrontEndIdTurma = avaliacao.getDisciplina().getPostArgs()[2];
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

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDisciplinaFrontEndIdTurma() {
        return disciplinaFrontEndIdTurma;
    }

    public void setDisciplinaFrontEndIdTurma(String disciplinaFrontEndIdTurma) {
        this.disciplinaFrontEndIdTurma = disciplinaFrontEndIdTurma;
    }

    public Avaliacao getAvaliacao(Disciplina disciplina) {
        return new Avaliacao(idNoSIGAA, disciplina, data, descricao);
    }
}
