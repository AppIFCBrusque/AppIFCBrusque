package com.ifcbrusque.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.lembretes.LembretesFragment;
import com.ifcbrusque.app.ui.home.noticias.NoticiasFragment;
import com.ifcbrusque.app.ui.home.settings.SettingsAparenciaFragment;
import com.ifcbrusque.app.ui.home.settings.SettingsFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class HomeActivity extends BaseActivity {
    public static final String EXTRAS_ID_ABA_INICIAL = "EXTRAS_ID_ABA_INICIAL";

    public static Intent getStartIntent(Context context, int idAbaInicial) {
        Intent intent = new Intent(context, HomeActivity.class);

        intent.putExtra(EXTRAS_ID_ABA_INICIAL, idAbaInicial);

        return intent;
    }

    private BottomNavigationView mBottomNavigationView;

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

    private boolean onSelectedNavigationItemChanged(int itemId) {
        Fragment fragment = null;

        switch (itemId) {
            default:
            case R.id.navigation_home:
                fragment = new LembretesFragment();
                break;

            case R.id.navigation_noticias:
                fragment = new NoticiasFragment();
                break;

            case R.id.navigation_configuracoes:
                fragment = new SettingsFragment();
                break;

            case R.id.navigation_configuracoes_notificacoes:
                // Definir as configurações como a aba atual da navigation bar para poder voltar
                mBottomNavigationView.setSelectedItemId(R.id.navigation_configuracoes);

                // Abrir o fragmento de configurações no fundo e o da aparência na frente
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.nav_host_fragment, new SettingsFragment())
                        .add(R.id.nav_host_fragment, new SettingsAparenciaFragment())
                        .addToBackStack(SettingsAparenciaFragment.class.getName())
                        .commit();
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
    }

    @Override
    protected void setUp() {
        // Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);

        // Configuração do bottom navigation view
        int idAbaInicial = getIntent().getExtras().getInt(EXTRAS_ID_ABA_INICIAL, R.id.navigation_home);

        mBottomNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView.setOnItemSelectedListener(item -> onSelectedNavigationItemChanged(item.getItemId()));
        mBottomNavigationView.setSelectedItemId(idAbaInicial);
        onSelectedNavigationItemChanged(idAbaInicial);
    }
}