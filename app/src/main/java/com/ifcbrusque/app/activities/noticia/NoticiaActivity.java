package com.ifcbrusque.app.activities.noticia;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.network.PaginaNoticias;

import org.jetbrains.annotations.NotNull;

public class NoticiaActivity extends AppCompatActivity implements NoticiaPresenter.View {
    private NoticiaPresenter presenter;

    private WebView wv;
    private CircularProgressIndicator pb;

    /*
    Chaves do Bundle
     */
    public static final String NOTICIA_TITULO = "NOTICIA_TITULO";
    public static final String NOTICIA_DATA = "NOTICIA_DATA";
    public static final String NOTICIA_URL = "NOTICIA_URL";
    public static final String NOTICIA_URL_IMAGEM_PREVIEW = "NOTICIA_URL_IMAGEM_PREVIEW";

    @Override
    protected void onCreate(@NotNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);

        wv = findViewById(R.id.wvNoticia);
        pb = findViewById(R.id.pbCircularNoticia);

        //Ativar botão de voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Presenter
        presenter = new NoticiaPresenter(this, getIntent().getExtras(), AppDatabase.getDbInstance(this.getApplicationContext()), new PaginaNoticias(getApplicationContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void carregarHtmlWebView(String html) {
        wv.loadData(html, "text/html", "UTF-8");
    }

    @Override
    public void esconderProgressBar() {
        pb.setVisibility(View.GONE);
    }

    @Override
    public void mostrarProgressBar() {
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}