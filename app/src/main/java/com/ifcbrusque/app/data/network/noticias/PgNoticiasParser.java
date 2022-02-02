package com.ifcbrusque.app.data.network.noticias;

import com.ifcbrusque.app.data.db.model.Noticia;
import com.ifcbrusque.app.data.db.model.Preview;

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
public class PgNoticiasParser {
    public static final SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy");

    private PgNoticiasParser() {
    }

    /**
     * Transforma uma página como
     * http://noticias.brusque.ifc.edu.br/category/noticias/page/14/
     * em um ArrayList<Preview>
     *
     * @param r response do cliente web
     * @return lista com os previews
     */
    public static ArrayList<Preview> getObjetosPreview(Response r) throws IOException {
        Document d = Jsoup.parse(r.body().string());

        ArrayList<Preview> l = new ArrayList<>();
        for (Element preview : d.getElementsByClass("media")) { //Cada classe media é um preview
            String titulo = "", descricao = "", urlImagem = "", urlNoticia = "";
            Date data = new Date();

            for (Element imagem : preview.getElementsByTag("img")) {
                urlImagem = imagem.attr("src");

                //Utilizar a versão com maior qualidade das imagens que são frequentemente reutilizadas
                switch (urlImagem) {
                    case "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2018/07/East-Shore-1-e1592944304903.png":
                        urlImagem = "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2018/07/East-Shore-1.png";
                        break;
                    case "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2020/06/resultado-e1592521217969.png":
                        urlImagem = "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2020/06/resultado.png";
                        break;
                    case "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2017/01/AVISO-1-300x300-e1483529798833.png":
                        urlImagem = "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2017/01/AVISO-1-300x300.png";
                        break;
                    case "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2016/09/aviso-e1519079412387.jpg":
                        urlImagem = "http://noticias.brusque.ifc.edu.br/wp-content/uploads/sites/2/2016/09/aviso.jpg";
                        break;
                }
            }

            for (Element heading : preview.getElementsByClass("media-heading")) {
                titulo = heading.text();
                urlNoticia = heading.getElementsByTag("a").get(0).attr("href");
            }

            for (Element info : preview.getElementsByClass("info")) {
                try {
                    data = FORMATO_DATA.parse(info.getElementsByClass("text-muted").get(0).text().replace("[", "").replace("]", ""));
                } catch (ParseException e) {
                    data = new Date();
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
     *
     * @param r resposta do cliente web
     * @param p preview da notícia
     * @return objeto notícia
     */
    public static Noticia getObjetoNoticia(Response r, Preview p) throws IOException {


        Document d = Jsoup.parse(r.body().string());

        String titulo = "", htmlConteudo = "", dataFormatada = "";

        for (Element subheader : d.getElementsByClass("page-subheader")) {
            titulo = subheader.text();
        }

        for (Element data : d.getElementsByClass("text-muted")) {
            dataFormatada = data.text();
        }

        for (Element content : d.getElementsByClass("entry-content")) {
            htmlConteudo = content.outerHtml();
        }

        return new Noticia(p.getUrlNoticia(), titulo, htmlConteudo, p.getDataNoticia(), dataFormatada);
    }

    /**
     * Utilizado para encontrar o "caminho real" de uma imagem (usado para tentar identificar se elas são iguais somente através do link)
     * <p>
     * Exemplo:
     * http://brusque.ifc.edu.br/wp-content/uploads/sites/2/2021/07/PNAEImagemBrusque.png -> 2021/07/PNAEImagemBrusque
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
     */
    public static String formatarCorpoNoticia(Preview preview, String html) {
        Document doc = Jsoup.parse(html);

        //Continuar o texto que transborda na próxima linha
        doc.getElementsByTag("body").attr("style", "overflow-x: hidden; overflow-wrap: break-word;");

        //Ajustar imagens
        Elements imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            //Tamanho das imagens
            img.attr("style", "display: inline; max-width: 100%; height: auto;");

            //Centralizar e deixar um espaço nas imagens que estavam alinhadas
            if (img.className().contains("alignright") || img.className().contains("alignleft") || img.className().contains("aligncenter")) {
                img.before("<br>");
                img.after("<br>");
                img.attr("style", "display: block; max-width: 100%; height: auto; margin: 0 auto;");
            }
        }

        return doc.html();
    }
}
