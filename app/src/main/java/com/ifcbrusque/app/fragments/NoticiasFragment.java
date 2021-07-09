package com.ifcbrusque.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.adapters.NoticiasAdapter;
import com.ifcbrusque.app.helpers.PreferencesHelper;
import com.ifcbrusque.app.helpers.noticias.NoticiasHelper;
import com.ifcbrusque.app.models.Preview;
import com.ifcbrusque.app.data.AppDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NoticiasFragment extends Fragment {
    //TODO: - ordenar a recycler view baseado na data?
    //      - salvar a posicao do scroll
    //      -> lembrar de dps deletar a pasta db inteira (nao to mais usando)

    private Integer carregarQuandoFaltar = 5; //Quando estiver este número de notícias abaixo da atual, será carregada a próxima página

    RecyclerView recyclerView;
    NoticiasAdapter noticiasAdapter;
    NoticiasHelper noticias;

    AppDatabase db;
    PreferencesHelper pref;

    private List<Preview> previewsSalvos; //Lista de previews utilizados pelo recycler view
    private Integer ultimaPagina; //Número da última página acessada
    private Integer firstCompletelyVisibleItemPosition; //Utilizado para, quando retornar ao fragmento, voltar para a última posição que deixou
    private Boolean carregandoPagina; //Indica se está atualmente carregando alguma página

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //Inflar este fragmento
        View root = inflater.inflate(R.layout.fragment_noticias, container, false);

        //Database e shared preferences
        db = AppDatabase.getDbInstance(this.getContext().getApplicationContext());
        pref = new PreferencesHelper(this.getContext().getApplicationContext());

        ultimaPagina = pref.getUltimaPaginaNoticias();

        //Configuração do recycler view
        recyclerView = root.findViewById(R.id.recyclerView_noticias);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        Observable.defer(() -> { //Eu preciso usar a DB aqui, então fiz isso em um observable TODO: Acho que dá pra usar só o .just mesmo
            return Observable.just(db.previewDao().getAll()); //Realizar o login da sessão
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(previewsSalvos -> {
                    this.previewsSalvos = previewsSalvos;
                })
                .doOnComplete(() -> {
                    //Iniciar recycler view
                    noticiasAdapter = new NoticiasAdapter(this.getContext(), previewsSalvos);
                    recyclerView.setAdapter(noticiasAdapter);
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { //Adicionar listener para solicitar nova página caso se aproxime do final
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                            if(layoutManager.findLastCompletelyVisibleItemPosition()>=previewsSalvos.size()-carregarQuandoFaltar && !carregandoPagina){
                                carregandoPagina = true;
                                ultimaPagina++;
                                obterPaginaDeNoticias(ultimaPagina);
                            }
                        }
                    });

                    //Carregar primeira página
                    if(previewsSalvos.size() == 0) {
                        //Carregar pela primeira vez
                        ultimaPagina = 1;
                        obterPaginaDeNoticias(ultimaPagina);
                    } else {
                        //Carregar primeira página para ver se tem algo novo
                        obterPaginaDeNoticias(1);
                    }
                }).subscribe();

        ///Inicializar helper de notícias
        noticias = new NoticiasHelper(this.getContext());

        return root;
    }

    //TODO: Reorganizar estas funções em algo tipo um presenter
    void obterPaginaDeNoticias(int numeroPagina) {
        System.out.println("[NoticiasFragment] Carregando página " + numeroPagina);
        noticias.getPaginaNoticias(numeroPagina)
                .doOnNext(previewsRetornados -> {
                    adicionarNoticiasNovasDatabase(previewsRetornados); //Adicionar pro banco de dados
                    salvarImagensNovas(previewsRetornados); //Salvar as imagens
                })
                .doOnComplete(() -> {
                    carregandoPagina = false;
                })
                .subscribe();
    }

    void adicionarNoticiasNovasDatabase(List<Preview> previewsRetornados) {
        Observable.defer(() -> {
            previewsSalvos = db.previewDao().getAll();
            List<Preview> previews_novos = new ArrayList<>();
            for (Preview p : previewsRetornados) {
                if (!previewsSalvos.stream().filter(_p -> _p.getUrlNoticia().equals(p.getUrlNoticia())).findFirst().isPresent()) {
                    previews_novos.add(p);
                }
            }
            if(previews_novos.size() > 0) {
                db.previewDao().insertAll(previews_novos);
                previewsSalvos = db.previewDao().getAll();
            }
            pref.setUltimaPagina(ultimaPagina); //Isso aqui é desnecessário pra quando carrega a primeira página além da primeira vez, mas não atrapalha

            return Observable.just(true);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    //Atualizar a recycler view
                    noticiasAdapter.previews = previewsSalvos;
                    noticiasAdapter.notifyItemInserted(previewsSalvos.size() - 1);
                })
                .subscribe();
    }

    void salvarImagensNovas(List<Preview> previewsRetornados) {
        noticias.salvarImagens(previewsRetornados, false)
                .doOnNext(urlImagemBaixada -> {
                    //Atualizar a recycler view com a imagem
                    if(urlImagemBaixada.length() > 0) {
                        System.out.println("[NoticiasFragment] Atualizando: " + urlImagemBaixada);
                        int indexAtualizado = previewsSalvos.indexOf(previewsSalvos.stream().filter(o -> o.getUrlImagemPreview().equals(urlImagemBaixada)).findFirst().get());
                        if(noticiasAdapter.previews.size() > indexAtualizado) noticiasAdapter.notifyItemChanged(indexAtualizado); //Sem esse if, ele pode tentar atualizar um item que ainda não foi inserido na recycler view, crashando o aplicativo
                    }
                })
                .subscribe();
    }
}