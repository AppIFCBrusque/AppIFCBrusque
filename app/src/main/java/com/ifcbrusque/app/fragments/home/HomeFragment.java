package com.ifcbrusque.app.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;

public class HomeFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton inserir = root.findViewById(R.id.fabCompleto);
        inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Acho melhor fazer a inserção de lembretes em outra activity
                Intent intentLembrete = new Intent(getActivity(), InserirLembreteActivity.class);
                startActivity(intentLembrete);

                /*
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment,new InsereTarefas())
                        .commit();*/
            }
        });


        return root;
    }
}

