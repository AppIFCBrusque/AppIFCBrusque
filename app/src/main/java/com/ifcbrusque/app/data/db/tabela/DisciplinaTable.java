package com.ifcbrusque.app.data.db.tabela;

import android.provider.BaseColumns;

public class DisciplinaTable {
    private DisciplinaTable() {
    }

    public static final String TABLE_NAME = "disciplina";
    public static final String FRONT_END_ID = "_id";
    public static final String NOME = "nome";
    public static final String PERIODO = "periodo";

    public static final String CRIAR_TABELA =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    FRONT_END_ID + " TEXT NOT NULL PRIMARY KEY," +
                    NOME + " TEXT NOT NULL," +
                    PERIODO + " TEXT NOT NULL);";

    public static final String DELETAR_TABELA = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
}
