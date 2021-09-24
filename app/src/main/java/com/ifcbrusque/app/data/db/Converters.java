package com.ifcbrusque.app.data.db;

import androidx.room.TypeConverter;
import java.util.Date;

public class Converters {
    /*
    Funções utilizadas pelo banco de dados para converter tipos de dados ao armazenar
     */
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
