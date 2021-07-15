package com.ifcbrusque.app.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ifcbrusque.app.models.Preview;

@Database(entities = {Preview.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
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