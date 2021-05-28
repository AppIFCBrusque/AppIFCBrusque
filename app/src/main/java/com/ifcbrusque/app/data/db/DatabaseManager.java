package com.ifcbrusque.app.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ifcbrusque.app.data.db.tabela.*;
import com.stacked.sigaa_ifc.*;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    DatabaseHelper dbHelper;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    /////////////////////////////////////////////////////////////////
    public List getUsuario() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                UsuarioTable.MATRICULA,
                UsuarioTable.NOME_COMPLETO,
                UsuarioTable.EMAIL,
                UsuarioTable.AVATAR
        };

        Cursor cursor = db.query(
                UsuarioTable.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToNext();
        List usuario = new ArrayList<>(); //TODO: trocar por um objeto?
        usuario.add(cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioTable.MATRICULA)));
        usuario.add(cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioTable.NOME_COMPLETO)));
        usuario.add(cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioTable.EMAIL)));
        usuario.add(cursor.getLong(cursor.getColumnIndexOrThrow(UsuarioTable.AVATAR)));
        cursor.close();

        return usuario;
    }

    public long inserirUsuario(Usuario u, byte[] imagem) {
        //TODO: eu deveria fazer a mesma função p inserir e atualizar o usuário, pq só vai ter 1 de qualquer maneira

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsuarioTable.MATRICULA, u.getMatricula());
        values.put(UsuarioTable.NOME_COMPLETO, u.getNome());
        values.put(UsuarioTable.EMAIL, u.getEmail());
        values.put(UsuarioTable.AVATAR, imagem);

        return db.insert(UsuarioTable.TABLE_NAME, null, values);
    }
    /////////////////////////////////////////////////////////////////
}
