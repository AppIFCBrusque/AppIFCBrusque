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
import com.ifcbrusque.app.util.helpers.DatabaseHelper;
import com.ifcbrusque.app.util.helpers.NotificationHelper;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Observable;

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
    private HomeAdapter homeAdapter;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Iniciar variáveis
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        fabNovoLembrete = root.findViewById(R.id.fabNovoLembrete);
        btCategorias = root.findViewById(R.id.btCategorias);

        presenter = new HomePresenter(this, AppDatabase.getDbInstance(getContext().getApplicationContext()), new PreferencesHelper(getContext()));

        fabNovoLembrete.setOnClickListener(this);
        btCategorias.setOnClickListener(this);

        //Configuração do recycler view
        recyclerView = root.findViewById(R.id.rvHome);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        //recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL)); //TODO: Arrumar isto aqui. O problema está descrito na parte da interface.
        int categoria = presenter.getUltimaCategoriaAcessadaHome();
        definirCategoria(categoria); //Para atualizar o texto do botão. Como o adapter ainda não existe, ele não vai alterar a recycler view ou o valor do última categoria acessada
        homeAdapter = new HomeAdapter(this.getContext(), presenter.getLembretesArmazenados(), categoria, this);
        recyclerView.setAdapter(homeAdapter);

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
            //Definir o que acontece quando é clicado em alguma das categorias
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v == tvIncompletos) {
                        definirCategoria(Lembrete.ESTADO_INCOMPLETO);
                    } else if(v == tvCompletos) {
                        definirCategoria(Lembrete.ESTADO_COMPLETO);
                    } else if(v == tvTodos) {
                        definirCategoria(0);
                    }
                    bottomSheetDialog.dismiss();
                }
            };
            tvIncompletos.setOnClickListener(onClickListener);
            tvCompletos.setOnClickListener(onClickListener);
            tvTodos.setOnClickListener(onClickListener);

            bottomSheetDialog.show();
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

    /**
     * Define a categoria de lembretes a ser exibida pelo recycler view
     * Atualiza o recycler view e o texto do botão
     * 0 a 2: categorias padrão
     * @param categoria
     */
    private void definirCategoria(int categoria) {
        //Texto do botão
        switch(categoria) {
            case 0:
                btCategorias.setText(getResources().getString(R.string.categoria_lembretes_todos));
                break;

            case 1:
                btCategorias.setText(getResources().getString(R.string.categoria_lembretes_incompletos));
                break;

            case 2:
                btCategorias.setText(getResources().getString(R.string.categoria_lembretes_completos));
                break;

            default:
                //TODO: Categorias personalizadas
                break;
        }
        //Atualizar o recycler view e a última categoria acessada
        if(homeAdapter != null) {
            homeAdapter.setCategoria(categoria);
            presenter.setUltimaCategoriaAcessadaHome(categoria);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas no presenter ou no adapter para serem definidas por esta view
     */

    /**
     * Utilizado para mudar os itens da recycler view. Atualiza todos os itens de uma vez (mais pesado)
     * Exibe somente os lembretes incompletos
     * Define os lembretes do adapter e o notifica para atualizar
     * @param lembretes lembretes para serem exibidos pela recycler view
     * @param agendarNotificacoes indica se vai agendar as notificações dos lembretes incompletos
     */
    @Override
    public void atualizarRecyclerView(List<Lembrete> lembretes, boolean agendarNotificacoes) {
        homeAdapter.setLembretes(lembretes);

        //Agendar as notificações futuras e incompletas
        if(agendarNotificacoes) {
            List<Lembrete> lembretesFuturosIncompletos = lembretes.stream().filter(l  -> l.getEstado() == Lembrete.ESTADO_INCOMPLETO && new Date().before(l.getDataLembrete())).collect(Collectors.toList());
            for(Lembrete l : lembretesFuturosIncompletos) {
                NotificationHelper.agendarNotificacaoLembrete(getContext(), l);
            }
        }
    }

    /**
     * Utilizado para mudar os itens da recycler view. Atualiza um só item de uma vez (mais leve)
     * @param lembretes lista com todos os lembretes
     * @param position posição do item atualizado/removido
     * @param agendarNotificacao boolean indicando se a notificação para o item alterado vai ser agendada
     * @param removido indica se o item foi removido
     */
    @Override
    public void atualizarRecyclerView(List<Lembrete> lembretes, int position, boolean agendarNotificacao, boolean removido) {
        homeAdapter.setLembretes(lembretes, position, removido);

        //Agendar notificação do lembrete em questão
        if(!removido && agendarNotificacao && lembretes.get(position).getEstado() == Lembrete.ESTADO_INCOMPLETO && new Date().before(lembretes.get(position).getDataLembrete())) {
            NotificationHelper.agendarNotificacaoLembrete(getContext(), lembretes.get(position));
        }
    }

    /**
     * Agenda a notificação de um lembrete
     */
    @Override
    public void agendarNotificacaoLembrete(Lembrete lembrete) {
        NotificationHelper.agendarNotificacaoLembrete(getContext(), lembrete);
    }

    /**
     * Desagenda e exclui a notificação de um lembrete
     */
    @Override
    public void desagendarNotificacaoLembrete(Lembrete lembrete) {
        NotificationHelper.desagendarNotificacaoLembrete(getContext(), lembrete);
    }

    /**
     * Pula um lembrete com repetição para a próxima data
     * @return observable com o lembrete atualizado
     */
    @Override
    public Observable<Lembrete> atualizarParaProximaDataLembreteComRepeticao(long idLembrete) {
        return DatabaseHelper.atualizarParaProximaDataLembreteComRepeticao(getContext().getApplicationContext(), idLembrete);
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

    /**
     * Executado ao clicar em "marcar como completo/incompleto" das opções de um lembrete
     */
    @Override
    public void onAlternarEstadoClick(int position) {
        presenter.alternarEstadoLembrete(position);
    }

    /**
     * Executado ao clicar em "excluir" das opções de um lembrete
     */
    @Override
    public void onExcluirClick(int position) {
        presenter.excluirLembrete(position);
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

