package com.ifcbrusque.app.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.adapters.HomeAdapter;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.models.Lembrete;
import java.util.List;
import static android.app.Activity.RESULT_OK;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ADICIONADO;

public class HomeFragment extends Fragment implements HomePresenter.View, View.OnClickListener, HomeAdapter.OnPreviewListener {
    int REQUEST_CODE_LEMBRETE = 100;

    private HomePresenter presenter;

    private FloatingActionButton fabNovoLembrete;

    private RecyclerView recyclerView;
    private HomeAdapter noticiasAdapter;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fabNovoLembrete = root.findViewById(R.id.fabNovoLembrete);

        presenter = new HomePresenter(this, AppDatabase.getDbInstance(getContext().getApplicationContext()));

        fabNovoLembrete.setOnClickListener(this);

        //Configuração do recycler view
        recyclerView = root.findViewById(R.id.rvHome);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        noticiasAdapter = new HomeAdapter(this.getContext(), presenter.getLembretesArmazenados(), this);
        recyclerView.setAdapter(noticiasAdapter);

        return root;
    }

    //Implementar as funções de onClick dos itens neste fragmento
    @Override
    public void onClick(View v) {
        //Clique no botão de inserir lembrete
        if (v == fabNovoLembrete) {
            //Abrir a activity para inserir um lembrete
            Intent intentLembrete = new Intent(getActivity(), InserirLembreteActivity.class);
            startActivityForResult(intentLembrete, REQUEST_CODE_LEMBRETE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Atualizar a recycler view se um preview novo foi adicionado
        if (requestCode == REQUEST_CODE_LEMBRETE) {
            if(resultCode == RESULT_OK) {
                boolean adicionado = data.getBooleanExtra(EXTRAS_LEMBRETE_ADICIONADO, false);
                if(adicionado) {
                    presenter.carregarLembretesArmazenados();
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void atualizarRecyclerView(List<Lembrete> lembretes) {
        noticiasAdapter.setLembretes(lembretes);
        noticiasAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPreviewClick(int position) {
        //TODO
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}

