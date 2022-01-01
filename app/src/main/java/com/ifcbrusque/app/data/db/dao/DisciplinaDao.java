package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ifcbrusque.app.data.db.model.DisciplinaArmazenavel;
import com.ifcbrusque.app.data.db.model.Preview;

import java.util.List;

@Dao
public interface DisciplinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(DisciplinaArmazenavel disciplina);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<DisciplinaArmazenavel> disciplinas);

    @Delete
    void delete(DisciplinaArmazenavel disciplina);

    @Query("DELETE FROM disciplina_table")
    void deleteAll();

    @Query("SELECT * FROM disciplina_table WHERE front_end_id_turma = :frontEndIdTurma")
    List<DisciplinaArmazenavel> getDisciplinas(String frontEndIdTurma);

    @Query("SELECT * FROM disciplina_table")
    List<DisciplinaArmazenavel> getAll();

    @Update
    void atualizarDisciplina(DisciplinaArmazenavel disciplina);
}
