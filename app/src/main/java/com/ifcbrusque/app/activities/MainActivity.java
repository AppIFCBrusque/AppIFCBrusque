package com.ifcbrusque.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ifcbrusque.app.helpers.PreferencesHelper;
import com.stacked.sigaa_ifc.*;


public class MainActivity extends AppCompatActivity {

    boolean inicializado = false;

    int requestCodeLogin = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferencesHelper pref = new PreferencesHelper(this.getApplicationContext());

        if(pref.getLoginSIGAA() != "" && pref.getSenhaSIGAA() != "") {
            //Já logado uma vez (ir para a home)
            Intent intentHome = new Intent(MainActivity.this, HomeActivity.class);
            MainActivity.this.startActivity(intentHome);

            //TODO

            //TODO: Inicializar caso não tenha

            //SIGAAHelper sigaa = new SIGAAHelper(this.getApplicationContext());
            //DatabaseManager db = new DatabaseManager(this.getApplicationContext());

            //(DELETAR DEPOIS) TESTE NOTICIAS
            /*
            NoticiasHelper noticias = new NoticiasHelper();
            noticias.getPaginaNoticias(14)
                    .doOnNext(previews -> {
                        for(Preview p : previews) {
                            System.out.println("NOTICIAS PREVIEW: " + p.getTitulo());
                        }
                        noticias.getNoticia(previews.get(1))
                        .doOnNext(noticia -> {
                            System.out.println("NOTICIAS NOTICIA: " + noticia.getTitulo() + "\n" + noticia.getHtml());
                        })
                        .subscribe();
                    })
                    .subscribe();*/

            /*(DELETAR DEEPOIS) TESTE SIGAA
            sigaa.getTodasAtividades()
                    .doOnNext(atv -> {
                        for(Tarefa t : atv.getTarefas()) {
                            System.out.println("Debug API: " + t.getTitulo());
                        }
                    })
                    .subscribe();*/

        } else {
            //Nunca logado/sessão encerrada (ir para o login)
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
                //TODO: Enviar o pacote para a home activity
            }
        }
    }
}