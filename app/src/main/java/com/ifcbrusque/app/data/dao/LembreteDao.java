package com.ifcbrusque.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ifcbrusque.app.models.Lembrete;
import java.util.List;

@Dao
public interface LembreteDao {
    /*
    Funções para utilizar os lembretes no banco de dados
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Lembrete lembrete);

    @Delete
    void delete(Lembrete lembrete);

    @Query("SELECT * FROM lembrete_table WHERE id = :id")
    Lembrete getLembrete(int id);

    @Update
    void updateLembrete(Lembrete lembrete);

    @Query("SELECT * FROM lembrete_table ORDER BY data_lembrete ASC")
    List<Lembrete> getAll();

    @Query("DELETE FROM lembrete_table")
    void deleteAll();
}
