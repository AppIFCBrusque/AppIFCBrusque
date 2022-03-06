package com.ifcbrusque.app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.HomeActivity;

import javax.inject.Inject;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class LoginActivity extends BaseActivity implements LoginContract.LoginView {
    public static String EXTRAS_PODE_VOLTAR = "EXTRAS_PODE_VOLTAR";

    public static Intent getStartIntent(Context context, boolean podeVoltar) {
        Intent intent = new Intent(context, LoginActivity.class);

        intent.putExtra(EXTRAS_PODE_VOLTAR, podeVoltar);

        return intent;
    }

    @Inject
    LoginContract.LoginPresenter<LoginContract.LoginView> mPresenter;

    private MaterialProgressBar mProgressBar;
    private TextInputLayout mEtUsuario, mEtSenha;
    private TextView mTvErro;
    private Button mBtEntrar, mBtPular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_IFCBrusque_SplashTheme);
        setContentView(R.layout.activity_login);

        getActivityComponent().inject(this);

        mPresenter.onAttach(LoginActivity.this);

        setUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Botão de voltar
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setUp() {
        Toolbar toolbar = findViewById(R.id.toolbarLogin);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        mProgressBar = findViewById(R.id.pbHorizontalLogin);
        mEtUsuario = findViewById(R.id.etUsuario);
        mEtSenha = findViewById(R.id.etSenha);
        mTvErro = findViewById(R.id.tvErro);
        mBtEntrar = findViewById(R.id.btEntrar);
        mBtPular = findViewById(R.id.btPular);

        mBtEntrar.setOnClickListener(v -> mPresenter.onEntrarClick(mEtUsuario.getEditText().getText().toString(), mEtSenha.getEditText().getText().toString()));
        mBtPular.setOnClickListener(v -> mPresenter.onPularClick());

        if (getIntent().getExtras().getBoolean(EXTRAS_PODE_VOLTAR, false)) {
            esconderBotaoPular();
            mostrarVoltar();
        }
    }

    @Override
    public void desativarBotaoEntrar() {
        mBtEntrar.setEnabled(false);
    }

    @Override
    public void ativarBotaoEntrar() {
        mBtEntrar.setEnabled(true);
    }

    @Override
    public void esconderBotaoPular() {
        mBtPular.setVisibility(View.GONE);
    }

    @Override
    public void mostrarLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void esconderLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void mostrarVoltar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void mostrarMensagemErro() {
        mTvErro.setVisibility(View.VISIBLE);
    }

    @Override
    public void setMensagemErro(int resid) {
        mTvErro.setText(resid);
    }

    @Override
    public void abrirHome() {
        startActivity(HomeActivity.getStartIntent(this, R.id.navigation_home));
    }

    @Override
    public void fecharActivity() {
        finish();
    }
}
