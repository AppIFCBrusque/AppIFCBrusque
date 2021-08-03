package com.ifcbrusque.app.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ifcbrusque.app.models.Noticia;

@Dao
public interface NoticiaDao {
    @Query("SELECT * FROM noticia_table WHERE url = :url")
    Noticia getNoticia(String url);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Noticia noticia);

    @Delete
    void delete(Noticia noticia);

    @Query("DELETE FROM noticia_table")
    public void deleteAll();
}
