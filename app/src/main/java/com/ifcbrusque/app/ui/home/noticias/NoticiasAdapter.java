package com.ifcbrusque.app.ui.home.noticias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Preview;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import static com.ifcbrusque.app.data.network.noticias.PgNoticiasParser.*;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.ViewHolder> {
    private final Picasso mPicasso;

    private List<Preview> mPreviews;
    private ItemListener mItemListener;

    private final String URL_SEM_IMAGEM = "http://noticias.brusque.ifc.edu.br/wp-content/themes/ifc-v2/assets/images/sem_imagem.jpg";

    @Inject
    public NoticiasAdapter(List<Preview> previews, Picasso picasso) {
        mPreviews = previews;
        mPicasso = picasso;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções que podem ser utilizadas pela view
     */
    public void setItemListener(NoticiasAdapter.ItemListener itemListener) {
        mItemListener = itemListener;
    }

    public List<Preview> getPreviews() {
        return mPreviews;
    }

    public void setPreviews(List<Preview> previews) {
        mPreviews = previews;
        notifyDataSetChanged();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Função obrigatória do recycler view
     */
    @NonNull
    @Override
    public NoticiasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_noticia, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Função obrigatória do recycler view
     */
    @Override
    public int getItemCount() {
        return mPreviews.size();
    }

    /**
     * Função obrigatória do recycler view
     * Executada para ao colocar um item_noticia no recycler view
     * É aqui que você define as propriedades do item em questão, como o que vai estar escrito em cada parte
     */
    @Override
    public void onBindViewHolder(@NonNull NoticiasAdapter.ViewHolder holder, int position) {
        Preview preview = mPreviews.get(position);
        //TODO: Adicionar o espaçamento extra em cima do primeiro preview

        if (preview.getTitulo().length() > 0) {
            holder.mTvTitulo.setText(preview.getTitulo());
        } else {
            holder.mTvTitulo.setText(R.string.noticia_titulo_padrao);
        }

        String data = FORMATO_DATA.format(preview.getDataNoticia());
        holder.mTvData.setText(data);

        mPicasso.load(((preview.getUrlImagemPreview().equals("") || preview.getUrlImagemPreview().length() == 0)) ? URL_SEM_IMAGEM : preview.getUrlImagemPreview())
                .fit()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .into(holder.mIvPreview);
    }

    /*
    Utilizado para declarar a estrutura do ViewHolder (item_noticia)

    Essencialmente, serve pra transformar o item em uma view
    Você configura ele quase da mesma forma que uma view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mTvTitulo;
        final TextView mTvData;
        final ImageView mIvPreview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTitulo = itemView.findViewById(R.id.noticia_titulo);
            mTvData = itemView.findViewById(R.id.noticia_data);
            mIvPreview = itemView.findViewById(R.id.noticia_imagem);

            itemView.setOnClickListener(v -> mItemListener.onPreviewClick(getAdapterPosition()));
        }

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas neste adapter que são definidas na view (comunica a view com o adapter)
     */
    public interface ItemListener {
        void onPreviewClick(int position);
    }
}
