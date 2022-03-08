package com.ifcbrusque.app.ui.home.settings;

import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.preference.ListPreference;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_TEMA;
import static com.ifcbrusque.app.utils.ThemeUtils.getStringResIdTema;

public class SettingsAparenciaFragment extends BasePreferenceFragment {
    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.aparencia);
        ImageButton ibFiltro = getActivity().findViewById(R.id.image_button_filtros);
        ibFiltro.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {
        // ListPreference do tema
        String idTemaAtual = getPreferenceManager().getSharedPreferences().getString(PREF_TEMA, "0");
        ListPreference listPreferenceTema = inserirListPreference(PREF_TEMA, R.string.tema, idTemaAtual, R.array.temas, R.array.temas_values, null);
        listPreferenceTema.setOnPreferenceChangeListener((preference, newValue) -> {
            String idTemaNovo = (String) newValue;

            preference.setSummary(getStringResIdTema(idTemaNovo));

            // Reiniciar activity ao mudar o tema
            if (!idTemaAtual.equals(idTemaNovo)) {
                reiniciarActivity();
            }

            return true;
        });
    }

    private void reiniciarActivity() {
        startActivity(HomeActivity.getStartIntent(getContext(), R.id.navigation_configuracoes_aparencia));
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.hold_100);
        getActivity().finish();
    }
}