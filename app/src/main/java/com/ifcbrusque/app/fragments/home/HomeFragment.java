package com.ifcbrusque.app.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

public class HomeFragment extends Fragment implements HomePresenter.View, View.OnClickListener {
    private HomePresenter presenter;

    private FloatingActionButton fabNovoLembrete;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fabNovoLembrete = root.findViewById(R.id.fabNovoLembrete);

        presenter = new HomePresenter(this, AppDatabase.getDbInstance(getContext().getApplicationContext()));

        fabNovoLembrete.setOnClickListener(this);

        //exibirLembretesSalvos();

        return root;
    }

    //Implementar as funções de on click
    @Override
    public void onClick(View v) {
        //Clique no botão de inserir lembrete
        if(v == fabNovoLembrete) {
            //Abrir a activity para inserir um lembrete
            Intent intentLembrete = new Intent(getActivity(), InserirLembreteActivity.class);
            startActivity(intentLembrete);
        }
    }

    void exibirLembretesSalvos() {
        AppDatabase db = AppDatabase.getDbInstance(getContext().getApplicationContext());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Completable.fromRunnable(() -> {
            List<Lembrete> lembretes = db.lembreteDao().getAll();
            for(Lembrete l : lembretes) {
                Log.d(TAG, "exibirLembretesSalvos: " + l.getTitulo() + " " + l.getDescricao() + " " + dateFormat.format(l.getDataLembrete()));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}

