package com.ifcbrusque.app.ui.noticia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Preview;
import com.ifcbrusque.app.ui.base.BaseActivity;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static com.ifcbrusque.app.data.db.Converters.dateToTimestamp;

public class NoticiaActivity extends BaseActivity implements NoticiaContract.NoticiaView {
    @Inject
    NoticiaContract.NoticiaPresenter<NoticiaContract.NoticiaView> mPresenter;

    private WebView mWv;
    private CircularProgressIndicator mPb;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWv = findViewById(R.id.wvNoticia);
        mPb = findViewById(R.id.pbCircularNoticia);

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
    public void carregarHtmlWebView(String html) {
        mWv.loadData(html, "text/html", "UTF-8");
    }

    @Override
    public void mostrarLoading() {
        mPb.show();
    }

    @Override
    public void esconderLoading() {
        mPb.hide();
    }
}