package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;

import java.util.List;

@Dao
public interface TarefaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TarefaArmazenavel tarefa);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<TarefaArmazenavel> tarefas);

    @Delete
    void delete(TarefaArmazenavel tarefa);

    @Query("DELETE FROM tarefa_table")
    void deleteAll();

    @Query("SELECT * FROM tarefa_table WHERE disciplina_front_end_id_turma = :frontEndIdTurma")
    List<TarefaArmazenavel> getTarefas(String frontEndIdTurma);

    @Query("SELECT * FROM tarefa_table")
    List<TarefaArmazenavel> getAll();

    @Update
    int atualizarTarefa(TarefaArmazenavel tarefa);
}
