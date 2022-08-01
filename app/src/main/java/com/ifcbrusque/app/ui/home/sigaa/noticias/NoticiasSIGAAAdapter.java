package com.ifcbrusque.app.ui.home.sigaa.noticias;

import static com.ifcbrusque.app.utils.AppConstants.FORMATO_DATA;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.NoticiaArmazenavel;
import com.ifcbrusque.app.ui.home.lembretes.HeaderViewHolder;
import com.ifcbrusque.app.ui.home.lembretes.StickyHeaderDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class NoticiasSIGAAAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyHeaderDecoration.StickyHeaderInterface {
    public static final int TIPO_ITEM = 0;
    public static final int TIPO_HEADER = 1;
    private final Context mContext;
    private final List<Object> mDados = new ArrayList<>();
    private final SimpleDateFormat mFormatoData = new SimpleDateFormat(FORMATO_DATA.split(" ")[0]);
    private NoticiaSIGAAItemListener mItemListener;

    public NoticiasSIGAAAdapter(Context context) {
        mContext = context;
    }


    public List<Object> getDados() {
        return mDados;
    }
    
    /**
     * Ordena a lista de notícias pela data com a mais nova no topo e a mais velha no fim
     */
    private List<NoticiaArmazenavel> ordenar(List<NoticiaArmazenavel> noticiasArmazenaveis) {
        noticiasArmazenaveis.sort(Comparator.comparing(NoticiaArmazenavel::getData).reversed());
        return noticiasArmazenaveis;
    }

    /**
     * Retorna uma lista com as notícias e os cabeçalhos
     */
    private List<Object> addHeadersToData(List<NoticiaArmazenavel> noticiasArmazenaveis) {
        List<Object> dados = new ArrayList<>();

        Calendar hoje = Calendar.getInstance();
        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_YEAR, -1);

        String tituloHoje = mContext.getString(R.string.secao_hoje);
        String tituloOntem = mContext.getString(R.string.secao_atrasado);

        ordenar(noticiasArmazenaveis);

        for (int i = 0; i < noticiasArmazenaveis.size(); i++) {
            NoticiaArmazenavel noticiaArmazenavel = (NoticiaArmazenavel) noticiasArmazenaveis.get(i);

            // Adicionar cabeçalho
            String header = "";
            Calendar data = Calendar.getInstance();
            data.setTime(noticiaArmazenavel.getData());

            if (data.get(Calendar.YEAR) == hoje.get(Calendar.YEAR) && data.get(Calendar.DAY_OF_YEAR) == hoje.get(Calendar.DAY_OF_YEAR)) {
                // Hoje
                header = tituloHoje;
            } else if (data.get(Calendar.YEAR) == ontem.get(Calendar.YEAR) && data.get(Calendar.DAY_OF_YEAR) == ontem.get(Calendar.DAY_OF_YEAR)) {
                // Ontem
                header = tituloOntem;
            } else {
                // Outra data
                header = mFormatoData.format(data.getTime());
            }

            if (header.length() > 0 && !dados.contains(header)) {
                dados.add(header);
            }

            // Adicionar a notícia
            dados.add(noticiaArmazenavel);
        }

        return dados;
    }

    public void setItemListener(NoticiaSIGAAItemListener itemListener) {
        this.mItemListener = itemListener;
    }

    public void setNoticiasSIGAA(List<NoticiaArmazenavel> noticiasArmazenaveis) {
        mDados.clear();
        mDados.addAll(addHeadersToData(noticiasArmazenaveis));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == TIPO_ITEM) {
            return new NoticiaSIGAAViewHolder(layoutInflater.inflate(R.layout.item_noticia_sigaa, parent, false), mItemListener);
        } else if (viewType == TIPO_HEADER) {
            return new HeaderViewHolder(layoutInflater.inflate(R.layout.section_lembretes, parent, false));
        }

        throw new RuntimeException(viewType + " não é um tipo válido");
    }

    @Override
    public int getItemCount() {
        return mDados.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((mDados.get(position) instanceof NoticiaArmazenavel)) {
            return TIPO_ITEM;
        } else {
            return TIPO_HEADER;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NoticiaSIGAAViewHolder) {
            NoticiaArmazenavel noticiaArmazenavel = (NoticiaArmazenavel) mDados.get(position);
            NoticiaSIGAAViewHolder viewHolder = (NoticiaSIGAAViewHolder) holder;
            viewHolder.mTvTitulo.setText(noticiaArmazenavel.getTitulo());
            viewHolder.mTvDisciplina.setText(noticiaArmazenavel.getDisciplinaNome());
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.mTitulo.setText((String) mDados.get(position));
        }
    }

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
        TextView mTitulo = header.findViewById(R.id.secao_lembretes_titulo);
        mTitulo.setText((String) mDados.get(headerPosition));
    }
}
