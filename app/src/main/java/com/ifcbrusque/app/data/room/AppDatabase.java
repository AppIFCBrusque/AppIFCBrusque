package com.ifcbrusque.app.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ifcbrusque.app.data.noticias.classe.Preview;

@Database(entities = {Preview.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PreviewDao previewDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ifcbrusque_db")
                    .build();
        }
        return INSTANCE;
    }
}