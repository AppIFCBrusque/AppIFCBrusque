package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ifcbrusque.app.data.db.model.Lembrete;
import java.util.List;

@Dao
public interface LembreteDao {
    /*
    Funções para utilizar os lembretes no banco de dados
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Lembrete lembrete);

    @Delete
    void delete(Lembrete lembrete);

    @Query("DELETE FROM lembrete_table")
    void deleteAll();

    @Query("SELECT * FROM lembrete_table WHERE id = :id")
    Lembrete getLembrete(long id);

    @Query("SELECT * FROM lembrete_table ORDER BY data_lembrete ASC")
    List<Lembrete> getAll();

    @Update
    void atualizarLembrete(Lembrete lembrete);

    @Query("UPDATE lembrete_table SET estado = :estado WHERE id = :id")
    void alterarEstadoLembrete(long id, int estado);
}
