package com.ifcbrusque.app.ui.home.lembretes;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;

public class LembreteViewHolder extends RecyclerView.ViewHolder {
    final TextView mTvTitulo, mTvDescricao, mTvData, mTvHora, mTvRepeticao, mTvDisciplina, mTvTipo;
    final ImageButton mIbOpcoes;
    final View mVwCor;

    public LembreteViewHolder(@NonNull View itemView, LembreteItemListener itemListener) {
        super(itemView);
        mTvTitulo = itemView.findViewById(R.id.lembrete_titulo);
        mTvDescricao = itemView.findViewById(R.id.lembrete_descricao);
        mTvData = itemView.findViewById(R.id.lembrete_data);
        mTvHora = itemView.findViewById(R.id.lembrete_hora);
        mIbOpcoes = itemView.findViewById(R.id.lembrete_opcoes);
        mTvRepeticao = itemView.findViewById(R.id.lembrete_repeticao);
        mVwCor = itemView.findViewById(R.id.lembrete_cor);
        mTvDisciplina = itemView.findViewById(R.id.lembrete_disciplina);
        mTvTipo = itemView.findViewById(R.id.lembrete_tipo);

        itemView.setOnClickListener(v -> itemListener.onLembreteClick(getAdapterPosition()));
        mIbOpcoes.setOnClickListener(v -> itemListener.onOpcoesClick(getAdapterPosition()));
    }
}