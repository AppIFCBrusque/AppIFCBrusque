package com.ifcbrusque.app.helpers.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
Classe com funções para manipular imagens (baixar, comprimir, redimensionar...)
 */
public class ImageUtil {
    /*
    Construtor privado para não permitir instanciação
     */
    private ImageUtil() {}

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
    static public byte[] baixarImagem(String url, OkHttpClient cliente) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response r = cliente.newCall(request).execute();

        return r.body().bytes();
    }
}
