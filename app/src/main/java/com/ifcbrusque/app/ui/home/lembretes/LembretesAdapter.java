package com.ifcbrusque.app.ui.home.lembretes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Lembrete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA;

public class LembretesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderDecoration.StickyHeaderInterface {
    private final Context mContext;

    private List<Lembrete> mTodosOsLembretes;
    private List<Object> mDadosVisiveis;

    public static final int TIPO_LEMBRETE = 0;
    public static final int TIPO_HEADER = 1;

    private LembreteItemListener mItemListener;

    private int mCategoria;

    private final SimpleDateFormat mFormatoData;
    private final int mCorIncompleto, mCorCompleto;

    public LembretesAdapter(Context context, List<Lembrete> lembretes, int categoria) {
        mContext = context;
        mTodosOsLembretes = lembretes;
        mDadosVisiveis = new ArrayList<>();
        mCategoria = categoria;
        mCorIncompleto = mContext.getResources().getColor(R.color.vermelho);
        mCorCompleto = mContext.getResources().getColor(R.color.verde);
        mFormatoData = new SimpleDateFormat(FORMATO_DATA);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void exibirLembretes() {
        mDadosVisiveis = aplicarFiltros(mTodosOsLembretes);
        mDadosVisiveis = adicionarHeadersDosLembretes(mDadosVisiveis);
        notifyDataSetChanged();
    }

    private List<Lembrete> ordenarLembretesPelaData(List<Lembrete> lembretes) {
        lembretes.sort((o1, o2) -> o1.getDataLembrete().compareTo(o2.getDataLembrete()));
        return lembretes;
    }

    private List<Object> adicionarHeadersDosLembretes(List<Object> lembretes) {
        //Lista que vai ser inserida no recycler view
        List<Object> dados = new ArrayList<>();

        Calendar hoje = Calendar.getInstance();
        Calendar amanha = Calendar.getInstance();
        amanha.add(Calendar.DAY_OF_YEAR, 1);
        amanha.set(Calendar.HOUR_OF_DAY, 23);
        amanha.set(Calendar.MINUTE, 59);
        amanha.set(Calendar.SECOND, 59);
        amanha.set(Calendar.MILLISECOND, 999);
        Calendar daquiUmMes = Calendar.getInstance();
        daquiUmMes.add(Calendar.MONTH, 1);

        String tituloAtrasado = mContext.getString(R.string.secao_atrasado);
        String tituloHoje = mContext.getString(R.string.secao_hoje);
        String tituloAmanha = mContext.getString(R.string.secao_amanha);
        String tituloEmXDias = mContext.getString(R.string.secao_em_dias);
        String tituloApos1Mes = mContext.getString(R.string.secao_apos_um_mes);
        String tituloAposXMeses = mContext.getString(R.string.secao_apos_meses);

        for (int i = 0; i < lembretes.size(); i++) {
            Lembrete lembrete = (Lembrete) lembretes.get(i);

            //Adicionar cabeçalho
            Calendar dataLembrete = Calendar.getInstance();
            dataLembrete.setTime(lembrete.getDataLembrete());

            if (dataLembrete.after(daquiUmMes)) {
                //Meses
                int meses = dataLembrete.get(Calendar.MONTH) - hoje.get(Calendar.MONTH);
                String titulo;
                if (meses == 1) {
                    titulo = tituloApos1Mes;
                } else {
                    titulo = String.format(tituloAposXMeses, Integer.toString(meses));
                }

                if (!dados.contains(titulo)) {
                    dados.add(titulo);
                }
            } else if (dataLembrete.after(amanha)) {
                //Em x dias
                int dias = dataLembrete.get(Calendar.DAY_OF_YEAR) - hoje.get(Calendar.DAY_OF_YEAR);
                String titulo = String.format(tituloEmXDias, Integer.toString(dias));
                if (!dados.contains(titulo)) {
                    dados.add(titulo);
                }
            } else if (dataLembrete.get(Calendar.YEAR) == amanha.get(Calendar.YEAR) && dataLembrete.get(Calendar.DAY_OF_YEAR) == amanha.get(Calendar.DAY_OF_YEAR)) {
                //Amanhã
                if (!dados.contains(tituloAmanha)) {
                    dados.add(tituloAmanha);
                }
            } else if (dataLembrete.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) && dataLembrete.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR)) {
                //Hoje
                if (!dados.contains(tituloHoje)) {
                    dados.add(tituloHoje);
                }
            } else if (hoje.getTime().after(dataLembrete.getTime())) {
                //Atrasado
                if (!dados.contains(tituloAtrasado)) {
                    dados.add(tituloAtrasado);
                }
            }

            //Adicionar lembrete
            dados.add(lembrete);
        }

        return dados;
    }

    private List<Object> aplicarFiltros(List<Lembrete> lembretes) {
        return lembretes.stream().filter(lembrete -> isItemVisivel(lembrete)).collect(Collectors.toList());
    }

    private boolean isItemVisivel(Lembrete lembrete) {
        if (mCategoria >= 10) {
            //TODO: Categorias personalizadas
            return false;
        } else {
            //Categorias padrão (incompleto, completo, todos)
            return lembrete.getEstado() == mCategoria || mCategoria == 0;
        }
    }

    private boolean existeItemVisivelNaFrenteDoHeader(int position) {
        int i = position + 1;
        while (i < mDadosVisiveis.size() && mDadosVisiveis.get(i) instanceof Lembrete) {
            if (isItemVisivel((Lembrete) mDadosVisiveis.get(i))) {
                return true;
            }
            i++;
        }
        return false;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções que podem ser utilizadas pela view
     */
    public void setItemListener(LembreteItemListener itemListener) {
        mItemListener = itemListener;
    }

    public List<Lembrete> getLembretes() {
        return mTodosOsLembretes;
    }

    public void setLembretes(List<Lembrete> lembretes) {
        mTodosOsLembretes = ordenarLembretesPelaData(lembretes);
        exibirLembretes();
    }

    public List<Object> getDadosVisiveis() {
        return mDadosVisiveis;
    }

    public void setCategoria(int categoria) {
        if (mCategoria != categoria) {
            mCategoria = categoria;
            exibirLembretes();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Função obrigatória do recycler view
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TIPO_LEMBRETE) {
            View view = layoutInflater.inflate(R.layout.item_lembrete, parent, false);
            return new LembreteViewHolder(view, mItemListener);
        } else if (viewType == TIPO_HEADER) {
            View view = layoutInflater.inflate(R.layout.section_lembretes, parent, false);
            return new HeaderViewHolder(view);
        }

        throw new RuntimeException(viewType + " não é um tipo válido");
    }

    /**
     * Função obrigatória do recycler view
     */
    @Override
    public int getItemCount() {
        return mDadosVisiveis.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((mDadosVisiveis.get(position) instanceof Lembrete)) {
            return TIPO_LEMBRETE;
        } else {
            return TIPO_HEADER;
        }
    }

    /**
     * Função obrigatória do recycler view
     * Executada ao colocar um item_lembrete ou section_lembrete no recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LembreteViewHolder) {

            LembreteViewHolder itemHolder = (LembreteViewHolder) holder;
            Lembrete lembrete = (Lembrete) mDadosVisiveis.get(position);

            //Título
            itemHolder.mTvTitulo.setText(lembrete.getTitulo());

            //Descrição
            if (lembrete.getDescricao().length() > 0) {
                itemHolder.mTvDescricao.setVisibility(View.VISIBLE);
                itemHolder.mTvDescricao.setText(lembrete.getDescricao());
            } else {
                itemHolder.mTvDescricao.setVisibility(View.GONE);
                itemHolder.mTvDescricao.setText("");
            }

            //Data
            String[] data = mFormatoData.format(lembrete.getDataLembrete()).split(" ");
            itemHolder.mTvData.setText(data[0]);
            itemHolder.mTvHora.setText(data[1]);

            //Repetição
            if (lembrete.getTipoRepeticao() != Lembrete.REPETICAO_SEM) {
                itemHolder.mTvRepeticao.setVisibility(View.VISIBLE);
                itemHolder.mTvRepeticao.setText(Lembrete.getIdDaStringRepeticao(lembrete.getTipoRepeticao()));
            } else {
                itemHolder.mTvRepeticao.setVisibility(View.GONE);
            }

            //Informações do SIGAA
            if (lembrete.getTipo() != Lembrete.LEMBRETE_PESSOAL) {
                /*
                Disciplina

                Estou salvando o nome da disciplina diretamente no lembrete
                Acredito que não vale a pena consultar o banco de dados para encontrar a disciplina associada à atividade aqui, pois não influencia coisa alguma
                 */
                itemHolder.mTvDisciplina.setText(lembrete.getNomeDisciplina());

                //Tipo
                switch (lembrete.getTipo()) {
                    case Lembrete.LEMBRETE_AVALIACAO:
                        itemHolder.mTvTipo.setText(R.string.avaliacao);
                        break;

                    case Lembrete.LEMBRETE_TAREFA:
                        itemHolder.mTvTipo.setText(R.string.tarefa);
                        break;

                    case Lembrete.LEMBRETE_QUESTIONARIO:
                        itemHolder.mTvTipo.setText(R.string.questionario);
                        break;
                }

                itemHolder.mTvDisciplina.setVisibility(View.VISIBLE);
                itemHolder.mTvTipo.setVisibility(View.VISIBLE);
            } else {
                itemHolder.mTvDisciplina.setVisibility(View.GONE);
                itemHolder.mTvTipo.setVisibility(View.GONE);
            }

            //Cor
            if (lembrete.getEstado() == Lembrete.ESTADO_INCOMPLETO) {
                itemHolder.mVwCor.setBackgroundColor(mCorIncompleto);
            } else {
                itemHolder.mVwCor.setBackgroundColor(mCorCompleto);
            }

        } else if (holder instanceof HeaderViewHolder) {

            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;

            String textoHeader = (String) mDadosVisiveis.get(position);
            headerHolder.mTitulo.setText(textoHeader);

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções do StickyHeaderDecoration
     */
    @Override
    public boolean isHeader(int itemPosition) {
        return (mDadosVisiveis.get(itemPosition) instanceof String);
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.section_lembretes;
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        LinearLayout layout = header.findViewById(R.id.secao_layout);
        TextView mTitulo = header.findViewById(R.id.secao_lembretes_titulo);

        if (existeItemVisivelNaFrenteDoHeader(headerPosition)) {
            String textoHeader = (String) mDadosVisiveis.get(headerPosition);
            mTitulo.setText(textoHeader);

            layout.setAlpha(1);
        } else {
            layout.setAlpha(0);
        }
    }
}
