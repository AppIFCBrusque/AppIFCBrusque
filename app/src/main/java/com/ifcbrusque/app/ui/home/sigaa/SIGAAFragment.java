package com.ifcbrusque.app.ui.home.sigaa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.android.material.imageview.ShapeableImageView;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.component.ActivityComponent;
import com.ifcbrusque.app.ui.base.BaseFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class SIGAAFragment extends BaseFragment implements SIGAAContract.SIGAAView {
    @Inject
    SIGAAContract.SIGAAPresenter<SIGAAContract.SIGAAView> mPresenter;
    @Inject
    Picasso mPicasso;
    private TextView mTvNome, mTvCurso;
    private ShapeableImageView mIvAvatarSIGAA;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sigaa, container, false);

        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            mPresenter.onAttach(this);
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setElevation(0);
        actionBar.setTitle(R.string.title_sigaa);
        ImageButton ibFiltros = getBaseActivity().findViewById(R.id.image_button_filtros);
        ibFiltros.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {
        mTvNome = getView().findViewById(R.id.sigaa_nome);
        mTvCurso = getView().findViewById(R.id.sigaa_curso);
        mIvAvatarSIGAA = getView().findViewById(R.id.sigaa_avatar);

        mPresenter.onViewPronta();
    }

    @Override
    public void setNomeText(String string) {
        mTvNome.setText(string);
    }

    @Override
    public void setCursoText(String string) {
        mTvCurso.setText(string);
    }

    @Override
    public void setAvatarSIGAA(String url) {
        mPicasso.load(url)
                .fit()
                .centerCrop()
                .into(mIvAvatarSIGAA);
    }
}