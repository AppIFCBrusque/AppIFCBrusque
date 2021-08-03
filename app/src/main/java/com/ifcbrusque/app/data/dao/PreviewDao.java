package com.ifcbrusque.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import com.ifcbrusque.app.models.Preview;

@Dao
public interface PreviewDao {
    @Query("SELECT * FROM preview_table ORDER BY data_noticia DESC")
    List<Preview> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Preview> previews);

    @Delete
    void delete(Preview preview);

    @Query("DELETE FROM preview_table")
    public void deleteAll();
}
