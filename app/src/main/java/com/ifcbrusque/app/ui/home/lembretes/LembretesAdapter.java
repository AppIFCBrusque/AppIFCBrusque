package com.ifcbrusque.app.ui.home.lembretes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Lembrete;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA;

public class LembretesAdapter extends RecyclerView.Adapter<LembretesAdapter.ViewHolder> {
    private List<Lembrete> mLembretes;
    private ItemListener mItemListener;
    private int mCategoria;

    public LembretesAdapter(List<Lembrete> lembretes, int categoria) {
        mLembretes = lembretes;
        mCategoria = categoria;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções que podem ser utilizadas pela view
     */
    public void setItemListener(ItemListener itemListener) {
        mItemListener = itemListener;
    }

    public List<Lembrete> getLembretes() {
        return mLembretes;
    }

    public void setLembretes(List<Lembrete> lembretes) {
        mLembretes = lembretes;
        notifyDataSetChanged();
    }

    public void setCategoria(int categoria) {
        if(mCategoria != categoria) {
            mCategoria = categoria;
            notifyDataSetChanged();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Função obrigatória do recycler view
     */
    @NonNull
    @Override
    public LembretesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_lembrete, parent, false);
        LembretesAdapter.ViewHolder viewHolder = new LembretesAdapter.ViewHolder(view);
        return viewHolder;
    }

    /**
     * Função obrigatória do recycler view
     */
    @Override
    public int getItemCount() {
        return mLembretes.size();
    }

    /**
     * Função obrigatória do recycler view
     * Executada para ao colocar um item_lembrete no recycler view
     */
    @Override
    public void onBindViewHolder(@NonNull LembretesAdapter.ViewHolder holder, int position) {
        Lembrete lembrete = mLembretes.get(position);

        holder.mTvTitulo.setText(lembrete.getTitulo());

        if(lembrete.getDescricao().length() > 0) {
            holder.mTvDescricao.setVisibility(View.VISIBLE);
            holder.mTvDescricao.setText(lembrete.getDescricao());
        } else {
            holder.mTvDescricao.setVisibility(View.GONE);
            holder.mTvDescricao.setText("");
        }

        String[] data = new SimpleDateFormat(FORMATO_DATA).format(lembrete.getDataLembrete()).split(" ");
        holder.mTvData.setText(data[0]);
        holder.mTvHora.setText(data[1]);

        if(lembrete.getTipoRepeticao() != Lembrete.REPETICAO_SEM) {
            holder.mTvRepeticao.setVisibility(View.VISIBLE);
            holder.mTvRepeticao.setText(Lembrete.getIdDaStringRepeticao(lembrete.getTipoRepeticao()));
        } else {
            holder.mTvRepeticao.setVisibility(View.GONE);
        }

        //Categorias
        if(mCategoria >= 10) {
            //TODO: Categorias personalizadas
        } else {
            //Categorias padrão (incompleto, completo, todos)
            if(lembrete.getEstado() == mCategoria || mCategoria == 0) {
                //Mostrar o item
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                //Esconder o item
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        }
    }

    /*
    Utilizado para declarar a estrutura do ViewHolder (item_lembrete)

    Essencialmente, serve pra transformar o item em uma view
    Você configura ele quase da mesma forma que uma view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvTitulo, mTvDescricao, mTvData, mTvHora, mTvRepeticao;
        ImageButton mIbOpcoes;

        public ViewHolder(@NonNull View itemView) {
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas neste adapter que são definidas na view (comunica a view com o adapter)
     */
    public interface ItemListener {
        void onLembreteClick(int position);
        void onOpcoesClick(int position);
    }
}
