package com.ifcbrusque.app.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.helpers.preferences.PreferencesHelper;
import com.stacked.sigaa_ifc.*;

import static com.ifcbrusque.app.helpers.NotificationsHelper.criarCanalNotificacoes;


public class MainActivity extends AppCompatActivity {
    final static public String TAG = "[APPIF]";

    boolean inicializado = false;

    int requestCodeLogin = 1;

    private Sessao sessao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        criarCanalNotificacoes(this);

        PreferencesHelper pref = new PreferencesHelper(this.getApplicationContext());

        Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
        MainActivity.this.startActivity(intentHome);

        //

        /*if(pref.getLoginSIGAA() != "" && pref.getSenhaSIGAA() != "") {
            //Já logado uma vez (ir para a home)
            Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(intentHome);

            //TODO: Inicializar caso não tenha
        } else {
            //Nunca logado/sessão encerrada (ir para o login)
            Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(intentHome);

            //Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            //MainActivity.this.startActivityForResult(intentLogin, requestCodeLogin);
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeLogin) {
            if(resultCode == RESULT_OK) {
                PacoteSessao pacote = (PacoteSessao) data.getSerializableExtra("pacote");
                //TODO: Enviar o pacote para a home activity
            }
        }
    }
}