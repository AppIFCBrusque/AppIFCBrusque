package com.ifcbrusque.app.data.db.tabela;

import android.provider.BaseColumns;

public class AvaliacaoTable {
    private AvaliacaoTable() {
    }

    public static final String TABLE_NAME = "avaliacao";
    public static final String DESCRICAO = "descricao";
    public static final String DATA_INICIO = "data_inicio";
    public static final String DATA_FIM = "data_fim";
    public static final String DISCIPLINA_FRONT_END_ID = "disciplina_frontEndId";

    public static final String CRIAR_TABELA =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    DESCRICAO + " TEXT NOT NULL," +
                    DATA_INICIO + " DATE NOT NULL," +
                    DATA_FIM + " DATE NOT NULL," +
                    "FOREIGN KEY(" + DISCIPLINA_FRONT_END_ID + ") REFERENCES " + DisciplinaTable.TABLE_NAME + "(" + DisciplinaTable.FRONT_END_ID + "));";

    public static final String DELETAR_TABELA = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
