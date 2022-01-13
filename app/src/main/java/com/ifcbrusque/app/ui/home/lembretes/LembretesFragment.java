package com.ifcbrusque.app.ui.home.lembretes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.component.ActivityComponent;
import com.ifcbrusque.app.ui.base.BaseFragment;
import com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity;
import com.ifcbrusque.app.data.db.model.Lembrete;

import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;
import static com.ifcbrusque.app.ui.lembrete.InserirLembreteActivity.EXTRAS_ATUALIZAR_RECYCLER_VIEW;

public class LembretesFragment extends BaseFragment implements LembretesContract.LembretesView {
    final int REQUEST_CODE_LEMBRETE = 100;

    @Inject
    LembretesContract.LembretesPresenter<LembretesContract.LembretesView> mPresenter;

    private FloatingActionButton mFabNovoLembrete;
    private Button mBtCategorias;
    private RecyclerView mRecyclerView;
    private BottomSheetDialog mBottomSheetDialog;

    @Inject
    LembretesAdapter mHomeAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.onAttach(this);
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.onDetach();
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
            if (resultCode == RESULT_OK) {
                boolean adicionado = data.getBooleanExtra(EXTRAS_ATUALIZAR_RECYCLER_VIEW, false);
                if (adicionado) {
                    mPresenter.onLembreteInserido();
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas para serem definidas por esta view
     */

    @Override
    public List<Lembrete> getLembretesNaView() {
        return mHomeAdapter.getLembretes();
    }

    @Override
    public void setLembretesNaView(List<Lembrete> lembretes) {
        mHomeAdapter.setLembretes(lembretes);
        mPresenter.onLembretesAtualizados();
    }

    @Override
    public List<Object> getDadosNaView() {
        return mHomeAdapter.getDados();
    }

    @Override
    public void setDadosNaView(List<Object> dados) {
        mHomeAdapter.setDados(dados);
    }

    /**
     * Define a categoria de lembretes a ser exibida pelo recycler view
     * Atualiza o recycler view e o texto do botão
     * 0 a 2: categorias padrão
     *
     * @param categoria
     */
    public void atualizarCategoriaRecyclerView(int categoria) {
        //Texto do botão
        switch (categoria) {
            case 0:
                mBtCategorias.setText(getResources().getString(R.string.categoria_lembretes_todos));
                break;

            case 1:
                mBtCategorias.setText(getResources().getString(R.string.categoria_lembretes_incompletos));
                break;

            case 2:
                mBtCategorias.setText(getResources().getString(R.string.categoria_lembretes_completos));
                break;

            default:
                //TODO: Categorias personalizadas
                break;
        }

        //Atualizar o recycler view e a última categoria acessada
        if (mHomeAdapter != null) {
            mHomeAdapter.setCategoria(categoria);
            mPresenter.onCategoriaAlterada(categoria);
        }
    }

    @Override
    protected void setUp(View view) {
        mFabNovoLembrete = view.findViewById(R.id.fabNovoLembrete);
        mBtCategorias = view.findViewById(R.id.btCategorias);

        mFabNovoLembrete.setOnClickListener(v -> {
            //Abrir activity para dicionar um lembrete
            Intent intentLembrete = new Intent(getActivity(), InserirLembreteActivity.class);
            startActivityForResult(intentLembrete, REQUEST_CODE_LEMBRETE);
        });
        mBtCategorias.setOnClickListener(v -> {
            //Exibir bottom sheet dialog
            if (mBottomSheetDialog != null) {
                mBottomSheetDialog.dismiss();
            } else {
                mBottomSheetDialog = new BottomSheetDialog(getContext());
            }
            mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_categorias_lembrete);

            TextView tvIncompletos = mBottomSheetDialog.findViewById(R.id.tvIncompletos);
            TextView tvCompletos = mBottomSheetDialog.findViewById(R.id.tvCompletos);
            TextView tvTodos = mBottomSheetDialog.findViewById(R.id.tvTodos);

            View.OnClickListener onClickListener = v1 -> {
                if (v1 == tvIncompletos) {
                    atualizarCategoriaRecyclerView(Lembrete.ESTADO_INCOMPLETO);
                } else if (v1 == tvCompletos) {
                    atualizarCategoriaRecyclerView(Lembrete.ESTADO_COMPLETO);
                } else if (v1 == tvTodos) {
                    atualizarCategoriaRecyclerView(0);
                }
                mBottomSheetDialog.dismiss();
            };
            tvIncompletos.setOnClickListener(onClickListener);
            tvCompletos.setOnClickListener(onClickListener);
            tvTodos.setOnClickListener(onClickListener);

            mBottomSheetDialog.show();
        });

        //Configuração do recycler view
        mRecyclerView = view.findViewById(R.id.rvHome);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setAdapter(mHomeAdapter);
        mRecyclerView.addItemDecoration(new StickyHeaderDecoration(mHomeAdapter));

        LembretesAdapter.ItemListener itemListener = new LembretesAdapter.ItemListener() {
            @Override
            public void onLembreteClick(int position) {
                //Abrir uma activity para adicionar um lembrete
                Lembrete lembrete = (Lembrete) getDadosNaView().get(position);

                Intent intent = InserirLembreteActivity.getStartIntent(getContext(), lembrete);

                startActivityForResult(intent, REQUEST_CODE_LEMBRETE);
            }

            @Override
            public void onOpcoesClick(int position) {
                //Bottom sheet dialog com as opções do lembrete
                if (mBottomSheetDialog != null) {
                    mBottomSheetDialog.dismiss();
                } else {
                    mBottomSheetDialog = new BottomSheetDialog(getContext());
                }

                mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_opcoes_lembrete);
                TextView tvAlternarEstado = mBottomSheetDialog.findViewById(R.id.tvAlterarEstado);
                TextView tvExcluir = mBottomSheetDialog.findViewById(R.id.tvExcluir);

                //Ajustar texto e ícones
                switch (((Lembrete) getDadosNaView().get(position)).getEstado()) {
                    case Lembrete.ESTADO_INCOMPLETO:
                        tvAlternarEstado.setText(R.string.lembrete_completar);
                        tvAlternarEstado.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.outline_done_black_24, 0, 0, 0);
                        break;

                    case Lembrete.ESTADO_COMPLETO:
                        tvAlternarEstado.setText(R.string.lembrete_descompletar);
                        tvAlternarEstado.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.outline_clear_black_24, 0, 0, 0);
                        break;
                }

                View.OnClickListener onClickListener = v -> {
                    if (v == tvAlternarEstado) {
                        mPresenter.onAlternarEstadoLembreteClick(position);
                    } else if (v == tvExcluir) {
                        mPresenter.onExcluirLembreteClick(position);
                    }
                    mBottomSheetDialog.dismiss();
                };
                tvAlternarEstado.setOnClickListener(onClickListener);
                tvExcluir.setOnClickListener(onClickListener);

                mBottomSheetDialog.show();
            }
        };
        mHomeAdapter.setItemListener(itemListener);

        mPresenter.onViewPronta();
    }
}

