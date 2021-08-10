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

    public static SimpleDateFormat FORMATO_DATA = new SimpleDateFormat("dd/MM/yyyy HH:mm"); //TODO: Passar isto para outro lugar

    private int colorFrom;
    private final int colorTo = Color.BLUE;

    public HomeAdapter(Context context, List<Lembrete> lembretes, HomeAdapter.OnPreviewListener onPreviewListener) {
        //Iniciar variáveis
        this.context = context;
        this.lembretes = lembretes;
        this.mOnPreviewListener = onPreviewListener;

        colorFrom = MaterialColors.getColor(context, R.attr.colorSurface, Color.WHITE);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    Funções que podem ser utilizadas pela view
     */
    public void setLembretes(List<Lembrete> lembretes) {
        this.lembretes = lembretes;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Função obrigatória do recycler view
     */
    @NonNull
    @Override
    public HomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_lembrete, parent, false);
        HomeAdapter.ViewHolder viewHolder = new HomeAdapter.ViewHolder(view, mOnPreviewListener);
        return viewHolder;
    }

    /**
     * Função obrigatória do recycler view
     */
    @Override
    public int getItemCount() {
        return lembretes.size();
    }

    /**
     * Função obrigatória do recycler view
     * Executada para ao colocar um item_lemnbrete no recycler view
     * É aqui que você define as propriedades do item em questão, como o que vai estar escrito em cada parte
     */
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

    /*
    Utilizado para declarar a estrutura do ViewHolder (item_lembrete)

    Essencialmente, serve pra transformar o item em uma view
    Você configura ele quase da mesma forma que uma view
     */
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

        /**
         * Definir funçõa de onClick para o itemView.setOnClickListener(this);
         *
         * Executa a animação da mudança de cor ao contrário (da cor destacada ao fundo original) e chama a função para abrir o lembrete
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
