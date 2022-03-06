package com.ifcbrusque.app.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.HomeActivity;

import javax.inject.Inject;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class LoginActivity extends BaseActivity implements LoginContract.LoginView {
    @Inject
    LoginContract.LoginPresenter<LoginContract.LoginView> mPresenter;

    private MaterialProgressBar mProgressBar;
    private TextInputLayout mEtUsuario, mEtSenha;
    private TextView mTvErro;
    private Button mBtEntrar, mBtPular;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

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
    protected void setUp() {
        mProgressBar = findViewById(R.id.pbHorizontalLogin);
        mEtUsuario = findViewById(R.id.etUsuario);
        mEtSenha = findViewById(R.id.etSenha);
        mTvErro = findViewById(R.id.tvErro);
        mBtEntrar = findViewById(R.id.btEntrar);
        mBtPular = findViewById(R.id.btPular);

        mBtEntrar.setOnClickListener(v -> mPresenter.onEntrarClick(mEtUsuario.getEditText().getText().toString(), mEtSenha.getEditText().getText().toString()));
        mBtPular.setOnClickListener(v -> mPresenter.onPularClick());
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
    public void mostrarLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void esconderLoading() {
        mProgressBar.setVisibility(View.GONE);
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
