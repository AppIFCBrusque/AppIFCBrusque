package com.ifcbrusque.app.ui.home.settings;

import androidx.appcompat.app.ActionBar;
import androidx.preference.ListPreference;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_TEMA;
import static com.ifcbrusque.app.utils.ThemeUtils.getStringResIdTema;

public class SettingsAparenciaFragment extends BasePreferenceFragment {

    @Override
    protected void setUp() {
        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.aparencia);

        // ListPreference do tema
        String idTemaAtual = getPreferenceManager().getSharedPreferences().getString(PREF_TEMA, "0");
        ListPreference listPreferenceTema = inserirListPreference(PREF_TEMA, R.string.tema, idTemaAtual, R.array.temas, R.array.temas_values, null);
        listPreferenceTema.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary(getStringResIdTema((String) newValue));
            return true;
        });
    }
}