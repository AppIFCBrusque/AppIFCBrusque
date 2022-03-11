package com.ifcbrusque.app.ui.lembrete;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Lembrete;
import com.ifcbrusque.app.ui.base.BaseActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import javax.inject.Inject;

import static com.ifcbrusque.app.utils.DialogUtils.bsdAddDescricaoBelow;
import static com.ifcbrusque.app.utils.DialogUtils.bsdAddOpcaoBelow;

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

    TextInputLayout mTiTitulo, mTiDescricao, mTiAnotacoes;
    Button mBtnArquivo, mBtnDatePicker, mBtnTimePicker, mBtnRepeticao;
    TextView mTvTipo, mTvDisciplina, mTvArquivo;
    FloatingActionButton mFabSalvar;

    BottomSheetDialog mBottomSheetDialog;
    DatePickerDialog mDatePickerDialog;
    TimePickerDialog mTimePickerDialog;

    ClipboardManager mClipboard;

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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        mTvTipo = findViewById(R.id.inserir_lembrete_tipo);
        mTvDisciplina = findViewById(R.id.inserir_lembrete_disciplina);
        mTvArquivo = findViewById(R.id.inserir_lembrete_texto_arquivo);
        mTiTitulo = findViewById(R.id.tiTitulo);
        mTiDescricao = findViewById(R.id.tiDescricao);
        mTiAnotacoes = findViewById(R.id.input_anotacoes);
        mBtnArquivo = findViewById(R.id.inserir_lembrete_botao_arquivo);
        mBtnDatePicker = findViewById(R.id.btData);
        mBtnTimePicker = findViewById(R.id.btHora);
        mBtnRepeticao = findViewById(R.id.btRepeticao);
        mFabSalvar = findViewById(R.id.fabInserir);

        mBtnArquivo.setOnClickListener(v -> mPresenter.onBotaoArquivoClick());
        mBtnDatePicker.setOnClickListener(v -> mPresenter.onBotaoDataClick());
        mBtnTimePicker.setOnClickListener(v -> mPresenter.onBotaoTempoClick());
        mBtnRepeticao.setOnClickListener(v -> mPresenter.onBotaoRepeticaoClick());
        mFabSalvar.setOnClickListener(v -> mPresenter.onBotaoSalvarClick());
        mTiTitulo.getEditText().addTextChangedListener(mPresenter.onTextoTituloChanged());

        long idLembrete = -1;
        if (getIntent().getExtras() != null) {
            idLembrete = getIntent().getExtras().getLong(EXTRAS_LEMBRETE_ID, -1);
        }
        mPresenter.onViewPronta(idLembrete);
    }

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
    public String getAnotacoes() {
        return mTiAnotacoes.getEditText().getText().toString();
    }

    @Override
    public void setTitulo(String titulo) {
        mTiTitulo.getEditText().setText(titulo);
    }

    @Override
    public void setDescricao(String descricao) {
        mTiDescricao.getEditText().setText(descricao);
    }

    @Override
    public void setAnotacoes(String anotacoes) {
        mTiAnotacoes.getEditText().setText(anotacoes);
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
        mTiTitulo.getEditText().setLongClickable(true);
        mTiTitulo.getEditText().setCursorVisible(false);
        mTiTitulo.getEditText().setOnLongClickListener(onEditTextLongClickListener(mTiTitulo.getEditText()));
    }

    @Override
    public void desativarDescricao() {
        mTiDescricao.getEditText().setFocusable(false);
        mTiDescricao.getEditText().setFocusableInTouchMode(false);
        mTiDescricao.getEditText().setClickable(false);
        mTiDescricao.getEditText().setLongClickable(true);
        mTiDescricao.getEditText().setCursorVisible(false);
        mTiDescricao.getEditText().setOnLongClickListener(onEditTextLongClickListener(mTiDescricao.getEditText()));
    }

    @Override
    public void esconderDescricao() {
        mTiDescricao.setVisibility(View.GONE);
    }

    @Override
    public void mostrarAnotacoes() {
        mTiAnotacoes.setVisibility(View.VISIBLE);
    }

    private View.OnLongClickListener onEditTextLongClickListener(EditText editText) {
        return view -> {
            String texto = editText.getText().toString();
            if (texto.length() > 0) {
                ClipData clip = ClipData.newPlainText(getString(R.string.copiado), texto);
                mClipboard.setPrimaryClip(clip);
                showMessage(R.string.copiado);
                return true;
            } else {
                return false;
            }
        };
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

    @Override
    public void ativarBotaoSalvar() {
        mFabSalvar.setEnabled(true);
    }

    @Override
    public void desativarBotaoSalvar() {
        mFabSalvar.setEnabled(false);
    }

    @Override
    public void exibirBotaoArquivo() {
        mTvArquivo.setVisibility(View.VISIBLE);
        mBtnArquivo.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTextoTipoLembrete(int stringId) {
        mTvTipo.setText(stringId);
    }

    @Override
    public void exibirTipoLembrete() {
        mTvTipo.setVisibility(View.VISIBLE);
    }

    @Override
    public void setTextoDisciplina(String disciplina) {
        mTvDisciplina.setText(disciplina);
    }

    @Override
    public void exibirDisciplina() {
        mTvDisciplina.setVisibility(View.VISIBLE);
    }

    @Override
    public void abrirNavegador(String url) {
        Intent browserIntent = new Intent(android.content.Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        startActivity(browserIntent);
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