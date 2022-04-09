package com.ifcbrusque.app.ui.home.sigaa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.di.component.ActivityComponent;
import com.ifcbrusque.app.ui.base.BaseFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;

import javax.inject.Inject;

public class SIGAAFragment extends BaseFragment implements SIGAAContract.SIGAAView {
    @Inject
    SIGAAContract.SIGAAPresenter<SIGAAContract.SIGAAView> mPresenter;

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
        actionBar.setTitle(R.string.title_sigaa);
        ImageButton ibFiltros = getBaseActivity().findViewById(R.id.image_button_filtros);
        ibFiltros.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {

    }
}