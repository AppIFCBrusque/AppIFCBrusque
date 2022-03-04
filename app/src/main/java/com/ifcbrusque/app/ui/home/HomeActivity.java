package com.ifcbrusque.app.ui.home;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.lembretes.LembretesFragment;
import com.ifcbrusque.app.ui.home.noticias.NoticiasFragment;
import com.ifcbrusque.app.ui.home.settings.SettingsFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class HomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getActivityComponent().inject(this);

        setUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Botão de voltar
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            // Finalizar activity
            finish();
        } else {
            // Voltar ao fragmento anterior
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    protected void setUp() {
        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);

        // Configuração do bottom navigation view
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new LembretesFragment();
                    break;

                case R.id.navigation_noticias:
                    fragment = new NoticiasFragment();
                    break;

                case R.id.navigation_configuracoes:
                    fragment = new SettingsFragment();
                    break;
            }

            if (fragment != null) {
                // Trocar para o fragmento
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.nav_host_fragment, fragment)
                        .commit();
                return true;
            } else {
                return false; // Retornar false não troca o item selecionado no bottom navigation view
            }
        });
    }

}