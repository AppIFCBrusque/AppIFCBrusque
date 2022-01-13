package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ifcbrusque.app.data.db.model.Noticia;

@Dao
public interface NoticiaDao {
    /*
    Funções para utilizar as notícias no banco de dados
     */
    @Query("SELECT * FROM noticia_table WHERE url = :url")
    Noticia getNoticia(String url);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Noticia noticia);

    @Delete
    void delete(Noticia noticia);

    @Query("DELETE FROM noticia_table")
    void deleteAll();
}
