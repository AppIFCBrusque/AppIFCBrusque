package com.ifcbrusque.app.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;
import com.ifcbrusque.app.R;
import com.ifcbrusque.app.models.Lembrete;

import java.text.SimpleDateFormat;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private List<Lembrete> lembretes;
    private HomeAdapter.OnPreviewListener mOnPreviewListener;
    Context context;

    public static SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private int colorFrom;
    private final int colorTo = Color.BLUE;

    public HomeAdapter(Context context, List<Lembrete> lembretes, HomeAdapter.OnPreviewListener onPreviewListener) {
        this.context = context;
        this.lembretes = lembretes;
        this.mOnPreviewListener = onPreviewListener;

        colorFrom = MaterialColors.getColor(context, R.attr.colorSurface, Color.WHITE);
    }

    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_lembrete, parent, false);
        HomeAdapter.ViewHolder viewHolder = new HomeAdapter.ViewHolder(view, mOnPreviewListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.ViewHolder holder, int position) {
        if(position == 0) {
            //Adicionar o espaçamento extra em cima
            int padding_views = (int) context.getResources().getDimension(R.dimen.padding_views);
            int padding_views_dobro = (int) context.getResources().getDimension(R.dimen.padding_views_dobro);
            holder.itemView.setPadding(padding_views, padding_views_dobro, padding_views, 0);
        }

        holder.tvTitulo.setText(lembretes.get(position).getTitulo());
        if(lembretes.get(position).getDescricao().length() > 0) {
            holder.tvDescricao.setVisibility(View.VISIBLE);
            holder.tvDescricao.setText(lembretes.get(position).getDescricao());
        } else {
            holder.tvDescricao.setVisibility(View.GONE);
            holder.tvDescricao.setText("");
        }

        String[] data = FORMATO_DATA.format(lembretes.get(position).getDataLembrete()).split(" ");
        holder.tvData.setText(data[0]);
        holder.tvHora.setText(data[1]);
    }

    @Override
    public int getItemCount() {
        return lembretes.size();
    }

    public void setLembretes(List<Lembrete> lembretes) {
        this.lembretes = lembretes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        TextView tvTitulo, tvDescricao, tvData, tvHora;
        HomeAdapter.OnPreviewListener onPreviewListener;
        ValueAnimator colorAnimation;

        public ViewHolder(@NonNull View itemView, HomeAdapter.OnPreviewListener onPreviewListener) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.lembrete_titulo);
            tvDescricao = itemView.findViewById(R.id.lembrete_descricao);
            tvData = itemView.findViewById(R.id.lembrete_data);
            tvHora = itemView.findViewById(R.id.lembrete_hora);
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
