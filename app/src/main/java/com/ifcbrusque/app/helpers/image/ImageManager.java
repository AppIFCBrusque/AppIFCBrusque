package com.ifcbrusque.app.helpers.image;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/*
Funções que envolvem o armazenamento de imagens no dispositivo e dependem de contexto
 */
public class ImageManager {
    static private String diretorio = "/img/";
    static private String[] formatosAceitos = new String[]{"jpeg", "jpg", "png"};
    private Context context;

    public ImageManager(Context context) {
        this.context = context;
    }

    /*
    Confere se a imagem do url está na lista de formatos aceitos
     */
    static public boolean imagemFormatoAceito(String url) {
        String[] i = url.split("\\.");
        return Arrays.asList(formatosAceitos).contains(i[i.length - 1]);
    }

    /*
    Retorna o nome com qual a imagem de algum link é armazenado
    Exemplo:
    http://brusque.ifc.edu.br/wp-content/uploads/sites/2/2021/07/novo-edital-2021-300x300.png -> 2_2021_07_novo-edital-2021-300x300.png
     */
    static public String getNomeArmazenamentoImagem(String url) {
        return url.replace("http://", "").replace("https://", "").replace("noticias.brusque.ifc.edu.br/wp-content/uploads/sites/", "").replace("/", "_");
    }

    /*
    Retorna o diretório de armazenamento de alguma imagem
    Exemplo:
    http://brusque.ifc.edu.br/wp-content/uploads/sites/2/2021/07/novo-edital-2021-300x300.png -> (LOCAL DE ARMAZENAMENTO)/img/2_2021_07_novo-edital-2021-300x300.png
     */
    public Uri getUriArmazenamentoImagem(String url) {
        return Uri.parse(context.getExternalFilesDir(diretorio) + "/" + getNomeArmazenamentoImagem(url));
    }

    static public Uri getUriArmazenamentoImagem(String url, Context context) {
        return Uri.parse(context.getExternalFilesDir(diretorio) + "/" + getNomeArmazenamentoImagem(url));
    }

    /*
    Confere se o tamanho de uma imagem armazenada (este método é utilizado somente fora desta classe)
     */
    public long getTamanhoImagemArmazenada(String url) {
        File arquivo = new File(context.getExternalFilesDir(diretorio), getNomeArmazenamentoImagem(url));
        if(arquivo.exists()) return arquivo.length();
        return 0;
    }

    /*
    Salva uma imagem no armazenamento

    Retorna o nome do arquivo salvo (se não salvar, retorna uma string em branco)
     */
    public String armazenarImagem(byte[] bytesImagem, String nomeArmazenamento, boolean overwrite) throws IOException {
        File arquivo = new File(context.getExternalFilesDir(diretorio), nomeArmazenamento);

        //Conferir se o arquivo existe
        if (arquivo.exists()) {
            if(!overwrite) {
                //Se não for para sobreescrever, não salvar
                return "";
            } else {
                //Se for para sobreescrever, deleta o arquivo atual
                arquivo.delete();
            }
        }

        //Salvar
        FileOutputStream fos = new FileOutputStream(arquivo);
        fos.write(bytesImagem);
        fos.close();

        System.out.println("[ImagemManager] Salvo: " + nomeArmazenamento);
        return nomeArmazenamento;
    }
}
