package com.ifcbrusque.app.activities.noticia;

import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ifcbrusque.app.helpers.noticia.NoticiasParser;
import com.ifcbrusque.app.helpers.noticia.PaginaNoticias;
import com.ifcbrusque.app.models.Preview;

import java.util.regex.Pattern;

import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.Converters.*;

public class NoticiaPresenter  {
    final private String TAG = "[DEBUGA]";

    //Regex para encontrar imagens redimensionadas
    final private Pattern pattern = Pattern.compile("-[0-9]{1,4}x[0-9]{1,4}");

    private View view;

    private Preview preview;

    private PaginaNoticias campus;

    public NoticiaPresenter(NoticiaPresenter.View view, Bundle bundle) {
        this.view = view;
        campus = new PaginaNoticias();

        preview = new Preview(bundle.getString(NOTICIA_TITULO), "", bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW), bundle.getString(NOTICIA_URL), fromTimestamp(bundle.getLong(NOTICIA_DATA)));

        getNoticia();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void getNoticia() {
        campus.getNoticia(preview)
                .doOnError(e -> {
                    //TODO
                })
                .doOnNext(noticia -> {
                    //TODO: armazenar a noticia
                    //armazenarPreviewsNovos(previews);
                    //salvarImagensInternet(previews, false);
                    view.carregarHtmlWebView(formatarCorpoNoticia(noticia.getHtmlConteudo()));
                }).subscribe();
    }

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
            String srcSemExtensao = pattern.matcher(img.attr("src")).replaceFirst("").replace(".jpeg", "").replace(".png", "").replace(".jpg", "");
            String previewSemExtensao = preview.getUrlImagemPreview().replace(".jpeg", "").replace(".png", "").replace(".jpg", "");

            if(srcSemExtensao.contains(previewSemExtensao) || previewSemExtensao.contains(srcSemExtensao)) contemPreview = true;
            //Log.d(TAG, "formatarCorpoNoticia: " + src + "\n" + preview.getUrlImagemPreview() + "\n" + src.contains(preview.getUrlImagemPreview().replace(".jpeg", "").replace(".png", "").replace(".jpg", "")) + "\n" + );
            //TODO: Também dava para usar o regex pra limpar o url do preview em si
        }

        doc.getElementsByTag("body").first().before("<h3 class=\"titulo\">" + preview.getTitulo() + "</h3>"); //Título no topo
        doc.getElementsByClass("titulo").get(0).after("<p class=\"data\">" + NoticiasParser.FORMATO_DATA.format(preview.getDataNoticia()) + "</p>"); //Data abaixo do título TODO: formatar bonitinho isso também
        doc.getElementsByClass("data").get(0).after("<hr class=\"barra_horizontal\"></hr>");
        //Preview abaixo da data (adiciona somente se já não está no texto)
        if(!contemPreview && !preview.getUrlImagemPreview().equals("")) doc.getElementsByClass("barra_horizontal").get(0).after("<br><img class=\"preview\" src=\"" + preview.getUrlImagemPreview() + "\" style=\"width: 100%; height: auto;\">");


        return doc.toString();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public interface View {
        /*
        Métodos utilizados aqui para atualizar a view
         */
        void carregarHtmlWebView(String html);
    }
}
