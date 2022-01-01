package com.ifcbrusque.app.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ifcbrusque.app.data.db.dao.AvaliacaoDao;
import com.ifcbrusque.app.data.db.dao.DisciplinaDao;
import com.ifcbrusque.app.data.db.dao.LembreteDao;
import com.ifcbrusque.app.data.db.dao.NoticiaDao;
import com.ifcbrusque.app.data.db.dao.PreviewDao;
import com.ifcbrusque.app.data.db.dao.TarefaDao;
import com.ifcbrusque.app.data.db.model.AvaliacaoArmazenavel;
import com.ifcbrusque.app.data.db.model.DisciplinaArmazenavel;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;

import static com.ifcbrusque.app.utils.AppConstants.DB_NAME;
import static com.ifcbrusque.app.utils.AppConstants.DB_VERSION;

@Database(entities = {Preview.class, Noticia.class, Lembrete.class, AvaliacaoArmazenavel.class, DisciplinaArmazenavel.class, TarefaArmazenavel.class}, version = DB_VERSION, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PreviewDao previewDao();

    public abstract NoticiaDao noticiaDao();

    public abstract LembreteDao lembreteDao();

    public abstract AvaliacaoDao avaliacaoDao();

    public abstract DisciplinaDao disciplinaDao();

    public abstract TarefaDao tarefaDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .build();
        }
        return INSTANCE;
    }
}