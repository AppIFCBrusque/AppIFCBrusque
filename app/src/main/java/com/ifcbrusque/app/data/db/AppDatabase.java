package com.ifcbrusque.app.data.db;

import static com.ifcbrusque.app.utils.AppConstants.DB_NAME;
import static com.ifcbrusque.app.utils.AppConstants.DB_VERSION;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ifcbrusque.app.data.db.dao.AvaliacaoDao;
import com.ifcbrusque.app.data.db.dao.DisciplinaDao;
import com.ifcbrusque.app.data.db.dao.LembreteDao;
import com.ifcbrusque.app.data.db.dao.NoticiaDao;
import com.ifcbrusque.app.data.db.dao.NoticiaSIGAADao;
import com.ifcbrusque.app.data.db.dao.PreviewDao;
import com.ifcbrusque.app.data.db.dao.QuestionarioDao;
import com.ifcbrusque.app.data.db.dao.TarefaDao;
import com.ifcbrusque.app.data.db.model.AvaliacaoArmazenavel;
import com.ifcbrusque.app.data.db.model.DisciplinaArmazenavel;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.NoticiaArmazenavel;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.data.db.model.QuestionarioArmazenavel;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;

@Database(entities = {Lembrete.class, Preview.class, Noticia.class, DisciplinaArmazenavel.class, AvaliacaoArmazenavel.class, NoticiaArmazenavel.class, TarefaArmazenavel.class, QuestionarioArmazenavel.class}, version = DB_VERSION, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME)
                    .build();
        }
        return INSTANCE;
    }

    public abstract LembreteDao lembreteDao();

    public abstract PreviewDao previewDao();

    public abstract NoticiaDao noticiaDao();

    public abstract DisciplinaDao disciplinaDao();

    public abstract AvaliacaoDao avaliacaoDao();

    public abstract NoticiaSIGAADao noticiaSIGAADao();

    public abstract TarefaDao tarefaDao();

    public abstract QuestionarioDao questionarioDao();
}