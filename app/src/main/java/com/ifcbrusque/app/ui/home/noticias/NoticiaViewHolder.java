package com.ifcbrusque.app.ui.home.noticias;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;

public class NoticiaViewHolder extends RecyclerView.ViewHolder {
    final TextView mTvTitulo;
    final TextView mTvData;
    final ImageView mIvPreview;

    public NoticiaViewHolder(@NonNull View itemView, NoticiaItemListener itemListener) {
        super(itemView);
        mTvTitulo = itemView.findViewById(R.id.preview_titulo);
        mTvData = itemView.findViewById(R.id.preview_data);
        mIvPreview = itemView.findViewById(R.id.preview_imagem);

        itemView.setOnClickListener(v -> itemListener.onPreviewClick(getAdapterPosition()));
    }
}