package com.ifcbrusque.app.data.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.ifcbrusque.app.R;
import com.stacked.sigaa_ifc.Avaliacao;
import com.stacked.sigaa_ifc.Questionario;
import com.stacked.sigaa_ifc.Tarefa;

import java.util.Date;

@Entity(tableName = "lembrete_table")
public class Lembrete {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "id_notificacao")
    private long idNotificacao;

    private int tipo;
    public final static int LEMBRETE_PESSOAL = 1;
    public final static int LEMBRETE_AVALIACAO = 2;
    public final static int LEMBRETE_TAREFA = 3;
    public final static int LEMBRETE_QUESTIONARIO = 4;
    private String idObjetoAssociado;

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

    public static int getIdDaStringRepeticao(int tipoRepeticao) {
        switch (tipoRepeticao) {
            case Lembrete.REPETICAO_HORA:
                return R.string.repeticao_lembretes_hora;

            case Lembrete.REPETICAO_DIA:
                return R.string.repeticao_lembretes_dia;

            case Lembrete.REPETICAO_SEMANA:
                return R.string.repeticao_lembretes_semana;

            case Lembrete.REPETICAO_MES:
                return R.string.repeticao_lembretes_mes;

            case Lembrete.REPETICAO_ANO:
                return R.string.repeticao_lembretes_ano;

            default:
                return R.string.repeticao_lembretes_nao_repetir;
        }
    }

    private int estado;
    public final static int ESTADO_INCOMPLETO = 1;
    public final static int ESTADO_COMPLETO = 2;

    public Lembrete(int tipo, String idObjetoAssociado, String titulo, String descricao, Date dataLembrete, int tipoRepeticao, long tempoRepeticaoPersonalizada, int estado, long idNotificacao) {
        this.tipo = tipo;
        this.idObjetoAssociado = idObjetoAssociado;
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataLembrete = dataLembrete;
        this.tipoRepeticao = tipoRepeticao;
        this.tempoRepeticaoPersonalizada = tempoRepeticaoPersonalizada;
        this.estado = estado;
        this.idNotificacao = idNotificacao;
    }

    public Lembrete(Avaliacao a, long idNotificacao) {
        this(LEMBRETE_AVALIACAO, Long.toString(a.getId()), a.getDescricao(), "", a.getData(), REPETICAO_SEM, 0, ESTADO_INCOMPLETO, idNotificacao);
    }

    public Lembrete(Tarefa t, long idNotificacao) {
        this(LEMBRETE_TAREFA, t.getId(), t.getTitulo(), t.getDescricao(), t.getFim(), REPETICAO_SEM, 0, (t.isEnviada()) ? ESTADO_COMPLETO : ESTADO_INCOMPLETO, idNotificacao);
    }

    public Lembrete(Questionario q, long idNotificacao) {
        this(LEMBRETE_QUESTIONARIO, Long.toString(q.getId()), q.getTitulo(), "", q.getDataFim(), REPETICAO_SEM, 0, (q.isEnviado()) ? ESTADO_COMPLETO : ESTADO_INCOMPLETO, idNotificacao);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdNotificacao() {
        return idNotificacao;
    }

    public void setIdNotificacao(long idNotificacao) {
        this.idNotificacao = idNotificacao;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getIdObjetoAssociado() {
        return idObjetoAssociado;
    }

    public void setIdObjetoAssociado(String idObjetoAssociado) {
        this.idObjetoAssociado = idObjetoAssociado;
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

    public Date getDataLembrete() {
        return dataLembrete;
    }

    public void setDataLembrete(Date dataLembrete) {
        this.dataLembrete = dataLembrete;
    }

    public int getTipoRepeticao() {
        return tipoRepeticao;
    }

    public void setTipoRepeticao(int tipoRepeticao) {
        this.tipoRepeticao = tipoRepeticao;
    }

    public long getTempoRepeticaoPersonalizada() {
        return tempoRepeticaoPersonalizada;
    }

    public void setTempoRepeticaoPersonalizada(long tempoRepeticaoPersonalizada) {
        this.tempoRepeticaoPersonalizada = tempoRepeticaoPersonalizada;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
