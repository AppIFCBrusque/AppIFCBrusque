package com.ifcbrusque.app.activities.noticia;

import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.noticia.NoticiasParser;
import com.ifcbrusque.app.helpers.noticia.PaginaNoticias;
import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import java.io.IOException;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.Converters.*;

import static com.ifcbrusque.app.activities.MainActivity.TAG;

public class NoticiaPresenter  {
    //Regex para encontrar imagens redimensionadas
    final private Pattern patternRedimensionamento = Pattern.compile("-[0-9]{1,4}x[0-9]{1,4}");
    final private Pattern patternCaminho = Pattern.compile("wp-content/uploads/sites/[0-9]{1,2}/");

    private View view;

    private Preview preview;
    private Noticia noticia;

    private PaginaNoticias campus;
    private AppDatabase db;

    public NoticiaPresenter(NoticiaPresenter.View view, Bundle bundle, AppDatabase db) {
        this.view = view;
        this.db = db;
        campus = new PaginaNoticias();

        preview = new Preview(bundle.getString(NOTICIA_TITULO), "", bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW), bundle.getString(NOTICIA_URL), fromTimestamp(bundle.getLong(NOTICIA_DATA)));

        carregarNoticia();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void carregarNoticia() { //TODO: Talvez dê para organizar isso melhor
        view.mostrarProgressBar();
        Completable.fromRunnable(() -> {
            noticia = db.noticiaDao().getNoticia(preview.getUrlNoticia()); //Consultar no banco de dados

            if(noticia == null) { //Não armazenada anteriormente -> obter da internet
                try {
                    noticia = campus.getNoticia(preview);
                } catch (IOException e) {
                    ////
                }
                db.noticiaDao().insert(noticia); //Armazenar
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(e -> {
                    //TODO
                })
                .doOnComplete(() -> {
                    view.carregarHtmlWebView(formatarCorpoNoticia(noticia.getHtmlConteudo()));
                    view.esconderProgressBar();
                })
                .subscribe();
    }

    /*
    Utilizado para formatar o conteúdo em HTML da notícia obtido do site do campus a um formato que se adeque melhor ao aplicativo
     */
    private String formatarCorpoNoticia(String html) { //TODO: Deixar isso mais bonitinho
        Document doc = Jsoup.parse(html);

        doc.getElementsByTag("body").attr("style", "overflow-x: hidden; overflow-wrap: break-word;");

        boolean contemPreview = false;
        //Ajustar imagens
        Elements imgs = doc.getElementsByTag("img");
        imgs.after("<br>"); //Espaçamento
        for(Element img : imgs) {
            //Tamanho das imagens
            if(!img.className().equals("CToWUd")) { //As imagens com essa classe precisam permanecer no tamanho original -> imagens pequenas do "Graduação em Química Licenciatura, inscreva-se!"
                img.attr("style", "width: 100%; height: auto;"); //Ocupar o espaço horizontal inteiro
            } else {
                img.before("<br>");
            }

            //Conferir se já tem o preview no meio da notícia
            String srcImagem = getCaminhoImagem(img.attr("src"));
            String srcPreview = getCaminhoImagem(preview.getUrlImagemPreview());
            if(srcImagem.contains(srcPreview) || srcPreview.contains(srcImagem)) contemPreview = true;
            //TODO: Também dava para usar o regex pra limpar o url do preview em si
        }

        doc.getElementsByTag("body").first().before("<h3 class=\"titulo\">" + preview.getTitulo() + "</h3>"); //Título no topo
        doc.getElementsByClass("titulo").get(0).after("<p class=\"data\">" + NoticiasParser.FORMATO_DATA.format(preview.getDataNoticia()) + "</p>"); //Data abaixo do título TODO: formatar bonitinho isso também
        doc.getElementsByClass("data").get(0).after("<hr class=\"barra_horizontal\"></hr>");
        //Preview abaixo da data (adiciona somente se já não está no texto)
        if(!contemPreview && !preview.getUrlImagemPreview().equals("")) doc.getElementsByClass("barra_horizontal").get(0).after("<br><img class=\"preview\" src=\"" + preview.getUrlImagemPreview() + "\" style=\"width: 100%; height: auto;\">");

        return doc.toString();
    }

    /*
    Utilizado para encontrar o "caminho" de uma imagem (tentar identificar se elas são iguais somente pelo link)
     */
    private String getCaminhoImagem(String src) {
        String srcSemSite = src.replace("http://noticias.brusque.ifc.edu.br/", "").replace("http://noticias.ifc.edu.br/", "");
        String srcSemRedimensionamentoEFormato = patternRedimensionamento.matcher(srcSemSite).replaceFirst("").replace(".jpeg", "").replace(".png", "").replace(".jpg", "");
        String srcSemCaminho = patternCaminho.matcher(srcSemRedimensionamentoEFormato).replaceFirst("");
        Log.d(TAG, "getCaminhoImagem: " + srcSemCaminho);
        return srcSemCaminho;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void carregarHtmlWebView(String html);

        void esconderProgressBar();

        void mostrarProgressBar();
    }
}
