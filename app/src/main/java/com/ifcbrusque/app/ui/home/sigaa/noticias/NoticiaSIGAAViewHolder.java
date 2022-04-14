package com.ifcbrusque.app.ui.home.sigaa.noticias;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;

public class NoticiaSIGAAViewHolder extends RecyclerView.ViewHolder {
    final TextView mTvTitulo, mTvDisciplina;

    public NoticiaSIGAAViewHolder(@NonNull View itemView, NoticiaSIGAAItemListener noticiaSIGAAItemListener) {
        super(itemView);
        mTvTitulo = itemView.findViewById(R.id.noticia_sigaa_titulo);
        mTvDisciplina = itemView.findViewById(R.id.noticia_sigaa_disciplina);
        itemView.setOnClickListener(v -> noticiaSIGAAItemListener.onClick(getAdapterPosition()));
    }
}
