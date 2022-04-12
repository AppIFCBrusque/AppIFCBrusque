package com.ifcbrusque.app.ui.home.noticias;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.component.ActivityComponent;
import com.ifcbrusque.app.ui.base.BaseFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;
import com.ifcbrusque.app.ui.noticia.NoticiaActivity;
import com.ifcbrusque.app.data.db.model.Preview;

import java.util.List;

import javax.inject.Inject;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/*
View dos previews (tela que você é levado ao clicar em "Notícias"), e não ao clicar para abrir em uma notícia
 */
public class NoticiasFragment extends BaseFragment implements NoticiasContract.NoticiasView {
    private final Integer mCarregarQuandoFaltar = 5; //Quando estiver este número de notícias abaixo da atual, será carregada a próxima página

    @Inject
    NoticiasContract.NoticiasPresenter<NoticiasContract.NoticiasView> mPresenter;

    private MaterialProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    @Inject
    NoticiasAdapter mNoticiasAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_noticias, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.onAttach(this);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setElevation(4 * getResources().getDisplayMetrics().density);
        actionBar.setTitle(R.string.title_noticias);
        ImageButton ibFiltros = getBaseActivity().findViewById(R.id.image_button_filtros);
        ibFiltros.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {
        mProgressBar = getBaseActivity().findViewById(R.id.pbHorizontalHome);

        //Configuração do recycler view
        mRecyclerView = getView().findViewById(R.id.recyclerView_noticias);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setAdapter(mNoticiasAdapter);
        //Solicitar nova página caso se aproxime do final
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mLayoutManager.findLastCompletelyVisibleItemPosition() >= mNoticiasAdapter.getPreviews().size() - mCarregarQuandoFaltar) {
                    mPresenter.onFimRecyclerView();
                }
            }
        });
        //Clique em um preview
        NoticiaItemListener itemListener = position -> {
            Preview preview = mNoticiasAdapter.getPreviews().get(position);

            Intent intent = NoticiaActivity.getStartIntent(getContext(), preview);

            startActivity(intent);
        };
        mNoticiasAdapter.setItemListener(itemListener);

        mPresenter.onViewPronta();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mProgressBar != null) {
            esconderProgressBar();
        }
    }

    @Override
    public void atualizarRecyclerView(List<Preview> previews) {
        mNoticiasAdapter.setPreviews(previews);
    }

    @Override
    public void mostrarProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void esconderProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }
}