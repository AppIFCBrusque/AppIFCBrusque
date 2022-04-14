package com.ifcbrusque.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ifcbrusque.app.data.db.model.NoticiaArmazenavel;

import java.util.List;

@Dao
public interface NoticiaSIGAADao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<NoticiaArmazenavel> noticiasArmazenaveis);

    @Query("DELETE FROM noticia_sigaa_table")
    void deleteAll();

    @Query("SELECT * FROM noticia_sigaa_table")
    List<NoticiaArmazenavel> getAll();
}
