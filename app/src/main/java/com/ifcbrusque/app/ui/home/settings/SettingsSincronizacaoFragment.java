package com.ifcbrusque.app.ui.home.settings;

import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;

public class SettingsSincronizacaoFragment extends BasePreferenceFragment {
    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.sincronizacao);
        ImageButton ibFiltro = getActivity().findViewById(R.id.ibCategorias);
        ibFiltro.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {

    }
}