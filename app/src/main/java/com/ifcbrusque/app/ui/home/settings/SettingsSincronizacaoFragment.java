package com.ifcbrusque.app.ui.home.settings;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BasePreferenceFragment;
import com.ifcbrusque.app.ui.home.HomeActivity;
import com.ifcbrusque.app.ui.login.LoginActivity;

import static android.app.Activity.RESULT_OK;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.PREF_SINCRONIZAR_SIGAA;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_CONECTADO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_LOGIN;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_NOME_DO_USUARIO;
import static com.ifcbrusque.app.data.prefs.PreferenceValues.SIGAA_SENHA;

public class SettingsSincronizacaoFragment extends BasePreferenceFragment {
    private AlertDialog mAlertDialog;
    private SharedPreferences mSharedPreferences;

    private SwitchPreference mSwitchSincronizarSIGAA;
    private Preference mPreferenceContaConectada;

    @Override
    public void onStart() {
        super.onStart();

        // Configuração da toolbar
        ActionBar actionBar = ((HomeActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.sincronizacao);
        ImageButton ibFiltro = getActivity().findViewById(R.id.image_button_filtros);
        ibFiltro.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado do LoginActivity
        if (resultCode == RESULT_OK) {
            reiniciarActivity();
        }
    }

    @Override
    protected void setUp() {
        mSharedPreferences = getPreferenceManager().getSharedPreferences();

        mSwitchSincronizarSIGAA = inserirSwitch(PREF_SINCRONIZAR_SIGAA, R.string.sincronizar_sigaa, R.string.sincronizar_sigaa_descricao, false, null);

        // Preference que mostra a conta conectada
        if (mSharedPreferences.getBoolean(SIGAA_CONECTADO, false)) {
            String tituloConectado = String.format(getString(R.string.sincronizar_sigaa_conectado_como), mSharedPreferences.getString(SIGAA_NOME_DO_USUARIO, ""));
            mPreferenceContaConectada = inserirPreference(SIGAA_CONECTADO, tituloConectado, getString(R.string.sincronizar_sigaa_conectado_como_descricao), null);
        } else {
            mPreferenceContaConectada = inserirPreference(SIGAA_CONECTADO, R.string.sincronizar_sigaa_nao_conectado, R.string.sincronizar_sigaa_nao_conectado_descricao, null);
        }

        mPreferenceContaConectada.setOnPreferenceClickListener(preference -> {
            if (mSharedPreferences.getBoolean(SIGAA_CONECTADO, false)) {
                mostrarDialogoDeslogar();
            } else {
                startActivityForResult(LoginActivity.getStartIntent(getContext(), true), 1);
            }
            return true;
        });

        mPreferenceContaConectada.setVisible(mSwitchSincronizarSIGAA.isChecked());

        mSwitchSincronizarSIGAA.setOnPreferenceChangeListener((preference, newValue) -> {
            mPreferenceContaConectada.setVisible((boolean) newValue);
            return true;
        });
    }

    private void mostrarDialogoDeslogar() {
        if (mAlertDialog != null) {
            // Esconder o AlertDialog caso já exista
            mAlertDialog.hide();
        }

        mAlertDialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.sincronizar_sigaa_conectado_deslogar_mensagem)
                .setPositiveButton(R.string.sincronizar_sigaa_conectado_deslogar, (dialog, id) -> deslogarSIGAA())
                .setNegativeButton(R.string.cancel, (dialog, id) -> mAlertDialog = null)
                .create();

        mAlertDialog.show();
    }

    private void deslogarSIGAA() {
        mSharedPreferences.edit()
                .putBoolean(SIGAA_CONECTADO, false)
                .putString(SIGAA_LOGIN, "")
                .putString(SIGAA_SENHA, "")
                .putString(SIGAA_NOME_DO_USUARIO, "")
                .apply();

        reiniciarActivity();
    }

    private void reiniciarActivity() {
        startActivity(HomeActivity.getStartIntent(getContext(), R.id.navigation_configuracoes_sincronizacao));
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.hold_100);
        getActivity().finish();
    }
}