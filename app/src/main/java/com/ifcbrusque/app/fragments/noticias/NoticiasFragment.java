package com.ifcbrusque.app.fragments.noticias;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.adapters.NoticiasAdapter;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.image.ImageManager;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.ifcbrusque.app.models.Preview;

import java.util.List;

/*
View dos previews (tela que você é levado ao clicar em "Notícias"), e não ao clicar para abrir em uma notícia
 */
public class NoticiasFragment extends Fragment implements NoticiasPresenter.View {
    private Integer carregarQuandoFaltar = 5; //Quando estiver este número de notícias abaixo da atual, será carregada a próxima página

    private NoticiasPresenter presenter;

    private RecyclerView recyclerView;
    private NoticiasAdapter noticiasAdapter;
    private LinearLayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        presenter = new NoticiasPresenter(this, new ImageManager(this.getContext()), new PreferencesHelper(this.getContext()), AppDatabase.getDbInstance(this.getContext().getApplicationContext()));

        //Inflar este fragmento
        View root = inflater.inflate(R.layout.fragment_noticias, container, false);

        //Configuração do recycler view
        recyclerView = root.findViewById(R.id.recyclerView_noticias);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        noticiasAdapter = new NoticiasAdapter(this.getContext(), presenter.getPreviewsArmazenados());
        recyclerView.setAdapter(noticiasAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { //Adicionar listener para solicitar nova página caso se aproxime do final
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(layoutManager.findLastCompletelyVisibleItemPosition() >= noticiasAdapter.previews.size() - carregarQuandoFaltar && !presenter.isCarregandoPagina()){
                    presenter.getProximaPaginaNoticias();
                }
            }
        });
        return root;
    }

    /*
    Quando sai do fragmento (manter posição atual)
     */
    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    /*
    Quando clica novamente já neste fragmento (voltar ao topo)
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroyView(layoutManager.findFirstVisibleItemPosition());

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void atualizarRecyclerView(List<Preview> previews) {
        noticiasAdapter.previews = previews;
        noticiasAdapter.notifyItemInserted(previews.size() - 1);
    }

    @Override
    public void atualizarImagemRecyclerView(int index) {
        if (noticiasAdapter.previews.size() > index) noticiasAdapter.notifyItemChanged(index); //Sem esse if, ele pode tentar atualizar um item que ainda não foi inserido na recycler view, crashando o aplicativo
    }

    @Override
    public void setRecyclerViewPosition(int index) {
        layoutManager.scrollToPosition(index);
    }
}