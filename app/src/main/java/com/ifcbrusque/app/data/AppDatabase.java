package com.ifcbrusque.app.data;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

@Database(entities = {Preview.class, Noticia.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract PreviewDao previewDao();

    public abstract NoticiaDao noticiaDao();

    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "ifcbrusque_db")
                    .build();
        }
        return INSTANCE;
    }

    public Observable<List<Preview>> armazenarPreviewsNovos(List<Preview> previews, boolean retornarPreviewsNovos) {
        return Observable.defer(() -> {
            List<Preview> previewsNoArmazenamento = previewDao().getAll(); //Atualizar para o mais recente

            List<Preview> previewsNovos = new ArrayList<>();
            if (previews.size() > 0) {
                for (Preview p : previews) { //Encontrar os previews não armazenados
                    if (!previewsNoArmazenamento.stream().filter(_p -> _p.getUrlNoticia().equals(p.getUrlNoticia())).findFirst().isPresent()) {
                        previewsNovos.add(p);
                    }
                }

                if (previewsNovos.size() > 0) {
                    previewDao().insertAll(previewsNovos);
                    previewsNoArmazenamento = previewDao().getAll();
                }
            }

            if(retornarPreviewsNovos) return Observable.just(previewsNovos); else return Observable.just(previewsNoArmazenamento);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}