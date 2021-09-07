package com.ifcbrusque.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.stacked.sigaa_ifc.*;

import static com.ifcbrusque.app.util.helpers.NotificationHelper.criarCanalNotificacoes;


public class MainActivity extends AppCompatActivity {
    final static public String TAG = "[APPIF]";

    boolean inicializado = false;

    int requestCodeLogin = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        criarCanalNotificacoes(this);

        Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
        MainActivity.this.startActivity(intentHome);

        /*if(pref.getLoginSIGAA() != "" && pref.getSenhaSIGAA() != "") {
            //Já logado uma vez (ir para a home)
            Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(intentHome);
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