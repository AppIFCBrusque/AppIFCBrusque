package com.ifcbrusque.app.ui.home.noticias;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.component.ActivityComponent;
import com.ifcbrusque.app.ui.base.BaseFragment;
import com.ifcbrusque.app.ui.noticia.NoticiaActivity;
import com.ifcbrusque.app.data.db.model.Preview;

import java.util.List;

import javax.inject.Inject;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.ifcbrusque.app.ui.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.db.Converters.*;

/*
View dos previews (tela que você é levado ao clicar em "Notícias"), e não ao clicar para abrir em uma notícia
 */
public class NoticiasFragment extends BaseFragment implements NoticiasContract.NoticiasView {
    private Integer mCarregarQuandoFaltar = 5; //Quando estiver este número de notícias abaixo da atual, será carregada a próxima página

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
    protected void setUp(View view) {
        mProgressBar = getActivity().findViewById(R.id.pbHorizontalHome);

        //Configuração do recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView_noticias);
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
        NoticiasAdapter.ItemListener itemListener = position -> {
            Preview preview = mNoticiasAdapter.getPreviews().get(position);

            Intent intent = NoticiaActivity.getStartIntent(getContext(), preview);

            startActivity(intent);
        };
        mNoticiasAdapter.setItemListener(itemListener);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onViewPronta();
    }

    /*
        Executado quando sai do fragmento (manter posição atual)
        */
    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    /*
    Executado quando clica novamente já neste fragmento (voltar ao topo)
     */
    @Override
    public void onDestroyView() {
        int itemNoTopoLista = mLayoutManager.findFirstVisibleItemPosition();
        mPresenter.onDestroyView(itemNoTopoLista);
        super.onDestroyView();

        esconderProgressBar(); //TODO: Utilizar uma progerss bar única do fragmento
    }

    @Override
    public void atualizarRecyclerView(List<Preview> previews) {
        mNoticiasAdapter.setPreviews(previews);
    }

    /**
     * Utilizado para mudar a posição do recycler view
     *
     * @param position posição do preview no topo
     */
    @Override
    public void setRecyclerViewPosition(int position) {
        mLayoutManager.scrollToPosition(position);
    }

    @Override
    public List<Preview> getPreviewsNaView() {
        return mNoticiasAdapter.getPreviews();
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