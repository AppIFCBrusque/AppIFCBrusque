package com.ifcbrusque.app.ui.home.lembretes;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    public final TextView mTitulo;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        mTitulo = itemView.findViewById(R.id.secao_lembretes_titulo);
    }
}