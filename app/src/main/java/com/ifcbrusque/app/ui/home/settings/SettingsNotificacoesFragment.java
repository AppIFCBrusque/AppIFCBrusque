package com.ifcbrusque.app.ui.home.settings;

import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceCategory;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;

import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_AVALIACOES_ALTERADAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_AVALIACOES_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_LEMBRETES;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_NOTICIAS_DO_CAMPUS_NOVAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_QUESTIONARIOS_ALTERADOS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_QUESTIONARIOS_NOVOS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_TAREFAS_ALTERADAS;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_NOTIFICAR_TAREFAS_NOVAS;

public class SettingsNotificacoesFragment extends BasePreferenceFragment {
    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.notificacoes);
        ImageButton ibFiltro = getActivity().findViewById(R.id.ibCategorias);
        ibFiltro.setVisibility(View.GONE);
    }

    @Override
    protected void setUp() {
        // Opções
        PreferenceCategory categoriaNotificar = inserirCategoria(R.string.notificar);
        inserirSwitch(PREF_NOTIFICAR_LEMBRETES, R.string.notificar_lembretes, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_NOTICIAS_DO_CAMPUS_NOVAS, R.string.notificar_noticias_do_campus_novas, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_AVALIACOES_NOVAS, R.string.notificar_avaliacoes_novas, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_AVALIACOES_ALTERADAS, R.string.notificar_avaliacoes_alteradas, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_TAREFAS_NOVAS, R.string.notificar_tarefas_novas, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_TAREFAS_ALTERADAS, R.string.notificar_tarefas_alteradas, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_QUESTIONARIOS_NOVOS, R.string.notificar_questionarios_novos, 0, true, categoriaNotificar);
        inserirSwitch(PREF_NOTIFICAR_QUESTIONARIOS_ALTERADOS, R.string.notificar_questionarios_alterados, 0, true, categoriaNotificar);
    }
}