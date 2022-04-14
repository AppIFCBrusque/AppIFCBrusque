package com.ifcbrusque.app.ui.home.sigaa.noticias;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.NoticiaArmazenavel;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.lembretes.StickyHeaderDecoration;

import java.util.List;

import javax.inject.Inject;

public class NoticiasSIGAAActivity extends BaseActivity implements NoticiasSIGAAContract.NoticiasSIGAAView {
    @Inject
    NoticiasSIGAAContract.NoticiasSIGAAPresenter<NoticiasSIGAAContract.NoticiasSIGAAView> mPresenter;
    @Inject
    NoticiasSIGAAAdapter mNoticiasSIGAAAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, NoticiasSIGAAActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias_sigaa);
        getActivityComponent().inject(this);
        mPresenter.onAttach(NoticiasSIGAAActivity.this);
        setUp();
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
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
    protected void setUp() {
        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_noticias);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurar recycler view
        mRecyclerView = findViewById(R.id.noticias_sigaa_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setAdapter(mNoticiasSIGAAAdapter);
        mRecyclerView.addItemDecoration(new StickyHeaderDecoration(mNoticiasSIGAAAdapter));

        NoticiaSIGAAItemListener itemListener = position -> {
            // TODO
        };
        mNoticiasSIGAAAdapter.setItemListener(itemListener);

        mPresenter.onViewPronta();
    }

    @Override
    public void setNoticiasArmazenaveis(List<NoticiaArmazenavel> noticiasArmazenaveis) {
        mNoticiasSIGAAAdapter.setNoticiasSIGAA(noticiasArmazenaveis);
    }
}
