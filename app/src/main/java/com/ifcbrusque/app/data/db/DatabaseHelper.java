package com.ifcbrusque.app.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ifcbrusque.app.data.db.tabela.*;
import com.stacked.sigaa_ifc.Usuario;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Database.db";

    ///////////////////////////////////////////////////////
    //TODO: anexo, aula, participante

    private static final String SQL_CREATE_ENTRIES =
            UsuarioTable.CRIAR_TABELA +
                    DisciplinaTable.CRIAR_TABELA +
                    AvaliacaoTable.CRIAR_TABELA +
                    TarefaTable.CRIAR_TABELA +
                    NotaTable.CRIAR_TABELA;


    private static final String SQL_DELETE_ENTRIES =
            UsuarioTable.DELETAR_TABELA +
                    DisciplinaTable.DELETAR_TABELA +
                    AvaliacaoTable.DELETAR_TABELA +
                    TarefaTable.DELETAR_TABELA +
                    NotaTable.DELETAR_TABELA;
    ///////////////////////////////////////////////////////

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
