package com.ifcbrusque.app.ui.settings;

import androidx.preference.PreferenceCategory;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_AVALIACOES_ALTERADAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_AVALIACOES_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_LEMBRETES;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_NOTICIAS_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_QUESTIONARIOS_ALTERADOS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_QUESTIONARIOS_NOVOS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_TAREFAS_ALTERADAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_TAREFAS_NOVAS;

public class SettingsNotificacoesFragment extends BasePreferenceFragment {
    @Override
    protected void setUp() {
        PreferenceCategory categoriaNotificar = inserirCategoria(R.string.notificar);
        inserirCheckBox(PREF_NOTIFICAR_LEMBRETES, R.string.notificar_lembretes, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_NOTICIAS_NOVAS, R.string.notificar_noticias_novas, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_AVALIACOES_NOVAS, R.string.notificar_avaliacoes_novas, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_AVALIACOES_ALTERADAS, R.string.notificar_avaliacoes_alteradas, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_TAREFAS_NOVAS, R.string.notificar_tarefas_novas, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_TAREFAS_ALTERADAS, R.string.notificar_tarefas_alteradas, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_QUESTIONARIOS_NOVOS, R.string.notificar_questionarios_novos, 0, true, categoriaNotificar);
        inserirCheckBox(PREF_NOTIFICAR_QUESTIONARIOS_ALTERADOS, R.string.notificar_questionarios_alterados, 0, true, categoriaNotificar);
    }
}