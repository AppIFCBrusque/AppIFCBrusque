package com.ifcbrusque.app.ui.home.lembretes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Lembrete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA;

public class LembretesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderDecoration.StickyHeaderInterface {
    public static final int TIPO_LEMBRETE = 0;
    public static final int TIPO_HEADER = 1;

    private List<Object> mDados;
    private ItemListener mItemListener;
    private int mCategoria;
    private int mMargemHorizontal, mMargemVertical;

    public LembretesAdapter(List<Object> dados, int categoria, int margemHorizontal, int margemVertical) {
        mDados = dados;
        mCategoria = categoria;
        mMargemHorizontal = margemHorizontal;
        mMargemVertical = margemVertical;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<Lembrete> ordenarLembretesPelaData(List<Lembrete> lembretes) {
        lembretes.sort((o1, o2) -> o1.getDataLembrete().compareTo(o2.getDataLembrete()));
        return lembretes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções que podem ser utilizadas pela view
     */
    public void setItemListener(ItemListener itemListener) {
        mItemListener = itemListener;
    }

    public List<Lembrete> getLembretes() {
        List<Lembrete> lembretes = new ArrayList<>();
        for (Object o : mDados) {
            if (o instanceof Lembrete) {
                lembretes.add((Lembrete) o);
            }
        }

        return lembretes;
    }

    public void setLembretes(List<Lembrete> lembretes) {
        mDados = new ArrayList<>();
        mDados.addAll(ordenarLembretesPelaData(lembretes));
        notifyDataSetChanged();
    }

    public List<Object> getDados() {
        return mDados;
    }

    public void setDados(List<Object> dados) {
        mDados = dados;
        notifyDataSetChanged();
    }

    public void setCategoria(int categoria) {
        if (mCategoria != categoria) {
            mCategoria = categoria;
            notifyDataSetChanged();
        }
    }

    public boolean isItemVisivel(Lembrete lembrete) {
        if (mCategoria >= 10) {
            //TODO: Categorias personalizadas
            return false;
        } else {
            //Categorias padrão (incompleto, completo, todos)
            return lembrete.getEstado() == mCategoria || mCategoria == 0;
        }
    }

    public boolean itemVisivelNaFrenteDoHeader(int position) {
        int i = position + 1;
        while (i < mDados.size() && mDados.get(i) instanceof Lembrete) {
            if (isItemVisivel((Lembrete) mDados.get(i))) {
                return true;
            }
            i++;
        }
        return false;
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
            return new LembretesAdapter.ViewHolderItem(view);
        } else if (viewType == TIPO_HEADER) {
            View view = layoutInflater.inflate(R.layout.section_lembretes, parent, false);
            return new ViewHolderHeader(view);
        }

        throw new RuntimeException(viewType + " não é um tipo válido");
    }

    /**
     * Função obrigatória do recycler view
     */
    @Override
    public int getItemCount() {
        return mDados.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((mDados.get(position) instanceof Lembrete)) {
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
        if (holder instanceof ViewHolderItem) {

            ViewHolderItem itemHolder = (ViewHolderItem) holder;
            Lembrete lembrete = (Lembrete) mDados.get(position);

            itemHolder.mTvTitulo.setText(lembrete.getTitulo());

            if (lembrete.getDescricao().length() > 0) {
                itemHolder.mTvDescricao.setVisibility(View.VISIBLE);
                itemHolder.mTvDescricao.setText(lembrete.getDescricao());
            } else {
                itemHolder.mTvDescricao.setVisibility(View.GONE);
                itemHolder.mTvDescricao.setText("");
            }

            String[] data = new SimpleDateFormat(FORMATO_DATA).format(lembrete.getDataLembrete()).split(" ");
            itemHolder.mTvData.setText(data[0]);
            itemHolder.mTvHora.setText(data[1]);

            if (lembrete.getTipoRepeticao() != Lembrete.REPETICAO_SEM) {
                itemHolder.mTvRepeticao.setVisibility(View.VISIBLE);
                itemHolder.mTvRepeticao.setText(Lembrete.getIdDaStringRepeticao(lembrete.getTipoRepeticao()));
            } else {
                itemHolder.mTvRepeticao.setVisibility(View.GONE);
            }

            //Categorias
            if (isItemVisivel(lembrete)) {
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(mMargemHorizontal, mMargemVertical, mMargemHorizontal, mMargemVertical);
                itemHolder.itemView.setLayoutParams(params);
                itemHolder.itemView.setVisibility(View.VISIBLE);
            } else {
                itemHolder.itemView.setVisibility(View.GONE);
                itemHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

        } else if (holder instanceof ViewHolderHeader) {

            ViewHolderHeader headerHolder = (ViewHolderHeader) holder;

            //Esconder caso não tenha um lembrete vísivel na frente
            if (itemVisivelNaFrenteDoHeader(position)) {
                //Texto
                String textoHeader = (String) mDados.get(position);
                headerHolder.mTitulo.setText(textoHeader);

                headerHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                headerHolder.itemView.setVisibility(View.VISIBLE);
            } else {
                headerHolder.itemView.setVisibility(View.GONE);
                headerHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }

        }
    }

    /*
    Utilizado para declarar a estrutura do ViewHolder (item_lembrete)

    Essencialmente, serve pra transformar o item em uma view
    Você configura ele quase da mesma forma que uma view
     */
    public class ViewHolderItem extends RecyclerView.ViewHolder {
        final TextView mTvTitulo;
        final TextView mTvDescricao;
        final TextView mTvData;
        final TextView mTvHora;
        final TextView mTvRepeticao;
        final ImageButton mIbOpcoes;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            mTvTitulo = itemView.findViewById(R.id.lembrete_titulo);
            mTvDescricao = itemView.findViewById(R.id.lembrete_descricao);
            mTvData = itemView.findViewById(R.id.lembrete_data);
            mTvHora = itemView.findViewById(R.id.lembrete_hora);
            mIbOpcoes = itemView.findViewById(R.id.lembrete_opcoes);
            mTvRepeticao = itemView.findViewById(R.id.lembrete_repeticao);

            itemView.setOnClickListener(v -> mItemListener.onLembreteClick(getAdapterPosition()));
            mIbOpcoes.setOnClickListener(v -> mItemListener.onOpcoesClick(getAdapterPosition()));
        }
    }

    public class ViewHolderHeader extends RecyclerView.ViewHolder {
        final TextView mTitulo;

        public ViewHolderHeader(@NonNull View itemView) {
            super(itemView);
            mTitulo = itemView.findViewById(R.id.secao_lembretes_titulo);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções do StickyHeaderDecoration
     */
    @Override
    public boolean isHeader(int itemPosition) {
        return (mDados.get(itemPosition) instanceof String);
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

        if (itemVisivelNaFrenteDoHeader(headerPosition)) {
            String textoHeader = (String) mDados.get(headerPosition);
            mTitulo.setText(textoHeader);

            layout.setAlpha(1);
        } else {
            layout.setAlpha(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções chamadas pelos itens que são definidas na view
     */
    public interface ItemListener {
        void onLembreteClick(int position);

        void onOpcoesClick(int position);
    }
}
