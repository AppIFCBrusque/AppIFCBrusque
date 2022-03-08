package com.ifcbrusque.app.ui.noticia;

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
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static com.ifcbrusque.app.data.db.Converters.dateToTimestamp;
import static com.ifcbrusque.app.utils.ThemeUtils.getCorEmHex;

public class NoticiaActivity extends BaseActivity implements NoticiaContract.NoticiaView {
    @Inject
    NoticiaContract.NoticiaPresenter<NoticiaContract.NoticiaView> mPresenter;
    @Inject
    Picasso mPicasso;

    private WebView mWv;
    private ShapeableImageView mImg;
    private TextView mTvTitulo, mTvData;
    private RelativeLayout mRelativeLayout;

    /*
    Chaves do Bundle
     */
    public static final String NOTICIA_TITULO = "NOTICIA_TITULO";
    public static final String NOTICIA_DATA = "NOTICIA_DATA";
    public static final String NOTICIA_URL = "NOTICIA_URL";
    public static final String NOTICIA_URL_IMAGEM_PREVIEW = "NOTICIA_URL_IMAGEM_PREVIEW";

    public static Intent getStartIntent(Context context, Preview preview) {
        Intent intent = new Intent(context, NoticiaActivity.class);

        intent.putExtra(NOTICIA_TITULO, preview.getTitulo());
        intent.putExtra(NOTICIA_DATA, dateToTimestamp(preview.getDataNoticia()));
        intent.putExtra(NOTICIA_URL, preview.getUrlNoticia());
        intent.putExtra(NOTICIA_URL_IMAGEM_PREVIEW, preview.getUrlImagemPreview());

        return intent;
    }

    /*
    Implementar as funções de on click para os listeners são definidos como this (como em btnTimePicker.setOnClickListener(this))
     */
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
        //Ativar botão de voltar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWv = findViewById(R.id.wvNoticia);
        mImg = findViewById(R.id.noticia_imagem_grande);
        mTvTitulo = findViewById(R.id.noticia_titulo);
        mTvData = findViewById(R.id.noticia_data);
        mRelativeLayout = findViewById(R.id.noticia_relative_layout);

        mWv.setVerticalScrollBarEnabled(false);

        mPresenter.onViewPronta(getIntent().getExtras());
    }

    /*
    Executado quando algum item da barra de cima é selecionado

    Identifica o item e realiza os procedimentos correspondentes
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Botão de voltar (fecha a activity)
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas no presenter para serem definidas por esta view
     */

    /**
     * Utilizado para carregar um HTML no Web View
     *
     * @param html HTML a ser carregado
     */
    @Override
    public void carregarHtmlWebView(String html, int idTema) {
        String htmlComTema = adicionarCoresDoTemaAoHTML(html, idTema);
        mWv.loadDataWithBaseURL(null, htmlComTema, "text/html", "UTF-8", null);
        mWv.setVisibility(View.VISIBLE);
    }

    private String adicionarCoresDoTemaAoHTML(String html, int tema) {
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
    public void carregarImagemGrande(String url) {
        if (url.equals("") || url.length() == 0) {
            //Sem imagem
            mImg.setImageResource(R.drawable.splash_background);
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            mPicasso.load(url)
                    .resize(width, 0)
                    .into(mImg);
        }
    }

    @Override
    public void setTitulo(String titulo) {
        mTvTitulo.setText(titulo);
    }

    @Override
    public void setData(String data) {
        mTvData.setText(data);
    }

    @Override
    public void mostrarView() {
        mRelativeLayout.setVisibility(View.VISIBLE);
    }
}