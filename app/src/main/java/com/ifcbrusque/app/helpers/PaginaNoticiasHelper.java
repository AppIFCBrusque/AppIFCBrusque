package com.ifcbrusque.app.helpers;

import com.ifcbrusque.app.models.Noticia;
import com.ifcbrusque.app.models.Preview;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

import okhttp3.Response;

/*
Classe com funções relacionadas a página de notícias do campus
 */
public class PaginaNoticiasHelper {
    public static SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");

    private PaginaNoticiasHelper() {
    }

    /**
     * Transforma uma página como
     * http://noticias.brusque.ifc.edu.br/category/noticias/page/14/
     * em um ArrayList<Preview>
     * @param r response do cliente web
     * @return lista com os previews
     */
    public static ArrayList<Preview> getObjetosPreview(Response r) throws IOException, ParseException {
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

    /**
     * Transforma uma página como
     * http://noticias.brusque.ifc.edu.br/2021/03/16/graduacao-em-redes-de-computadores-inscreva-se/
     * em um objeto Noticia
     * @param r resposta do cliente web
     * @param p preview da notícia
     * @return objeto notícia
     */
    public static Noticia getObjetoNoticia(Response r, Preview p) throws IOException {
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

    /**
     Utilizado para encontrar o "caminho real" de uma imagem (usado para tentar identificar se elas são iguais somente através do link)

     Exemplo:
     http://brusque.ifc.edu.br/wp-content/uploads/sites/2/2021/07/PNAEImagemBrusque.png -> 2021/07/PNAEImagemBrusque
     */
    public static String getCaminhoImagemNoticia(String src) {
        //Regex utilizado para limpar o caminho das imagens
        final Pattern patternRedimensionamento = Pattern.compile("-[0-9]{1,4}x[0-9]{1,4}");
        final Pattern patternCaminho = Pattern.compile("wp-content/uploads/sites/[0-9]{1,2}/");

        String srcSemSite = src.replace("http://noticias.brusque.ifc.edu.br/", "").replace("http://noticias.ifc.edu.br/", "");
        String srcSemRedimensionamentoEFormato = patternRedimensionamento.matcher(srcSemSite).replaceFirst("").replace(".jpeg", "").replace(".png", "").replace(".jpg", "");
        String srcSemCaminho = patternCaminho.matcher(srcSemRedimensionamentoEFormato).replaceFirst("");

        return srcSemCaminho;
    }

    /**
     * Utilizado para formatar o conteúdo em HTML da notícia obtido do site do campus a um formato que se adeque melhor ao web view do aplicativo
     * @param html .html() do elemento com classe "entry-content" na página da notícia
     * @return corpo formatado
     */
    public static String formatarCorpoNoticia(Preview preview, String html) {
        //TODO: Preciso organizar isto melhor alguma hora
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
            String srcImagem = getCaminhoImagemNoticia(img.attr("src"));
            String srcPreview = getCaminhoImagemNoticia(preview.getUrlImagemPreview());
            if(srcImagem.contains(srcPreview) || srcPreview.contains(srcImagem)) contemPreview = true;
        }

        doc.getElementsByTag("body").first().before("<h3 class=\"titulo\">" + preview.getTitulo() + "</h3>"); //Título no topo
        doc.getElementsByClass("titulo").get(0).after("<p class=\"data\">" + FORMATO_DATA.format(preview.getDataNoticia()) + "</p>"); //Data abaixo do título TODO: formatar bonitinho isso também
        doc.getElementsByClass("data").get(0).after("<hr class=\"barra_horizontal\"></hr>");
        //Preview abaixo da data (adiciona somente se já não está no texto)
        if(!contemPreview && !preview.getUrlImagemPreview().equals("")) doc.getElementsByClass("barra_horizontal").get(0).after("<br><img class=\"preview\" src=\"" + preview.getUrlImagemPreview() + "\" style=\"width: 100%; height: auto;\">");

        return doc.toString();
    }
}
