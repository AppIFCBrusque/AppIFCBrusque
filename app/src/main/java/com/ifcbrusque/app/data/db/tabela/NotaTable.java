package com.ifcbrusque.app.data.db.tabela;

import android.provider.BaseColumns;

public class NotaTable {
    private NotaTable() {
    }

    public static final String TABLE_NAME = "nota";
    public static final String ABREVIACAO = "abreviacao";
    public static final String DESCRICAO = "descricao";
    public static final String NOTA = "nota";
    public static final String NOTA_MAXIMA = "nota_maxima";
    public static final String PESO = "peso";
    public static final String DISCIPLINA_FRONT_END_ID = "disciplina_frontEndId";

    public static final String CRIAR_TABELA =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ABREVIACAO + " TEXT NOT NULL," +
                    DESCRICAO + " TEXT," +
                    NOTA + " FLOAT NOT NULL," +
                    NOTA_MAXIMA + "  FLOAT NOT NULL," +
                    PESO + " FLOAT," +
                    "FOREIGN KEY(" + DISCIPLINA_FRONT_END_ID + ") REFERENCES " + DisciplinaTable.TABLE_NAME + "(" + DisciplinaTable.FRONT_END_ID + "));";

    public static final String DELETAR_TABELA = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
