package com.ifcbrusque.app.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
Classe com métodos para manipular imagens (baixar, salvar, comprimir, redimensionar...)
 */
public class ImagemHelper {
    static private String[] formatosAceitos = new String[]{"jpeg", "jpg", "png"};
    static private String diretorio = "/img/";

    /*
    Construtor privado para não permitir instanciação
     */
    private ImagemHelper() {}

    /*
    Converte byte[] para Bitmap
     */
    static public Bitmap byteParaBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /*
    Converte um Bitmap para um ByteArrayOutputStream (utilizado para salvar os arquivos no armazenamento)

    100 -> qualidade máxima
     */
    static public byte[] bitmapParaByte(Bitmap bitmap, int qualidade) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, qualidade, stream);

        return stream.toByteArray();
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
    static public Uri getUriArmazenamentoImagem(String url, Context context) {
        return Uri.parse(context.getExternalFilesDir(diretorio) + "/" + getNomeArmazenamentoImagem(url));
    }

    /*
    Confere se a imagem do url está na lista de formatos aceitos
     */
    static public boolean imagemFormatoAceito(String url) {
        String[] i = url.split("\\.");
        return Arrays.asList(formatosAceitos).contains(i[i.length - 1]);
    }

    /*
    Confere se o tamanho de uma imagem armazenada (este método é utilizado somente fora desta classe)
     */
    static public long tamanhoImagemArmazenada(Context context, String url) {
        File arquivo = new File(context.getExternalFilesDir(diretorio), getNomeArmazenamentoImagem(url));
        if(arquivo.exists()) return arquivo.length();
        return 0;
    }

    /*
    Redimensiona um bitmap mantendo a escala
    300x600 -> 150x300
     */
    static public Bitmap redimensionarBitmap(Bitmap bitmap, int widthNova) {
        double proporcao = (double) widthNova / (bitmap.getWidth());
        int heightNova = (int) Math.ceil(bitmap.getHeight() * proporcao);

        return bitmap.createScaledBitmap(bitmap, widthNova, heightNova, true);
    }

    /*
    Baixa alguma imagem da internet e retorna um byte[]

    Retorna null se não for um formato aceito
     */
    /*
    static public byte[] baixarImagem(String url, OkHttpClient cliente) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response r = cliente.newCall(request).execute();

        return IOUtils.toByteArray(r.body().byteStream());
    }*/

    static public byte[] baixarImagem(String url, OkHttpClient cliente) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response r = cliente.newCall(request).execute();

        return r.body().bytes();
    }

    /*
    Salva uma imagem no armazenamento

    Retorna o nome do arquivo salvo (se não salvar, retorna uma string em branco)
     */
    static public String armazenarImagem(Context context, byte[] bytesImagem, String nomeArmazenamento, boolean overwrite) throws IOException {
        File arquivo = new File(context.getExternalFilesDir(diretorio), nomeArmazenamento);

        //Conferir se o arquivo existe
        if (arquivo.exists()) {
            if(!overwrite) {
                //Se não for para sobreescrever, não salvar
                return "";
            } else {
                //Se for para sobreescrever, deleta o arquivo atual
                //TODO: Conferir se isto realmente funciona
                arquivo.delete();
            }
        }

        //Salvar
        FileOutputStream fos = new FileOutputStream(arquivo);
        fos.write(bytesImagem);
        fos.close();

        System.out.println("[ImagemHelper] Salvo: " + nomeArmazenamento);
        return nomeArmazenamento;
    }
}
