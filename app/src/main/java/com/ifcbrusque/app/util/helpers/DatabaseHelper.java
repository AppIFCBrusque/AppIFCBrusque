package com.ifcbrusque.app.util.helpers;

import android.content.Context;
import android.util.Log;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;

import java.util.Calendar;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

public class DatabaseHelper {
    private DatabaseHelper() {}

    /**
     * Atualiza a data de notificação para um lembrete com repetição
     * Se a repetição for de hora em hora, atualiza a data de notificação do lembrete para 1 hora depois da primeira notificação
     * Não atualiza para um momento antes ou igual a hora atual. Em vez disso, continua adicionando o intervalo até resultar em um momento posterior ao atual
     * @return observable com o lembrete armazenado (com a data nova)
     */
    public static Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(Context context, long idLembrete) {
        final AppDatabase db = AppDatabase.getDbInstance(context.getApplicationContext());
        final Calendar c = Calendar.getInstance();

        return Observable.defer(() -> {
            Lembrete lembrete = db.lembreteDao().getLembrete(idLembrete);

            c.setTime(lembrete.getDataLembrete());
            //Para evitar spammar um monte de notificação de lembretes com a data antes, apenas prossegue quando a data nova é maior que a atual
            while(c.getTime().compareTo(lembrete.getDataLembrete()) == 0 || c.getTime().before(new Date())) {
                switch(lembrete.getTipoRepeticao()) {
                    case Lembrete.REPETICAO_HORA:
                        c.add(Calendar.HOUR_OF_DAY, 1);
                        break;

                    case Lembrete.REPETICAO_DIA:
                        c.add(Calendar.DAY_OF_MONTH, 1);
                        break;

                    case Lembrete.REPETICAO_SEMANA:
                        c.add(Calendar.DAY_OF_MONTH, 7);
                        break;

                    case Lembrete.REPETICAO_MES:
                        c.add(Calendar.MONTH, 1);
                        break;

                    case Lembrete.REPETICAO_ANO:
                        c.add(Calendar.YEAR, 1);
                        break;
                }
            }
            Log.d(TAG, "atualizarDataLembreteComRepeticao: nova data " + c.get(Calendar.HOUR_OF_DAY) + " " + c.get(Calendar.MONTH) + " " + c.get(Calendar.DAY_OF_MONTH));
            lembrete.setDataLembrete(c.getTime());

            db.lembreteDao().updateLembrete(lembrete);
            return Observable.just(lembrete);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
