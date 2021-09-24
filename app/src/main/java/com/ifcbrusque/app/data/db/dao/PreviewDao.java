package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

import com.ifcbrusque.app.data.db.model.Preview;

@Dao
public interface PreviewDao {
    /*
    Funções para utilizar os previews no banco de dados
     */
    @Query("SELECT * FROM preview_table ORDER BY data_noticia DESC")
    List<Preview> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Preview> previews);

    @Delete
    void delete(Preview preview);

    @Query("DELETE FROM preview_table")
    public void deleteAll();
}
