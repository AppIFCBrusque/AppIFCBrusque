package com.ifcbrusque.app.ui.settings;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;

public class SettingsFragment extends BasePreferenceFragment {
    @Override
    protected void setUp() {
        inserirPreferencias("com.ifcbrusque.app.ui.settings.SettingsAparenciaFragment", SettingsAparenciaFragment.class.toString(), R.drawable.baseline_palette_black_24, R.string.aparencia);
        inserirPreferencias("com.ifcbrusque.app.ui.settings.SettingsNotificacoesFragment", SettingsNotificacoesFragment.class.toString(), R.drawable.ic_notifications_black_24dp, R.string.notificacoes);
    }
}