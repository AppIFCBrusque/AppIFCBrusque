package com.ifcbrusque.app.ui.settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_TEMA;
import static com.ifcbrusque.app.utils.ThemeUtils.getStringResIdTema;

public class SettingsAparenciaFragment extends BasePreferenceFragment {
    @Override
    protected void setUp() {
        // ListPreference do tema
        String idTemaAtual = getPreferenceManager().getSharedPreferences().getString(PREF_TEMA, "0");
        ListPreference listPreferenceTema = inserirListPreference(PREF_TEMA, R.string.tema, idTemaAtual, R.array.temas, R.array.temas_values, null);
        listPreferenceTema.setOnPreferenceChangeListener((preference, newValue) -> {
            preference.setSummary(getStringResIdTema((String) newValue));
            return true;
        });
    }
}