package com.ifcbrusque.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stacked.sigaa_ifc.*;

import java.util.concurrent.Callable;

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
                logarSessao(etUsuario.getText().toString(), etSenha.getText().toString());
            }
        });
    }

    Sessao sessao = new Sessao("https://sig.ifc.edu.br/");

    private void logarSessao(String usuario, String senha) {
        Observable.fromCallable(new Callable<Usuario>() {
            @Override
            public Usuario call() throws ExcecaoAPI, ExcecaoSessaoExpirada, ExcecaoSIGAA {
                // RxJava does not accept null return value. Null will be treated as a failure.
                // So just make it return true.
                return sessao.login(usuario, senha);
            }
        }) // Execute in IO thread, i.e. background thread.
                .subscribeOn(Schedulers.io())
                // report or post the result to main thread.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<Usuario>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Usuario arrayList) {
                        //handling the result
                        ((TextView) findViewById(R.id.txNome)).setText(arrayList.getNome());
                    }

                    @Override
                    public void onError(Throwable e) {
                        //error handling made simple
                    }

                    @Override
                    public void onComplete() {
                        //cleaning up tasks
                    }
                });
    }

}