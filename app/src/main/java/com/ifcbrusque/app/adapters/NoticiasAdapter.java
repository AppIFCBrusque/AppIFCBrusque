package com.ifcbrusque.app.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.color.MaterialColors;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.models.Preview;
import com.squareup.picasso.Picasso;
import java.util.List;

import static com.ifcbrusque.app.util.helpers.PaginaNoticiasHelper.*;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.ViewHolder> {
    private List<Preview> previews;
    private OnPreviewListener mOnPreviewListener;
    Context context;

    private final String URL_SEM_IMAGEM = "http://noticias.brusque.ifc.edu.br/wp-content/themes/ifc-v2/assets/images/sem_imagem.jpg";
    private int colorFrom;
    private final int colorTo = Color.BLUE;

    public NoticiasAdapter(Context context, List<Preview> previews, OnPreviewListener onPreviewListener) {
        this.context = context;
        this.previews = previews;
        this.mOnPreviewListener = onPreviewListener;

        colorFrom = MaterialColors.getColor(context, R.attr.colorSurface, Color.WHITE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções que podem ser utilizadas pela view
     */
    public List<Preview> getPreviews() {
        return previews;
    }

    public void setPreviews(List<Preview> previews) {
        this.previews = previews;
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
        ViewHolder viewHolder = new ViewHolder(view, mOnPreviewListener);
        return viewHolder;
    }

    /**
     * Função obrigatória do recycler view
     */
    @Override
    public int getItemCount() {
        return previews.size();
    }

    /**
     * Função obrigatória do recycler view
     * Executada para ao colocar um item_noticia no recycler view
     * É aqui que você define as propriedades do item em questão, como o que vai estar escrito em cada parte
     */
    @Override
    public void onBindViewHolder(@NonNull NoticiasAdapter.ViewHolder holder, int position) {
        //Adicionar o espaçamento extra em cima do primeiro preview
        if(position == 0) {
            int padding_views = (int) context.getResources().getDimension(R.dimen.padding_views);
            int padding_views_dobro = (int) context.getResources().getDimension(R.dimen.padding_views_dobro);
            holder.itemView.setPadding(padding_views, padding_views_dobro, padding_views, 0);
        }

        if(previews.get(position).getTitulo().length() > 0) holder.tvTitulo.setText(previews.get(position).getTitulo());
        holder.tvData.setText(FORMATO_DATA.format(previews.get(position).getDataNoticia()));
        //Imagem
        Picasso.get()
                .load(((previews.get(position).getUrlImagemPreview().equals("") || previews.get(position).getUrlImagemPreview().length() == 0)) ? URL_SEM_IMAGEM : previews.get(position).getUrlImagemPreview())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPreview);

    }

    /*
    Utilizado para declarar a estrutura do ViewHolder (item_noticia)

    Essencialmente, serve pra transformar o item em uma view
    Você configura ele quase da mesma forma que uma view
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        TextView tvTitulo, tvData;
        ImageView ivPreview;
        OnPreviewListener onPreviewListener;
        ValueAnimator colorAnimation;

        public ViewHolder(@NonNull View itemView, OnPreviewListener onPreviewListener) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.noticia_titulo);
            tvData = itemView.findViewById(R.id.noticia_data);
            ivPreview = itemView.findViewById(R.id.noticia_imagem);
            this.onPreviewListener = onPreviewListener;

            //Animação da mudança de cor ao clique
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(300); // milliseconds
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    itemView.setBackgroundColor((int) animator.getAnimatedValue());
                }
            });

            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
        }

        /**
         * Definir funçõa de onClick para o itemView.setOnClickListener(this);
         *
         * Executa a animação da mudança de cor ao contrário (da cor destacada ao fundo original) e chama a função para abrir a notícia
         */
        @Override
        public void onClick(View v) {
            colorAnimation.end();
            colorAnimation.setStartDelay(0);
            colorAnimation.reverse();

            onPreviewListener.onPreviewClick(getAdapterPosition());
        }

        /**
         * Definir função de onTouch para o itemView.setOnTouchListener(this);
         *
         * Muda a cor do fundo para destacar
         * Quando pressiona, começa a animação
         * Quando move, cancela a animação e retorna instantaneamente à cor original
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) { //Inicia a animação
                colorAnimation.setStartDelay(200);
                colorAnimation.start();
            } else if(event.getAction() == MotionEvent.ACTION_CANCEL) { //Reseta a animação
                colorAnimation.end();
                v.setBackgroundColor(colorFrom);
            }
            return false;
        }

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções utilizadas neste adapter que são definidas na view (comunica a view com o adapter)
     */
    public interface OnPreviewListener {
        void onPreviewClick(int position);
    }
}
