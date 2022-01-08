package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ifcbrusque.app.data.db.model.AvaliacaoArmazenavel;
import com.ifcbrusque.app.data.db.model.QuestionarioArmazenavel;

import java.util.List;

@Dao
public interface QuestionarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(QuestionarioArmazenavel questionario);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<QuestionarioArmazenavel> questionarios);

    @Delete
    void delete(QuestionarioArmazenavel questionario);

    @Query("DELETE FROM questionario_table")
    void deleteAll();

    @Query("SELECT * FROM questionario_table WHERE disciplina_front_end_id_turma = :frontEndIdTurma")
    List<QuestionarioArmazenavel> getQuestionarios(String frontEndIdTurma);

    @Query("SELECT * FROM questionario_table")
    List<QuestionarioArmazenavel> getAll();

    @Update
    int atualizarQuestionario(QuestionarioArmazenavel questionario);
}
