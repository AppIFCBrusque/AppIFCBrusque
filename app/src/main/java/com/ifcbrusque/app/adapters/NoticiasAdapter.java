package com.ifcbrusque.app.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;
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
import com.ifcbrusque.app.helpers.noticia.NoticiasParser;
import com.ifcbrusque.app.models.Preview;

import java.util.List;

import static com.ifcbrusque.app.helpers.image.ImageManager.*;

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
        if(previews.get(position).getTitulo().length() > 0) holder.tvTitulo.setText(previews.get(position).getTitulo());
        if(previews.get(position).getDescricao().length() > 0) holder.tvPrevia.setText(previews.get(position).getDescricao() + " [...]\n" + NoticiasParser.FORMATO_DATA.format(previews.get(position).getDataNoticia()));
        if(previews.get(position).getUrlImagemPreview().length() > 0) {
            holder.ivPreview.setImageURI(getUriArmazenamentoImagem(previews.get(position).getUrlImagemPreview(), context));
        } else {
            //Sem imagem, definir imagem padrão
            holder.ivPreview.setImageResource(R.drawable.ic_launcher_background);
        }
    }


    @Override
    public int getItemCount() {
        return previews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        TextView tvTitulo, tvPrevia;
        ImageView ivPreview;
        OnPreviewListener onPreviewListener;
        ValueAnimator colorAnimation;

        public ViewHolder(@NonNull View itemView, OnPreviewListener onPreviewListener) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.noticia_titulo);
            tvPrevia = itemView.findViewById(R.id.noticia_previa);
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
