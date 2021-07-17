package com.ifcbrusque.app.helpers.noticia;

import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Response;

public class NoticiasParser {
    public static SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");

    private NoticiasParser() {}
    /*
    Transforma uma página como
    http://noticias.brusque.ifc.edu.br/category/noticias/page/14/
    em um ArrayList<Preview>
     */
    public static ArrayList<Preview> objetosPreview(Response r) throws IOException, ParseException {
        Document d = Jsoup.parse(r.body().string());

        ArrayList<Preview> l = new ArrayList<>();
        for(Element preview : d.getElementsByClass("media")) { //Cada classe media é um preview
            String titulo = "", descricao = "", urlImagem = "", urlNoticia = "";
            Date data = new Date();

            for(Element imagem : preview.getElementsByTag("img")) {
                urlImagem = imagem.attr("src");
            }

            for(Element heading : preview.getElementsByClass("media-heading")) {
                titulo = heading.text();
                urlNoticia = heading.getElementsByTag("a").get(0).attr("href");
            }

            for(Element info : preview.getElementsByClass("info")) {
                try {
                    data = FORMATO_DATA.parse(info.getElementsByClass("text-muted").get(0).text().replace("[", "").replace("]", ""));
                } catch (ParseException e) {
                    data = FORMATO_DATA.parse("01/01/2999"); //Para garantir que não vai dar erro
                }
                info.getElementsByClass("text-muted").get(0).remove();
                descricao = info.text();
            }
            l.add(new Preview(titulo, descricao, urlImagem, urlNoticia, data));
        }
        return l;
    }

    /*
    Transforma uma página como
    http://noticias.brusque.ifc.edu.br/2021/03/16/graduacao-em-redes-de-computadores-inscreva-se/
    em um objeto Noticia
     */
    //TODO:
    // Arrumar isso (e a classe Noticia) quando for fazer a activity que mostra a notícia.
    // - Algumas imagens redirecionam para outros links.
    // - Há elementos a no meio do texto que levam para algum link.
    // - Há imagens no meio do texto.
    // - Negrito, itálico, etc...
    // - Há imagens que não levam pra link algum.
    // http://noticias.brusque.ifc.edu.br/2019/05/20/alunos-e-servidores-do-ifc-brusque-participam-do-curso-de-brigada-de-incendio/
    // http://noticias.brusque.ifc.edu.br/2021/04/07/recepcao-dos-estudantes-dos-cursos-noturnos-licenciatura-em-quimica-cst-redes-e-cervejaria/
    // http://noticias.brusque.ifc.edu.br/2019/04/30/1971/
    // http://noticias.brusque.ifc.edu.br/2021/03/16/graduacao-em-redes-de-computadores-inscreva-se/
    public static Noticia objetoNoticia(Response r, Preview p) throws IOException {
        Document d = Jsoup.parse(r.body().string());

        String titulo = "", htmlConteudo = "";

        for(Element subheader : d.getElementsByClass("page-subheader")) {
            titulo = subheader.text();
        }

        for(Element content : d.getElementsByClass("entry-content")) {
            htmlConteudo = content.html();
        }

        return new Noticia(p.getUrlNoticia(), titulo, htmlConteudo, p.getDataNoticia());
    }
}
