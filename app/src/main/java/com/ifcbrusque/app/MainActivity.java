package com.ifcbrusque.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.stacked.sigaa_ifc.PacoteSessao;
import com.stacked.sigaa_ifc.Sessao;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    boolean inicializado = false;

    int requestCodeLogin = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!inicializado) {
            Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivityForResult(intentLogin, requestCodeLogin);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == requestCodeLogin) {
            if(resultCode == RESULT_OK) {
                PacoteSessao pacote = (PacoteSessao) data.getSerializableExtra("pacote");
                Sessao sessao = new Sessao(pacote);
                System.out.println("Debug API: (main actv.) " + sessao.getUsuario().getNome());
            }
        }
    }
}