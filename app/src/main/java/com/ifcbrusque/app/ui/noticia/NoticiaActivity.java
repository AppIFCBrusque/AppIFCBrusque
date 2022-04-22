package com.ifcbrusque.app.ui.noticia;

import static com.ifcbrusque.app.data.db.Converters.dateToTimestamp;
import static com.ifcbrusque.app.utils.ThemeUtils.getCorEmHex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.imageview.ShapeableImageView;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.NoticiaArmazenavel;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class NoticiaActivity extends BaseActivity implements NoticiaContract.NoticiaView {
    public static final String NOTICIA_TITULO = "NOTICIA_TITULO";
    public static final String NOTICIA_DATA = "NOTICIA_DATA";
    public static final String NOTICIA_URL_IMAGEM_PREVIEW = "NOTICIA_URL_IMAGEM_PREVIEW";
    public static final String NOTICIA_HTML_CONTEUDO = "NOTICIA_HTML_CONTEUDO";
    public static final String NOTICIA_NOME_DISCIPLINA = "NOTICIA_NOME_DISCIPLINA";
    public static final String NOTICIA_URL = "NOTICIA_URL";
    @Inject
    NoticiaContract.NoticiaPresenter<NoticiaContract.NoticiaView> mPresenter;
    @Inject
    Picasso mPicasso;
    private WebView mWebView;
    private ShapeableImageView mImageView;
    private TextView mTvTitulo, mTvData;
    private RelativeLayout mRelativeLayout;

    /**
     * Cria um intent configurado para exibir uma notícia do campus
     */
    public static Intent getStartIntent(Context context, Preview preview) {
        Intent intent = new Intent(context, NoticiaActivity.class);

        intent.putExtra(NOTICIA_TITULO, preview.getTitulo());
        intent.putExtra(NOTICIA_DATA, dateToTimestamp(preview.getDataNoticia()));
        intent.putExtra(NOTICIA_NOME_DISCIPLINA, "");
        intent.putExtra(NOTICIA_URL_IMAGEM_PREVIEW, preview.getUrlImagemPreview());
        intent.putExtra(NOTICIA_URL, preview.getUrlNoticia());
        intent.putExtra(NOTICIA_HTML_CONTEUDO, "");

        return intent;
    }

    /**
     * Cria um intent configurado para exibir uma notícia do SIGAA
     */
    public static Intent getStartIntent(Context context, NoticiaArmazenavel noticiaArmazenavel) {
        Intent intent = new Intent(context, NoticiaActivity.class);

        intent.putExtra(NOTICIA_TITULO, noticiaArmazenavel.getTitulo());
        intent.putExtra(NOTICIA_DATA, dateToTimestamp(noticiaArmazenavel.getData()));
        intent.putExtra(NOTICIA_NOME_DISCIPLINA, noticiaArmazenavel.getDisciplinaNome());
        intent.putExtra(NOTICIA_URL_IMAGEM_PREVIEW, "");
        intent.putExtra(NOTICIA_URL, "");
        intent.putExtra(NOTICIA_HTML_CONTEUDO, noticiaArmazenavel.getHtmlConteudo());

        return intent;
    }


    @Override
    protected void onCreate(@NotNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticia);
        getActivityComponent().inject(this);
        mPresenter.onAttach(NoticiaActivity.this);
        setUp();
    }

    @Override
    protected void setUp() {
        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWebView = findViewById(R.id.wvNoticia);
        mImageView = findViewById(R.id.noticia_imagem_grande);
        mTvTitulo = findViewById(R.id.noticia_titulo);
        mTvData = findViewById(R.id.noticia_data);
        mRelativeLayout = findViewById(R.id.noticia_relative_layout);

        mWebView.setVerticalScrollBarEnabled(false);

        mPresenter.onViewPronta(getIntent().getExtras());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void loadHtmlWebView(String html, int idTema) {
        String htmlComTema = addTemaAoHtml(html, idTema);
        mWebView.loadDataWithBaseURL(null, htmlComTema, "text/html", "UTF-8", null);
        mWebView.setVisibility(View.VISIBLE);
    }

    private String addTemaAoHtml(String html, int tema) {
        String backgroundColor = "", textColor = "";

        switch (tema) {
            case 0:
                // Dia
                backgroundColor = getCorEmHex(getColor(R.color.background_dia));
                textColor = getCorEmHex(getColor(R.color.on_background_dia));
                break;

            case 2:
                // Meia-noite
                backgroundColor = getCorEmHex(getColor(R.color.background_meia_noite));
                textColor = getCorEmHex(getColor(R.color.on_background_meia_noite));
                break;
        }

        String style = "<style>body { background-color: " + backgroundColor + "; } .entry-content, span { color: " + textColor + " ; }</style>";

        int posInicioHead = html.indexOf("<head>") + 6;
        String htmlComTema = html.substring(0, posInicioHead) + style + html.substring(posInicioHead);

        return htmlComTema;
    }

    @Override
    public void loadImage(String url) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        mPicasso.load(url)
                .resize(width, 0)
                .into(mImageView);
    }

    @Override
    public void hideImageView() {
        mImageView.setVisibility(View.GONE);
    }

    @Override
    public void setTitulo(String titulo) {
        mTvTitulo.setText(titulo);
    }

    @Override
    public void setDisciplina(String disciplina) {
        // TODO
    }

    @Override
    public void showDisciplina() {
        // TODO
    }

    @Override
    public void setData(String data) {
        mTvData.setText(data);
    }

    @Override
    public void showView() {
        mRelativeLayout.setVisibility(View.VISIBLE);
    }
}