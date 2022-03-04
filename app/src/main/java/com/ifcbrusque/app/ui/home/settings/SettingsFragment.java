package com.ifcbrusque.app.ui.home.settings;

import android.view.View;
import android.widget.ImageButton;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;

public class SettingsFragment extends BasePreferenceFragment {
    @Override
    protected void setUp() {
        // Esconder botão de filtros
        ImageButton ibFiltro = getActivity().findViewById(R.id.ibCategorias);
        ibFiltro.setVisibility(View.GONE);

        inserirPreferencias("com.ifcbrusque.app.ui.home.settings.SettingsAparenciaFragment", SettingsAparenciaFragment.class.toString(), R.drawable.baseline_palette_black_24, R.string.aparencia);
        inserirPreferencias("com.ifcbrusque.app.ui.home.settings.SettingsNotificacoesFragment", SettingsNotificacoesFragment.class.toString(), R.drawable.ic_notifications_black_24dp, R.string.notificacoes);
    }
}