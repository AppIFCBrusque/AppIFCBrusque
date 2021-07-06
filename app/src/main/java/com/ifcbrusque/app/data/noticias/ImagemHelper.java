package com.ifcbrusque.app.data.noticias;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
Classe com métodos para salvar e abrir imagens no diretório interno
 */
public class ImagemHelper {
    static private String[] formatosAceitos = new String[]{"jpeg", "jpg", "png"};
    static private String diretorio = "/img/";

    static public String salvarImagemUrl(String i, OkHttpClient cliente, Context context) throws IOException {
        System.out.println("[ImagemHelper] Conferindo: " + i);
        String[] _i = i.split("\\.");
        if (!Arrays.asList(formatosAceitos).contains(_i[_i.length - 1])) { //Conferir se a extensão do arquivo em questão está na lista de formatos aceitos
            return "";
        }

        String nome = getNomeArmazenamentoImagem(i);
        File arquivo = new File(context.getExternalFilesDir(diretorio), nome);
        if (arquivo.exists()) { //Se o arquivo já existir, ir pra próxima imagem
            return "";
        }

        //Obter imagem da internet
        Request request = new Request.Builder()
                .url(i)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response r = cliente.newCall(request).execute();
        byte[] bytes = IOUtils.toByteArray(r.body().byteStream());

        //Compressão
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        int widthNova = 200;
        double proporcao = (double) widthNova / (bitmap.getWidth());
        int heightNova = (int) Math.ceil(bitmap.getHeight() * proporcao);
        bitmap = bitmap.createScaledBitmap(bitmap, widthNova, heightNova, true); //Diminuir o tamanho. Imagens muito grande resultam em queda de frame
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //Passar para a stream e diminuir a qualidade
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();

        //Salvar imagem
        FileOutputStream fos = new FileOutputStream(arquivo);
        fos.write(byteArray);
        fos.close();
        System.out.println("[ImagemHelper] Salvo: " + i);
        return i;
    }

    static public String getNomeArmazenamentoImagem(String url) {
        return url.replace("http://", "").replace("https://", "").replace("noticias.brusque.ifc.edu.br/wp-content/uploads/sites/", "").replace("/", "_");
    }

    static public Uri getUriArmazenamentoImagem(String url, Context context) {
        return Uri.parse(context.getExternalFilesDir(diretorio) + "/" + getNomeArmazenamentoImagem(url));
    }
}
