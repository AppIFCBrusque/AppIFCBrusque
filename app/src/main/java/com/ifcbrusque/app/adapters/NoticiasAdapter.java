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
import com.ifcbrusque.app.helpers.NoticiasParser;
import com.ifcbrusque.app.models.Preview;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.ViewHolder> {

    public List<Preview> previews; ///TODO: fazer uma função de set pra isso
    private OnPreviewListener mOnPreviewListener;
    Context context;

    private int colorFrom;
    private final int colorTo = Color.BLUE;

    public NoticiasAdapter(Context context, List<Preview> previews, OnPreviewListener onPreviewListener) {
        this.context = context;
        this.previews = previews;
        this.mOnPreviewListener = onPreviewListener;

        colorFrom = MaterialColors.getColor(context, R.attr.colorSurface, Color.WHITE);
    }

    @NonNull
    @Override
    public NoticiasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_noticia, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mOnPreviewListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiasAdapter.ViewHolder holder, int position) {
        if(position == 0) {
            //Adicionar o espaçamento extra em cima
            int padding_views = (int) context.getResources().getDimension(R.dimen.padding_views);
            int padding_views_dobro = (int) context.getResources().getDimension(R.dimen.padding_views_dobro);
            holder.itemView.setPadding(padding_views, padding_views_dobro, padding_views, 0);
        }

        if(previews.get(position).getTitulo().length() > 0) holder.tvTitulo.setText(previews.get(position).getTitulo());
        holder.tvData.setText(NoticiasParser.FORMATO_DATA.format(previews.get(position).getDataNoticia()));

        if(previews.get(position).getUrlImagemPreview().length() > 0) {
            Picasso.get()
                    .load(previews.get(position).getUrlImagemPreview())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivPreview);
        } else {
            //Sem imagem, definir imagem padrão
            Picasso.get()
                    .load(R.drawable.ic_launcher_background)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivPreview);
        }
    }

    @Override
    public int getItemCount() {
        return previews.size();
    }

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

        /*
        Abrir notícia e retornar a cor do fundo
         */
        @Override
        public void onClick(View v) {
            colorAnimation.end();
            colorAnimation.setStartDelay(0);
            colorAnimation.reverse();

            onPreviewListener.onPreviewClick(getAdapterPosition());
        }

        /*
        Mudar a cor do fundo para destacar
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

    public interface OnPreviewListener {
        void onPreviewClick(int position);
    }
}
