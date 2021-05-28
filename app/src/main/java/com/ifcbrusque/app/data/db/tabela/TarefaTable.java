package com.ifcbrusque.app.data.db.tabela;

import android.provider.BaseColumns;

public class TarefaTable {
    private TarefaTable() {
    }

    public static final String TABLE_NAME = "tarefa";
    public static final String ID = "_id";
    public static final String TITULO = "titulo";
    public static final String DESCRICAO = "descricao";
    public static final String URL_ARQUIVO = "url_arquivo";
    public static final String ENVIOS = "envios";
    public static final String ENVIAVEL = "enviavel";
    public static final String ENVIADA = "enviada";
    public static final String CORRIGIDA = "corrigida";
    public static final String DATA_INICIO = "data_inicio";
    public static final String DATA_FIM = "data_fim";
    public static final String DISCIPLINA_FRONT_END_ID = "disciplina_frontEndId";

    public static final String CRIAR_TABELA =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " TEXT NOT NULL PRIMARY KEY," +
                    TITULO + " TEXT NOT NULL," +
                    DESCRICAO + " TEXT NOT NULL," +
                    URL_ARQUIVO + " TEXT," +
                    ENVIOS + " INTEGER NOT NULL," +
                    ENVIAVEL + " BOOLEAN NOT NULL," +
                    ENVIADA + " BOOLEAN NOT NULL," +
                    CORRIGIDA + " BOOLEAN NOT NULL," +
                    DATA_INICIO + " DATE NOT NULL," +
                    DATA_FIM + " DATE NOT NULL," +
                    "FOREIGN KEY(" + DISCIPLINA_FRONT_END_ID + ") REFERENCES " + DisciplinaTable.TABLE_NAME + "(" + DisciplinaTable.FRONT_END_ID + "));";

    public static final String DELETAR_TABELA = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
