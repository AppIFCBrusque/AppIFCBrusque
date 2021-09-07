package com.ifcbrusque.app.fragments.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.adapters.HomeAdapter;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.util.helpers.NotificationHelper;
import com.ifcbrusque.app.models.Lembrete;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static android.app.Activity.RESULT_OK;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ADICIONADO;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID;
import static com.ifcbrusque.app.activities.lembrete.InserirLembreteActivity.EXTRAS_LEMBRETE_ID_NOTIFICACAO;

public class HomeFragment extends Fragment implements HomePresenter.View, View.OnClickListener, HomeAdapter.OnLembreteListener {
    int REQUEST_CODE_LEMBRETE = 100;

    private HomePresenter presenter;

    private FloatingActionButton fabNovoLembrete;
    private Button btCategorias;

    private RecyclerView recyclerView;
    private HomeAdapter noticiasAdapter;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Iniciar variáveis
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fabNovoLembrete = root.findViewById(R.id.fabNovoLembrete);
        btCategorias = root.findViewById(R.id.btCategorias);

        presenter = new HomePresenter(this, AppDatabase.getDbInstance(getContext().getApplicationContext()));

        fabNovoLembrete.setOnClickListener(this);
        btCategorias.setOnClickListener(this);

        //Configuração do recycler view
        recyclerView = root.findViewById(R.id.rvHome);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL)); //TODO: Arrumar isto aqui. O problema está descrito na parte da interface.
        noticiasAdapter = new HomeAdapter(this.getContext(), presenter.getLembretesArmazenados(), this);
        recyclerView.setAdapter(noticiasAdapter);

        return root;
    }

    /*
    Implementar as funções de on click para os listeners que são definidos como this (como em fabNovoLembrete.setOnClickListener(this))
     */
    @Override
    public void onClick(View v) {
        //Clique no botão de inserir lembrete
        if (v == fabNovoLembrete) {
            //Abrir a activity para inserir um lembrete
            Intent intentLembrete = new Intent(getActivity(), InserirLembreteActivity.class);
            startActivityForResult(intentLembrete, REQUEST_CODE_LEMBRETE);
        } else if(v == btCategorias) {
            //Clique nas categorias (mostrar diálogo para escolher a categoria)
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_categorias_lembrete);

            TextView tvIncompletos = bottomSheetDialog.findViewById(R.id.tvIncompletos);
            TextView tvCompletos = bottomSheetDialog.findViewById(R.id.tvCompletos);
            TextView tvTodos = bottomSheetDialog.findViewById(R.id.tvTodos);

            bottomSheetDialog.show();

            //Definir o que acontece quando é clicado em alguma das categorias
            tvIncompletos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticiasAdapter.setCategoria(Lembrete.ESTADO_INCOMPLETO);
                    btCategorias.setText(getResources().getString(R.string.categoria_lembretes_incompletos));
                    bottomSheetDialog.dismiss();
                }
            });
            tvCompletos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticiasAdapter.setCategoria(Lembrete.ESTADO_COMPLETO);
                    btCategorias.setText(getResources().getString(R.string.categoria_lembretes_completos));
                    bottomSheetDialog.dismiss();
                }
            });
            tvTodos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticiasAdapter.setCategoria(0);
                    btCategorias.setText(getResources().getString(R.string.categoria_lembretes_todos));
                    bottomSheetDialog.dismiss();
                }
            });
        }
    }

    /*
    Executado quando uma activity aberta através do startActivityForResult é fechada

    Identifica o item e realiza os procedimentos correspondentes
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Atualizar a recycler view se um preview novo foi adicionado
        if (requestCode == REQUEST_CODE_LEMBRETE) {
            if(resultCode == RESULT_OK) {
                boolean adicionado = data.getBooleanExtra(EXTRAS_LEMBRETE_ADICIONADO, false);
                if(adicionado) {
                    presenter.carregarLembretesArmazenados(false);
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas no presenter para serem definidas por esta view
     */

    /**
     * Utilizado para mudar os itens da recycler view
     * Exibe somente os lembretes incompletos
     * Define os lembretes do adapter e o notifica para atualizar
     * @param lembretes lembretes para serem exibidos pela recycler view
     * @param agendarNotificacoes indica se vai agendar as notificações dos lembretes incompletos
     */
    @Override
    public void atualizarRecyclerView(List<Lembrete> lembretes, boolean agendarNotificacoes) {
        noticiasAdapter.setLembretes(lembretes);

        //Agendar as notificações futuras e incompletas
        if(agendarNotificacoes) {
            List<Lembrete> lembretesFuturosIncompletos = lembretes.stream().filter(l  -> l.getEstado() == Lembrete.ESTADO_INCOMPLETO && new Date().before(l.getDataLembrete())).collect(Collectors.toList());
            for(Lembrete l : lembretesFuturosIncompletos) {
                NotificationHelper.agendarNotificacaoLembrete(getContext(), l);
            }
        }
    }

    /**
     * Método executado quando é clicado em um dos lembretes
     * Abre um InserirLembreteActivity para editar o lembrete selecionado
     *
     * O lembrete é identificado pelo id colocado no bundle
     */
    @Override
    public void onLembreteClick(int position) {
        Intent intentLembrete = new Intent(getActivity(), InserirLembreteActivity.class);
        intentLembrete.putExtra(EXTRAS_LEMBRETE_ID, presenter.getLembretesArmazenados().get(position).getId());
        intentLembrete.putExtra(EXTRAS_LEMBRETE_ID_NOTIFICACAO, presenter.getLembretesArmazenados().get(position).getIdNotificacao());
        startActivityForResult(intentLembrete, REQUEST_CODE_LEMBRETE);
    }

    @Override
    public void onCompletarClick(int position) {
        presenter.completarLembrete(presenter.getLembretesArmazenados().get(position));
    }

    @Override
    public void onExcluirClick(int position) {
        presenter.excluirLembrete(presenter.getLembretesArmazenados().get(position));
    }

    /**
     * Utilizado para exibir um texto na tela através do toast
     * @param texto texto a ser exibido no toast
     */
    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}

