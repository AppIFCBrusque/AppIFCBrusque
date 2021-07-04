package com.ifcbrusque.app.data.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import com.ifcbrusque.app.data.noticias.classe.Preview;

@Dao
public interface PreviewDao {
    @Query("SELECT * FROM preview_table ORDER BY id ASC")
    List<Preview> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Preview> previews);

    @Delete
    void delete(Preview preview);

    @Query("DELETE FROM preview_table")
    public void deleteAll();
}
