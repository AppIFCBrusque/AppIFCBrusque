package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ifcbrusque.app.data.db.model.AvaliacaoArmazenavel;
import com.ifcbrusque.app.data.db.model.DisciplinaArmazenavel;
import com.ifcbrusque.app.data.db.model.TarefaArmazenavel;

import java.util.List;

@Dao
public interface AvaliacaoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(AvaliacaoArmazenavel avaliacao);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<AvaliacaoArmazenavel> avaliacoes);

    @Delete
    void delete(AvaliacaoArmazenavel avaliacao);

    @Query("DELETE FROM avaliacao_table")
    void deleteAll();

    @Query("SELECT * FROM avaliacao_table WHERE disciplina_front_end_id_turma = :frontEndIdTurma")
    List<AvaliacaoArmazenavel> getAvaliacoes(String frontEndIdTurma);

    @Query("SELECT * FROM avaliacao_table")
    List<AvaliacaoArmazenavel> getAll();

    @Update
    void atualizarDisciplina(AvaliacaoArmazenavel avaliacao);
}
