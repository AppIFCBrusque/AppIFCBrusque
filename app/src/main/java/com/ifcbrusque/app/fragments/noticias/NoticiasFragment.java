package com.ifcbrusque.app.fragments.noticias;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.activities.noticia.NoticiaActivity;
import com.ifcbrusque.app.adapters.NoticiasAdapter;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.NotificationsHelper;
import com.ifcbrusque.app.models.PaginaNoticias;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.ifcbrusque.app.models.Preview;

import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.Converters.*;

/*
View dos previews (tela que você é levado ao clicar em "Notícias"), e não ao clicar para abrir em uma notícia
 */
public class NoticiasFragment extends Fragment implements NoticiasPresenter.View, NoticiasAdapter.OnPreviewListener {
    private Integer carregarQuandoFaltar = 5; //Quando estiver este número de notícias abaixo da atual, será carregada a próxima página

    private NoticiasPresenter presenter;

    private RecyclerView recyclerView;
    private NoticiasAdapter noticiasAdapter;
    private LinearLayoutManager layoutManager;

    private MaterialProgressBar pb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pb = getActivity().findViewById(R.id.pbHorizontalHome);

        presenter = new NoticiasPresenter(this, new PreferencesHelper(this.getContext()), AppDatabase.getDbInstance(this.getContext().getApplicationContext()), new PaginaNoticias(this.getContext()));

        //Inflar este fragmento
        View root = inflater.inflate(R.layout.fragment_noticias, container, false);

        //Configuração do recycler view
        recyclerView = root.findViewById(R.id.recyclerView_noticias);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        noticiasAdapter = new NoticiasAdapter(this.getContext(), presenter.getPreviewsArmazenados(), this);
        recyclerView.setAdapter(noticiasAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { //Adicionar listener para solicitar nova página caso se aproxime do final
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= noticiasAdapter.previews.size() - carregarQuandoFaltar && !presenter.isCarregandoPagina() && !presenter.atingiuPaginaFinal()) {
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
    /*
    Utilizado pelo NoticiasAdapter para abrir a activity da notícia
     */
    @Override
    public void onPreviewClick(int position) {
        Preview preview = presenter.getPreviewsArmazenados().get(position);

        Intent intentNoticia = new Intent(getActivity(), NoticiaActivity.class);

        intentNoticia.putExtra(NOTICIA_TITULO, preview.getTitulo());
        intentNoticia.putExtra(NOTICIA_DATA, dateToTimestamp(preview.getDataNoticia()));
        intentNoticia.putExtra(NOTICIA_URL, preview.getUrlNoticia());
        intentNoticia.putExtra(NOTICIA_URL_IMAGEM_PREVIEW, preview.getUrlImagemPreview());

        startActivity(intentNoticia);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void atualizarRecyclerView(List<Preview> previews) {
        noticiasAdapter.previews = previews;
        noticiasAdapter.notifyDataSetChanged();
    }

    @Override
    public void setRecyclerViewPosition(int index) {
        layoutManager.scrollToPosition(index);
    }

    @Override
    public void esconderProgressBar() {
        pb.setVisibility(View.GONE);
    }

    @Override
    public void mostrarProgressBar() {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(this.getContext(), texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void definirSincronizacaoPeriodicaNoticias() {
        NotificationsHelper.definirSincronizacaoPeriodicaNoticias(this.getContext());
    }
}