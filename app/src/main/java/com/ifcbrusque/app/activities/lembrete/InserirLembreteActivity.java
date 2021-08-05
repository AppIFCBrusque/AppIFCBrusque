package com.ifcbrusque.app.activities.lembrete;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.AppDatabase;

import android.app.AlertDialog;
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
    private InserirLembretePresenter presenter;

    TextInputLayout tiTitulo, tiDescricao;
    Button btnDatePicker, btnTimePicker;
    FloatingActionButton fabCompleto;

    public static final String EXTRAS_LEMBRETE_ADICIONADO = "EXTRAS_LEMBRETE_ADICIONADO";

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

        presenter = new InserirLembretePresenter(this, AppDatabase.getDbInstance(this.getApplicationContext()));

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        fabCompleto.setOnClickListener(this);

        //TODO 1: Abrir esta activity para editar um lembrete salvo
    }

    //Implementar as funções de on click
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Botão de voltar (fecha a activity)
        if(id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void mudarTextoBotaoData(int ano, int mes, int dia) {
        String texto = String.format("%02d", dia) + "/" + String.format("%02d", (mes+1)) + "/" + String.format("%04d", ano);
        btnDatePicker.setText(texto);
    }

    @Override
    public void mudarTextoBotaoHora(int hora, int minuto) {
        String texto = String.format("%02d", hora) + ":" + String.format("%02d", minuto);
        btnTimePicker.setText(texto);
    }

    @Override
    public void mostrarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void fecharActivity(boolean lembreteNovoInserido) {
        Intent intent = new Intent();
        intent.putExtra(EXTRAS_LEMBRETE_ADICIONADO, lembreteNovoInserido);
        setResult(RESULT_OK, intent);
        finish();
    }
}