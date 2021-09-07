package com.ifcbrusque.app.activities.lembrete;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.AppDatabase;
import com.ifcbrusque.app.util.NotificationHelper;
import com.ifcbrusque.app.models.Lembrete;
import com.ifcbrusque.app.util.preferences.PreferencesHelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class InserirLembreteActivity extends AppCompatActivity implements InserirLembretePresenter.View, View.OnClickListener {
    public static final String EXTRAS_LEMBRETE_ID = "EXTRAS_LEMBRETE_ID";
    public static final String EXTRAS_LEMBRETE_ID_NOTIFICACAO = "EXTRAS_LEMBRETE_ID_NOTIFICACAO";
    public static final String EXTRAS_LEMBRETE_TITULO = "EXTRAS_LEMBRETE_TITULO";
    public static final String EXTRAS_LEMBRETE_DESCRICAO = "EXTRAS_LEMBRETE_DESCRICAO";
    public static final String EXTRAS_LEMBRETE_ADICIONADO = "EXTRAS_LEMBRETE_ADICIONADO";

    private InserirLembretePresenter presenter;

    TextInputLayout tiTitulo, tiDescricao;
    Button btnDatePicker, btnTimePicker;
    FloatingActionButton fabCompleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_lembrete);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tiTitulo = findViewById(R.id.tiTitulo);
        tiDescricao = findViewById(R.id.tiDescricao);
        btnDatePicker = findViewById(R.id.btData);
        btnTimePicker = findViewById(R.id.btHora);
        fabCompleto = findViewById(R.id.fabInserir);

        long idLembrete, idNotificacaoLembrete;
        if(getIntent().getExtras() != null) {
            idLembrete = getIntent().getExtras().getLong(EXTRAS_LEMBRETE_ID, -1);
            idNotificacaoLembrete = getIntent().getExtras().getLong(EXTRAS_LEMBRETE_ID_NOTIFICACAO, -1);
        } else {
            idLembrete = -1;
            idNotificacaoLembrete = -1;
        }

        presenter = new InserirLembretePresenter(this, AppDatabase.getDbInstance(this.getApplicationContext()), new PreferencesHelper(this), idLembrete, idNotificacaoLembrete);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        fabCompleto.setOnClickListener(this);
    }



    /*
    Implementar as funções de on click para os listeners que são definidos como this (como em btnTimePicker.setOnClickListener(this))
     */
    @Override
    public void onClick(View v) {
        //Clique no botão da data
        if(v == btnDatePicker) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            presenter.onDataSelecionada(year, monthOfYear, dayOfMonth);
                        }
                    }, presenter.getAno(), presenter.getMes(), presenter.getDia());
            datePickerDialog.show();
        }

        //Clique no botão da hora
        if(v == btnTimePicker) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            presenter.onTempoSelecionado(hourOfDay, minute);
                        }
                    }, presenter.getHora(), presenter.getMinuto(), true);
            timePickerDialog.show();
        }

        //Clique no botão de inserir
        if(v == fabCompleto) {
            String titulo = tiTitulo.getEditText().getText().toString();
            String descricao = tiDescricao.getEditText().getText().toString();
            presenter.onCliqueInserir(titulo, descricao);
        }
    }

    /*
    Executado quando algum item da barra de cima é selecionado

    Identifica o item e realiza os procedimentos correspondentes
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Botão de voltar (fecha a activity)
        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas no presenter para serem definidas por esta view
     */

    /**
     * Utilizado para mudar o texto do botão que abre o dialogo para selecionar a data
     * Exemplo:
     * ano = 1, mes = 1, dia = 1
     * Texto do botão -> 01/02/0001
     *
     * O mês é incrementado em um pois o Calendar utiliza números de 0 a 11 para identificá-lo
     */
    @Override
    public void mudarTextoBotaoData(int ano, int mes, int dia) {
        String texto = String.format("%02d", dia) + "/" + String.format("%02d", (mes+1)) + "/" + String.format("%04d", ano);
        btnDatePicker.setText(texto);
    }

    /**
     * Utilizado para mudar o texto do botão que abre o dialogo para selecionar a hora
     */
    @Override
    public void mudarTextoBotaoHora(int hora, int minuto) {
        String texto = String.format("%02d", hora) + ":" + String.format("%02d", minuto);
        btnTimePicker.setText(texto);
    }

    /**
     * Muda o texto do input do título
     */
    @Override
    public void setTitulo(String titulo) {
        tiTitulo.getEditText().setText(titulo);
    }

    /**
     * Muda o texto do input da descrição
     */
    @Override
    public void setDescricao(String descricao) {
        tiDescricao.getEditText().setText(descricao);
    }

    /**
     * Agenda a notificação do lembrete inserido
     * @param lembrete lembrete a ser agendado
     */
    @Override
    public void agendarNotificacaoLembrete(Lembrete lembrete) {
        NotificationHelper.agendarNotificacaoLembrete(this, lembrete);
    }

    /**
     * Utilizado para exibir um texto na tela através do toast
     * @param texto texto a ser exibido no toast
     */
    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    /**
     * Utilizado para fechar esta activity e atualizar o recycler view do HomeFragment
     * Esta função adiciona um valor no bundle de resultado que indica se o HomeFragment deve atualizar os itens do recycler view (carregar o que está salvo no banco de dados)
     * @param lembreteNovoInserido boolean que indica se foi armazenado algum lembrete novo. Caso seja true, o HomeFragment atualizará o recycler view de lembretes
     */
    @Override
    public void fecharActivity(boolean lembreteNovoInserido) {
        Intent intent = new Intent();
        intent.putExtra(EXTRAS_LEMBRETE_ADICIONADO, lembreteNovoInserido);
        setResult(RESULT_OK, intent);
        finish();
    }
}