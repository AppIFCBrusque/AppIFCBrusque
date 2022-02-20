package com.ifcbrusque.app.ui.home;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.ui.base.BaseActivity;
import com.ifcbrusque.app.ui.home.lembretes.LembretesFragment;
import com.ifcbrusque.app.ui.home.noticias.NoticiasFragment;

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
    protected void setUp() {
        //Configuração da toolbar
        Toolbar toolbar = findViewById(R.id.toolbarHome);
        toolbar.setTitle(R.string.title_home);
        setSupportActionBar(toolbar);

        //Configuração do bottom navigation view
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int idTitulo = R.string.app_name;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new LembretesFragment();
                    idTitulo = R.string.title_home;
                    break;

                case R.id.navigation_noticias:
                    fragment = new NoticiasFragment();
                    idTitulo = R.string.title_noticias;
                    break;
            }

            if (fragment != null) {
                //Trocar para o fragmento
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .add(R.id.nav_host_fragment, fragment)
                        .commit();
                toolbar.setTitle(idTitulo);
                return true;
            } else {
                return false; //Retornar false não troca o item selecionado no bottom navigation view
            }
        });

    }

}