package com.ifcbrusque.app.activities.noticia;

import android.os.Bundle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.helpers.NoticiasParser;
import com.ifcbrusque.app.models.PaginaNoticias;
import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import java.io.IOException;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.ifcbrusque.app.activities.noticia.NoticiaActivity.*;
import static com.ifcbrusque.app.data.Converters.*;

public class NoticiaPresenter  {
    private View view;

    private Preview preview;
    private Noticia noticia;

    private PaginaNoticias campus;
    private AppDatabase db;

    /*
    Regex utilizado no getCaminhoImagem para limpar o caminho das imaegns
     */
    final private Pattern patternRedimensionamento = Pattern.compile("-[0-9]{1,4}x[0-9]{1,4}");
    final private Pattern patternCaminho = Pattern.compile("wp-content/uploads/sites/[0-9]{1,2}/");

    public NoticiaPresenter(NoticiaPresenter.View view, Bundle bundle, AppDatabase db, PaginaNoticias campus) {
        this.view = view;
        this.db = db;
        this.campus = campus;
        this.noticia = null;

        preview = new Preview(bundle.getString(NOTICIA_TITULO), "", bundle.getString(NOTICIA_URL_IMAGEM_PREVIEW), bundle.getString(NOTICIA_URL), fromTimestamp(bundle.getLong(NOTICIA_DATA)));

        carregarNoticia();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Utilizado para carregar uma notícia (do banco de dados ou da internet), armazenar e mostrar na view
     */
    private void carregarNoticia() {
        view.mostrarProgressBar();

        Observable.defer(() -> {
            noticia = db.noticiaDao().getNoticia(preview.getUrlNoticia()); //Consultar no banco de dados

            if(noticia == null) { //Não armazenada anteriormente -> obter da internet
                try {
                    noticia = campus.getNoticia(preview);
                } catch (IOException e) {
                    /*
                    Se acontecer algum erro, o noticia vai ser null
                    */
                }
                if(noticia != null) db.noticiaDao().insert(noticia); //Armazenar a notícia da internet
            }

            return (noticia != null) ? Observable.just(true) : Observable.just(false);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(carregou -> {
                    view.esconderProgressBar();

                    if(carregou) { //Carregou normalmente
                        view.carregarHtmlWebView(formatarCorpoNoticia(noticia.getHtmlConteudo()));
                        view.esconderProgressBar();
                    } else { //Erro de conexão
                        view.mostrarToast("ERRO DE CONEXÃO NOTICIAPRESENTER"); //////////////////////////////////////////
                    }
                }).subscribe();
    }

    //TODO: Mover isto para outro lugar
    /*
    Utilizado para formatar o conteúdo em HTML da notícia obtido do site do campus a um formato que se adeque melhor ao aplicativo
     */
    private String formatarCorpoNoticia(String html) { //TODO: Tenho que organizar isso melhor alguma hora
        Document doc = Jsoup.parse(html);

        doc.getElementsByTag("body").attr("style", "overflow-x: hidden; overflow-wrap: break-word;");

        boolean contemPreview = false;
        //Ajustar imagens
        Elements imgs = doc.getElementsByTag("img");
        imgs.after("<br>"); //Espaçamento depois das imagens
        for(Element img : imgs) {
            //Tamanho das imagens
            if(!img.className().equals("CToWUd")) { //As imagens com essa classe precisam permanecer no tamanho original -> imagens pequenas do "Graduação em Química Licenciatura, inscreva-se!"
                img.attr("style", "width: 100%; height: auto;"); //Ocupar o espaço horizontal inteiro
            } else {
                img.before("<br>"); //Espaçamento antes nas imagens menores
            }

            //Conferir se já tem o preview no meio da notícia
            String srcImagem = getCaminhoImagem(img.attr("src"));
            String srcPreview = getCaminhoImagem(preview.getUrlImagemPreview());
            if(srcImagem.contains(srcPreview) || srcPreview.contains(srcImagem)) contemPreview = true;
        }

        doc.getElementsByTag("body").first().before("<h3 class=\"titulo\">" + preview.getTitulo() + "</h3>"); //Título no topo
        doc.getElementsByClass("titulo").get(0).after("<p class=\"data\">" + NoticiasParser.FORMATO_DATA.format(preview.getDataNoticia()) + "</p>"); //Data abaixo do título TODO: formatar bonitinho isso também
        doc.getElementsByClass("data").get(0).after("<hr class=\"barra_horizontal\"></hr>");
        //Preview abaixo da data (adiciona somente se já não está no texto)
        if(!contemPreview && !preview.getUrlImagemPreview().equals("")) doc.getElementsByClass("barra_horizontal").get(0).after("<br><img class=\"preview\" src=\"" + preview.getUrlImagemPreview() + "\" style=\"width: 100%; height: auto;\">");

        return doc.toString();
    }

    /*
    Utilizado para encontrar o "caminho" de uma imagem (usado para tentar identificar se elas são iguais somente pelo link)

    Exemplo:
    http://brusque.ifc.edu.br/wp-content/uploads/sites/2/2021/07/PNAEImagemBrusque.png -> 2021/07/PNAEImagemBrusque
     */
    private String getCaminhoImagem(String src) {
        String srcSemSite = src.replace("http://noticias.brusque.ifc.edu.br/", "").replace("http://noticias.ifc.edu.br/", "");
        String srcSemRedimensionamentoEFormato = patternRedimensionamento.matcher(srcSemSite).replaceFirst("").replace(".jpeg", "").replace(".png", "").replace(".jpg", "");
        String srcSemCaminho = patternCaminho.matcher(srcSemRedimensionamentoEFormato).replaceFirst("");
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

        void mostrarToast(String texto);
    }
}
