package com.ifcbrusque.app.data.db.tabela;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.ifcbrusque.app.data.db.DatabaseHelper;
import com.stacked.sigaa_ifc.Usuario;

public class UsuarioTable {
    private UsuarioTable() {
    }

    public static final String TABLE_NAME = "usuario";
    public static final String MATRICULA = "_id";
    public static final String NOME_COMPLETO = "nome";
    public static final String EMAIL = "email";
    public static final String AVATAR = "avatar";

    public static final String CRIAR_TABELA =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    MATRICULA + " TEXT PRIMARY KEY," +
                    NOME_COMPLETO + " TEXT NOT NULL," +
                    EMAIL + " TEXT NOT NULL," +
                    AVATAR + " BLOB);";

    public static final String DELETAR_TABELA = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";


}
