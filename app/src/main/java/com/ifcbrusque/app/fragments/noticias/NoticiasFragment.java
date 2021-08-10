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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= noticiasAdapter.getPreviews().size() - carregarQuandoFaltar && !presenter.isCarregandoPagina() && !presenter.atingiuPaginaFinal()) {
                    presenter.getProximaPaginaNoticias();
                }
            }
        });
        return root;
    }

    /*
    Executado quando sai do fragmento (manter posição atual)
    Chama a função do presenter
    */
    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    /*
    Executado quando clica novamente já neste fragmento (voltar ao topo)
    Chama a função do presenter
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onDestroyView(layoutManager.findFirstVisibleItemPosition());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas no NoticiasAdapter para serem definidas por esta view
     */

    /**
    * Utilizado no NoticiasAdapter para abrir a activity da notícia
     * Prepara um bundle com o preview e inicia uma NoticiaActivity
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
    /*
    Funções declaradas no presenter para serem definidas por esta view
     */

    /**
     * Define um alarme que executa o serviço de fundo que atualiza carrega a página de notícias, salva e notifica as novas
     */
    @Override
    public void definirSincronizacaoPeriodicaNoticias() {
        NotificationsHelper.definirSincronizacaoPeriodicaNoticias(this.getContext());
    }

    /**
     * Utilizado para mudar os itens da recycler view
     * Define os previews do adapter e o notifica para atualizar
     * @param previews previews para serem exibidos pela recycler view
     */
    @Override
    public void atualizarRecyclerView(List<Preview> previews) {
        noticiasAdapter.setPreviews(previews);
        noticiasAdapter.notifyDataSetChanged();
    }

    /**
     * Utilizado para mudar a posição do recycler view
     * @param index posição do preview no topo
     */
    @Override
    public void setRecyclerViewPosition(int index) {
        layoutManager.scrollToPosition(index);
    }

    /**
     * Esconde a progress bar (começou a carregar)
     */
    @Override
    public void esconderProgressBar() {
        pb.setVisibility(View.GONE);
    }

    /**
     * Mostra a progress bar (começou a carregar)
     */
    @Override
    public void mostrarProgressBar() {
        pb.setVisibility(View.VISIBLE);
    }

    /**
     * Utilizado para exibir um texto na tela através do toast
     * @param texto texto a ser exibido no toast
     */
    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(this.getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}