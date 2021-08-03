package com.ifcbrusque.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ifcbrusque.app.models.Lembrete;

@Dao
public interface LembreteDao {
    @Query("SELECT * FROM lembrete_table WHERE id = :id")
    Lembrete getLembrete(int id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Lembrete lembrete);

    @Delete
    void delete(Lembrete lembrete);

    @Query("DELETE FROM lembrete_table")
    public void deleteAll();
}
