package com.ifcbrusque.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stacked.sigaa_ifc.*;

import java.util.concurrent.Callable;
import java.util.function.Function;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsuario = findViewById(R.id.etUsuario);
        EditText etSenha = findViewById(R.id.etSenha);
        Button btEntrar = findViewById(R.id.btEntrar);

        btEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(etUsuario.getText().toString(), etSenha.getText().toString());
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////
    Sessao sessao = new Sessao("https://sig.ifc.edu.br/");
    boolean retornoLogin = false;

    private void login(String usuario, String senha) {
        Observable.defer(() -> {
            return Observable.just(sessao.login(usuario, senha));
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    handleLoginError(throwable);
                })
                .doOnNext(logado -> {
                    retornoLogin = logado;
                })
                .doOnComplete(() -> {
                    handleLoginReturn();
                }).subscribe();
    }

    private void handleLoginError(Throwable e) {
        System.out.println("Debug API: " + e.getMessage());

        if(e.getClass() == ExcecaoSIGAA.class) {
            //SIGAA em manutenção, provavelmente
            System.out.println("Debug API: sig manutencao");
        } else if(e.getClass() == ExcecaoAPI.class) {
            //Problema na API
            System.out.println("Debug API: problema api");
        }
    }

    private void handleLoginReturn() {
        if(retornoLogin) {
            Intent intent = new Intent();
            intent.putExtra("pacote", sessao.empacotarSessao());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            //Usuário ou senha incorretos
            System.out.println("Debug API: usuario ou senha inc");
        }
    }

}

