package com.ifcbrusque.app.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.HomeActivity;
import com.ifcbrusque.app.ui.login.LoginActivity;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainContract.MainView {
    @Inject
    MainContract.MainPresenter<MainContract.MainView> mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivityComponent().inject(this);

        mPresenter.onAttach(MainActivity.this);

        setUp();
    }

    @Override
    protected void setUp() {
        mPresenter.onViewPronta();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void abrirLogin() {
        Intent intent = LoginActivity.getStartIntent(this, false);
        startActivity(intent);
    }

    @Override
    public void abrirHome() {
        startActivity(HomeActivity.getStartIntent(this, R.id.navigation_lembretes));
    }

    @Override
    public void fecharActivity() {
        finish();
    }
}