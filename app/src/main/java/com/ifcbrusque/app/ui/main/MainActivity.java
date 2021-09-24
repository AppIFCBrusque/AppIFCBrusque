package com.ifcbrusque.app.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.HomeActivity;

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
    public void abrirHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void fecharActivity() {
        finish();
    }
}