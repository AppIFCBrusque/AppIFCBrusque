package com.ifcbrusque.app.activities.lembrete;

import androidx.appcompat.app.AppCompatActivity;

import com.ifcbrusque.app.R;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class InserirLembreteActivity extends AppCompatActivity implements InserirLembretePresenter.View, View.OnClickListener {
    private InserirLembretePresenter presenter;

    Button btnDatePicker, btnTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_lembrete);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnDatePicker = findViewById(R.id.btData);
        btnTimePicker = findViewById(R.id.btHora);

        presenter = new InserirLembretePresenter(this);

        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);

        //TODO 1: Salvar lembretes
        //TODO 2: Abrir esta activity para editar um lembrete salvo
    }

    @Override
    public void onClick(View v) {
        //Clique no botão da data
        if (v == btnDatePicker) {
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
        if (v == btnTimePicker) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            presenter.onTempoSelecionado(hourOfDay, minute);
                        }
                    }, presenter.getHora(), presenter.getMinuto(), true);
            timePickerDialog.show();
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
}