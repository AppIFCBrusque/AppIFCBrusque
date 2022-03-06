package com.ifcbrusque.app.ui.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.ifcbrusque.app.R;

import static com.ifcbrusque.app.utils.AppConstants.PREF_NAME;
import static com.ifcbrusque.app.utils.ThemeUtils.getStringResIdTema;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {
    private boolean setUpDone = false;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        getPreferenceManager().setSharedPreferencesName(PREF_NAME);
        getPreferenceManager().setSharedPreferencesMode(Context.MODE_PRIVATE);
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Definir a cor do fundo
        TypedValue typedValue = new TypedValue();
        Resources.Theme tema = getContext().getTheme();
        tema.resolveAttribute(R.attr.backgroundColor, typedValue, true);
        @ColorInt int cor = typedValue.data;
        getView().setBackgroundColor(cor);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!setUpDone) {
            setUp();
            setUpDone = true;
        }
    }

    public void inserirPreferencias(String fragmento, String key, int icone, int titulo) {
        Preference preference = new Preference(getContext());
        preference.setFragment(fragmento);
        preference.setKey(key);
        preference.setIcon(icone);
        preference.setTitle(titulo);
        getPreferenceScreen().addPreference(preference);
    }

    public PreferenceCategory inserirCategoria(int titulo) {
        PreferenceCategory preferenceCategory = new PreferenceCategory(getContext());
        preferenceCategory.setTitle(titulo);
        preferenceCategory.setIconSpaceReserved(false);
        getPreferenceScreen().addPreference(preferenceCategory);
        return preferenceCategory;
    }

    public SwitchPreference inserirSwitch(String key, int titulo, int sumario, boolean checado, PreferenceCategory preferenceCategory) {
        SwitchPreference switchPreference = new SwitchPreference(getContext());

        switchPreference.setKey(key);
        switchPreference.setTitle(titulo);
        switchPreference.setChecked(checado);
        switchPreference.setIconSpaceReserved(false);

        if (sumario != 0) {
            switchPreference.setSummary(sumario);
        }

        if (preferenceCategory == null) {
            getPreferenceScreen().addPreference(switchPreference);
        } else {
            preferenceCategory.addPreference(switchPreference);
        }

        return switchPreference;
    }

    public ListPreference inserirListPreference(String key, int titleResId, String idTemaAtual, @ArrayRes int entriesResId, @ArrayRes int entryValuesResId, PreferenceCategory preferenceCategory) {
        ListPreference listPreference = new ListPreference(getContext());

        listPreference.setKey(key);
        listPreference.setTitle(titleResId);
        listPreference.setSummary(getStringResIdTema(idTemaAtual));
        listPreference.setDialogTitle(titleResId);
        listPreference.setEntries(entriesResId);
        listPreference.setEntryValues(entryValuesResId);
        listPreference.setValue(idTemaAtual);
        listPreference.setIconSpaceReserved(false);

        if (preferenceCategory == null) {
            getPreferenceScreen().addPreference(listPreference);
        } else {
            preferenceCategory.addPreference(listPreference);
        }

        return listPreference;
    }

    protected abstract void setUp();
}
