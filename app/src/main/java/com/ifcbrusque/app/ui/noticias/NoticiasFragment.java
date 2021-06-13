package com.ifcbrusque.app.ui.noticias;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.noticias.NoticiasHelper;
import com.ifcbrusque.app.data.noticias.classe.Preview;

import java.util.ArrayList;

public class NoticiasFragment extends Fragment {

    private NoticiasViewModel noticiasViewModel;

    RecyclerView recyclerView;
    NoticiasAdapter noticiasAdapter;
    NoticiasHelper noticias;
    int ultimaPagina = 1;

    ArrayList<Preview> previews = new ArrayList<>();
    boolean carregandoNoticias = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        noticiasViewModel =
                new ViewModelProvider(this).get(NoticiasViewModel.class);
        View root = inflater.inflate(R.layout.fragment_noticias, container, false);

        recyclerView = root.findViewById(R.id.recyclerView_noticias);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        noticiasAdapter = new NoticiasAdapter(this.getContext(), previews);
        recyclerView.setAdapter(noticiasAdapter);

        /*final TextView textView = root.findViewById(R.id.text_home);
        noticiasViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        noticias = new NoticiasHelper();
        obterPagina(ultimaPagina);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(layoutManager.findLastCompletelyVisibleItemPosition()>=previews.size()-5 && !carregandoNoticias){
                    carregandoNoticias = true;
                    ultimaPagina++;
                    obterPagina(ultimaPagina);
                }

                //TODO: esconder a barra de navegação quando a pessoa começa a scrollar para baixo (pq ela esconde parte da ultima noticia)
            }
        });


        return root;
    }

    void obterPagina(int numeroPagina) {
        System.out.println("[NOTÍCIAS] Carregando página " + numeroPagina);
        noticias.getPaginaNoticias(numeroPagina)
                .doOnNext(previewsRetornados -> {
                    for (Preview p : previewsRetornados) {
                        if (!previews.contains(p)) {
                            previews.add(p);
                        }
                    }
                }).doOnComplete(() -> {
            noticiasAdapter.notifyItemInserted(previews.size() - 1);
            carregandoNoticias = false;
        })
                .subscribe();
    }
}