package com.ifcbrusque.app.fragments.noticias;

import android.graphics.Bitmap;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.data.ImagemManager;
import com.ifcbrusque.app.helpers.PreferencesHelper;
import com.ifcbrusque.app.network.LeitorPaginaCampus;
import com.ifcbrusque.app.models.Preview;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.helpers.ImagemHelper.*;
import static com.ifcbrusque.app.helpers.ImagemHelper.redimensionarBitmap;
import static java.util.stream.Collectors.toList;

public class NoticiasPresenter {
    private View view;

    private LeitorPaginaCampus campus;
    private ImagemManager im;
    private AppDatabase db;
    private PreferencesHelper pref;

    private List<Preview> previewsArmazenados;
    private Integer ultimaPaginaAcessada;
    private boolean isCarregandoPagina;

    public NoticiasPresenter(View view, ImagemManager im, PreferencesHelper pref, AppDatabase db) {
        this.view = view;
        this.im = im;
        this.pref = pref;
        this.db = db;
        this.campus = new LeitorPaginaCampus();

        isCarregandoPagina = false;
        previewsArmazenados = new ArrayList<>();
        ultimaPaginaAcessada = pref.getUltimaPaginaNoticias();

        Completable.fromRunnable(() -> {
            previewsArmazenados = db.previewDao().getAll();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
            //TODO
            }).doOnComplete(() -> {
            //Carregar primeira página
            if (previewsArmazenados.size() == 0) {
                //Carregar pela primeira vez
                ultimaPaginaAcessada = 1;
                getPaginaNoticias(ultimaPaginaAcessada);
            } else {
                //Carregar primeira página para ver se tem algo novo
                getPaginaNoticias(1);
            }
            }).subscribe();
    }

    public boolean isCarregandoPagina() {
        return isCarregandoPagina;
    }

    public List<Preview> getPreviewsArmazenados() {
        return previewsArmazenados;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Utilizado pelo view
     */
    void getProximaPaginaNoticias() {
        ultimaPaginaAcessada++;
        getPaginaNoticias(ultimaPaginaAcessada);
    }

    private void getPaginaNoticias(int pagina) {
        isCarregandoPagina = true;
        campus.getPaginaNoticias(pagina)
                .doOnError(e -> {
                    //TODO
                })
                .doOnNext(previews -> {
                    armazenarPreviewsNovos(previews);
                    salvarImagensInternet(previews, false);
                })
                .doOnComplete(() -> {
                    isCarregandoPagina = false;
                }).subscribe();
    }

    /*
    Utilizada para adicionar os previews NOVOS no banco de dados
     */
    void armazenarPreviewsNovos(List<Preview> previews) {
        Completable.fromRunnable(() -> {
            previewsArmazenados = db.previewDao().getAll(); //Atualizar para o mais recente

            List<Preview> previewsNovos = new ArrayList<>();
            for (Preview p : previews) { //Procurar os previews não armazenados
                if (!previewsArmazenados.stream().filter(_p -> _p.getUrlNoticia().equals(p.getUrlNoticia())).findFirst().isPresent()) {
                    previewsNovos.add(p);
                }
            }

            if (previewsNovos.size() > 0) {
                db.previewDao().insertAll(previewsNovos);
                previewsArmazenados = db.previewDao().getAll();
            }

            pref.setUltimaPagina(ultimaPaginaAcessada); //Isso aqui é desnecessário pra quando carrega a primeira página além da primeira vez, mas não atrapalha
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    //TODO
                }).doOnComplete(() -> view.atualizarRecyclerView(previewsArmazenados))
                .subscribe();
    }

    /*
    Armazena as imagens dos previews a partir de uma lista contendo os Preview

    Se armazenar corretamente, o nome da imagem armazenada
    Se não armazenar, retorna uma string vazia
    */
    public void salvarImagensInternet(List<Preview> previews, boolean overwrite) {
        Observable.just(previews.stream().map(o -> o.getUrlImagemPreview()).collect(toList()))
                .flatMapIterable(x -> x)
                .map(url -> {
                    if (im.imagemFormatoAceito(url)) {
                        long tamanhoImagemArmazenada = im.tamanhoImagemArmazenada(url); //Se a imagem não existir, o valor é 0

                        if (tamanhoImagemArmazenada == 0 || overwrite) {
                            System.out.println("[NoticiasFragment] Baixando imagem: " + url);
                            byte[] bytesImagemBaixada = baixarImagem(url, campus.getClient());

                            Bitmap bitmapImagemBaixada = byteParaBitmap(bytesImagemBaixada);
                            Bitmap bitmapRedimensionado = redimensionarBitmap(bitmapImagemBaixada, 300);
                            byte[] bytesImagemRedimensionada = bitmapParaByte(bitmapRedimensionado, 100);

                            return im.armazenarImagem(bytesImagemRedimensionada, im.getNomeArmazenamentoImagem(url), overwrite);
                        }
                    }
                    return "";
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    //TODO
                })
                .doOnNext(nomeImagemArmazenada -> { //Atualizar a recycler view com a imagem
                    if (nomeImagemArmazenada.length() > 0) {
                        System.out.println("[NoticiasPresenter] Atualizando: " + nomeImagemArmazenada);

                        int indexAtualizado = previewsArmazenados.indexOf(previewsArmazenados.stream().filter(o -> im.getNomeArmazenamentoImagem(o.getUrlImagemPreview()).equals(nomeImagemArmazenada)).findFirst().get());
                        view.atualizarImagemRecyclerView(indexAtualizado);
                    }
                })
                .subscribe();
        ;
    }

    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void atualizarRecyclerView(List<Preview> preview);

        void atualizarImagemRecyclerView(int index);
    }
}
