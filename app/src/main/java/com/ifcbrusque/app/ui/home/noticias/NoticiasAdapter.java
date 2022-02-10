package com.ifcbrusque.app.ui.home.noticias;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.db.model.Preview;

import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import static com.ifcbrusque.app.data.network.noticias.PgNoticiasParser.*;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiaViewHolder> {
    private final Picasso mPicasso;

    private List<Preview> mPreviews;
    private NoticiaItemListener mItemListener;

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
    public void setItemListener(NoticiaItemListener itemListener) {
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
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(view, mItemListener);
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
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        Preview preview = mPreviews.get(position);

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
                .into(holder.mIvPreview);
    }
}
