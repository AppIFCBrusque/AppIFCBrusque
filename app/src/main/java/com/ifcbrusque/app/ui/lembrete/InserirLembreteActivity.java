package com.ifcbrusque.app.ui.lembrete;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.BaseActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import javax.inject.Inject;

import static com.ifcbrusque.app.utils.ViewUtils.bsdAddDescricaoBelow;
import static com.ifcbrusque.app.utils.ViewUtils.bsdAddOpcaoBelow;

public class InserirLembreteActivity extends BaseActivity implements InserirLembreteContract.InserirLembreteView {
    public static final String EXTRAS_LEMBRETE_ID = "EXTRAS_LEMBRETE_ID";
    public static final String EXTRAS_LEMBRETE_ID_NOTIFICACAO = "EXTRAS_LEMBRETE_ID_NOTIFICACAO";
    public static final String EXTRAS_LEMBRETE_TITULO = "EXTRAS_LEMBRETE_TITULO";
    public static final String EXTRAS_LEMBRETE_DESCRICAO = "EXTRAS_LEMBRETE_DESCRICAO";
    public static final String EXTRAS_LEMBRETE_TIPO_REPETICAO = "EXTRAS_LEMBRETE_TIPO_REPETICAO";
    public static final String EXTRAS_LEMBRETE_TEMPO_REPETICAO_PERSONALIZADA = "EXTRAS_LEMBRETE_TEMPO_REPETICAO_PERSONALIZADA";
    public static final String EXTRAS_ATUALIZAR_RECYCLER_VIEW = "EXTRAS_ATUALIZAR_RECYCLER_VIEW";


    public static Intent getStartIntent(Context context) {
        return getStartIntent(context, null);
    }

    public static Intent getStartIntent(Context context, Lembrete lembrete) {
        Intent intent = new Intent(context, InserirLembreteActivity.class);

        if (lembrete != null) {
            intent.putExtra(EXTRAS_LEMBRETE_ID, lembrete.getId());
            intent.putExtra(EXTRAS_LEMBRETE_ID_NOTIFICACAO, lembrete.getIdNotificacao());
        }

        return intent;
    }

    @Inject
    InserirLembreteContract.InserirLembretePresenter<InserirLembreteContract.InserirLembreteView> mPresenter;

    TextInputLayout mTiTitulo, mTiDescricao;
    Button mBtnDatePicker, mBtnTimePicker, mBtnRepeticao;
    FloatingActionButton mFabSalvar;

    BottomSheetDialog mBottomSheetDialog;
    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_lembrete);

        getActivityComponent().inject(this);

        mPresenter.onAttach(InserirLembreteActivity.this);

        setUp();
    }

    @Override
    protected void setUp() {
        Toolbar toolbar = findViewById(R.id.toolbarInserirLembrete);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTiTitulo = findViewById(R.id.tiTitulo);
        mTiDescricao = findViewById(R.id.tiDescricao);
        mBtnDatePicker = findViewById(R.id.btData);
        mBtnTimePicker = findViewById(R.id.btHora);
        mBtnRepeticao = findViewById(R.id.btRepeticao);
        mFabSalvar = findViewById(R.id.fabInserir);

        mBtnDatePicker.setOnClickListener(v -> mPresenter.onBotaoDataClick());
        mBtnTimePicker.setOnClickListener(v -> mPresenter.onBotaoTempoClick());
        mBtnRepeticao.setOnClickListener(v -> mPresenter.onBotaoRepeticaoClick());
        mFabSalvar.setOnClickListener(v -> mPresenter.onBotaoSalvarClick());

        long idLembrete = -1;
        if (getIntent().getExtras() != null) {
            idLembrete = getIntent().getExtras().getLong(EXTRAS_LEMBRETE_ID, -1);
        }
        mPresenter.onViewPronta(idLembrete);
    }

    /*
    Executado quando algum item da barra de cima é selecionado

    Identifica o item e realiza os procedimentos correspondentes
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Botão de voltar (fecha a activity)
        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mPresenter.onDetach();
        super.onDestroy();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções declaradas no contract para serem definidas por esta view
     */

    @Override
    public void exibirDialogoData(int anoInicial, int mesInicial, int diaInicial) {
        if (mDatePickerDialog != null) {
            mDatePickerDialog.dismiss();
        }

        mDatePickerDialog = new DatePickerDialog(this,
                (DatePickerDialog.OnDateSetListener) (view, year, monthOfYear, dayOfMonth) -> mPresenter.onDataSelecionada(year, monthOfYear, dayOfMonth),
                anoInicial,
                mesInicial,
                diaInicial);
        mDatePickerDialog.show();
    }

    @Override
    public void exibirDialogoTempo(int horaInicial, int minutoInicial) {
        if (mTimePickerDialog != null) {
            mTimePickerDialog.dismiss();
        }

        mTimePickerDialog = new TimePickerDialog(this,
                (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> mPresenter.onTempoSelecionado(hourOfDay, minute),
                horaInicial,
                minutoInicial,
                true);
        mTimePickerDialog.show();
    }

    @Override
    public void exibirDialogoRepeticao() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);

        RelativeLayout rlBottomSheetDialog = mBottomSheetDialog.findViewById(R.id.rlBottomSheetDialog);

        TextView tvRepeticao = bsdAddDescricaoBelow(this, R.string.repeticao, rlBottomSheetDialog, null);
        TextView tvHora = bsdAddOpcaoBelow(this, R.string.repeticao_lembretes_hora, 0, rlBottomSheetDialog, tvRepeticao);
        TextView tvDia = bsdAddOpcaoBelow(this, R.string.repeticao_lembretes_dia, 0, rlBottomSheetDialog, tvHora);
        TextView tvSemana = bsdAddOpcaoBelow(this, R.string.repeticao_lembretes_semana, 0, rlBottomSheetDialog, tvDia);
        TextView tvMes = bsdAddOpcaoBelow(this, R.string.repeticao_lembretes_mes, 0, rlBottomSheetDialog, tvSemana);
        TextView tvAno = bsdAddOpcaoBelow(this, R.string.repeticao_lembretes_ano, 0, rlBottomSheetDialog, tvMes);
        TextView tvNaoRepetir = bsdAddOpcaoBelow(this, R.string.repeticao_lembretes_nao_repetir, 0, rlBottomSheetDialog, tvAno);

        final View.OnClickListener onRepeticaoClick = v -> {
            if (v == tvHora) {
                mPresenter.onRepeticaoSelecionada(Lembrete.REPETICAO_HORA, 0);
            } else if (v == tvDia) {
                mPresenter.onRepeticaoSelecionada(Lembrete.REPETICAO_DIA, 0);
            } else if (v == tvSemana) {
                mPresenter.onRepeticaoSelecionada(Lembrete.REPETICAO_SEMANA, 0);
            } else if (v == tvMes) {
                mPresenter.onRepeticaoSelecionada(Lembrete.REPETICAO_MES, 0);
            } else if (v == tvAno) {
                mPresenter.onRepeticaoSelecionada(Lembrete.REPETICAO_ANO, 0);
            } else if (v == tvNaoRepetir) {
                mPresenter.onRepeticaoSelecionada(Lembrete.REPETICAO_SEM, 0);
            }
            mBottomSheetDialog.dismiss();
        };
        tvHora.setOnClickListener(onRepeticaoClick);
        tvDia.setOnClickListener(onRepeticaoClick);
        tvSemana.setOnClickListener(onRepeticaoClick);
        tvMes.setOnClickListener(onRepeticaoClick);
        tvAno.setOnClickListener(onRepeticaoClick);
        tvNaoRepetir.setOnClickListener(onRepeticaoClick);

        mBottomSheetDialog.show();
    }

    @Override
    public String getTitulo() {
        return mTiTitulo.getEditText().getText().toString();
    }

    @Override
    public String getDescricao() {
        return mTiDescricao.getEditText().getText().toString();
    }

    @Override
    public void setTitulo(String titulo) {
        mTiTitulo.getEditText().setText(titulo);
    }

    @Override
    public void setDescricao(String descricao) {
        mTiDescricao.getEditText().setText(descricao);
    }

    /**
     * Utilizado para mudar o texto do botão que abre o dialogo para selecionar a data
     * Exemplo:
     * ano = 1, mes = 1, dia = 1
     * Texto do botão -> 01/02/0001
     * <p>
     * O mês é incrementado em um pois o Calendar utiliza números de 0 a 11 para identificá-lo
     */
    @Override
    public void setTextoBotaoData(int ano, int mes, int dia) {
        String texto = String.format(getString(R.string.data_dd_MM_yyyy), dia, mes + 1, ano);
        mBtnDatePicker.setText(texto);
    }

    @Override
    public void setTextoBotaoHora(int hora, int minuto) {
        String texto = String.format(getString(R.string.hora_hh_mm), hora, minuto);
        mBtnTimePicker.setText(texto);
    }

    @Override
    public void setTextoBotaoRepeticao(int tipoRepeticao) {
        mBtnRepeticao.setText(Lembrete.getIdDaStringRepeticao(tipoRepeticao));
    }

    @Override
    public void desativarTitulo() {
        mTiTitulo.getEditText().setFocusable(false);
        mTiTitulo.getEditText().setFocusableInTouchMode(false);
        mTiTitulo.getEditText().setClickable(false);
        mTiTitulo.getEditText().setLongClickable(false);
        mTiTitulo.getEditText().setCursorVisible(false);
    }

    @Override
    public void desativarDescricao() {
        mTiDescricao.getEditText().setFocusable(false);
        mTiDescricao.getEditText().setFocusableInTouchMode(false);
        mTiDescricao.getEditText().setClickable(false);
        mTiDescricao.getEditText().setLongClickable(false);
        mTiDescricao.getEditText().setCursorVisible(false);
    }

    @Override
    public void desativarBotaoData() {
        mBtnDatePicker.setEnabled(false);
    }

    @Override
    public void desativarBotaoHora() {
        mBtnTimePicker.setEnabled(false);
    }

    @Override
    public void desativarBotaoRepeticao() {
        mBtnRepeticao.setEnabled(false);
    }

    /**
     * Utilizado para fechar esta activity e atualizar o recycler view do HomeFragment
     * Esta função adiciona um valor no bundle de resultado que indica se o HomeFragment deve atualizar os itens do recycler view (carregar o que está salvo no banco de dados)
     *
     * @param atualizarRecyclerView boolean que indica se foi armazenado algum lembrete novo. Caso seja true, o HomeFragment atualizará o recycler view de lembretes
     */
    @Override
    public void fecharActivity(boolean atualizarRecyclerView) {
        Intent intent = new Intent();
        intent.putExtra(EXTRAS_ATUALIZAR_RECYCLER_VIEW, atualizarRecyclerView);
        setResult(RESULT_OK, intent);
        finish();
    }
}