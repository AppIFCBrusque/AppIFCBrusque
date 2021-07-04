package com.ifcbrusque.app.ui.noticias;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ifcbrusque.app.R;
import com.ifcbrusque.app.data.noticias.classe.Preview;

import java.util.ArrayList;
import java.util.List;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class NoticiasAdapter extends RecyclerView.Adapter<NoticiasAdapter.ViewHolder> {

    List<Preview> previews;
    Context context;

    public NoticiasAdapter(Context context, List<Preview> previews) {
        this.context = context;
        this.previews = previews;
    }

    @NonNull
    @Override
    public NoticiasAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_noticia, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiasAdapter.ViewHolder holder, int position) {
        if(previews.get(position).getTitulo().length() > 0) holder.tvTitulo.setText(previews.get(position).getTitulo());
        if(previews.get(position).getDescricao().length() > 0) holder.tvPrevia.setText(previews.get(position).getDescricao() + " [...]");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.tvTitulo.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            holder.tvPrevia.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
        }
        //TODO: set onclick seria aqui
        //TODO: imagem da noticia
    }


    @Override
    public int getItemCount() {
        return previews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo, tvPrevia;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.noticia_titulo);
            tvPrevia = itemView.findViewById(R.id.noticia_previa);
        }
    }
}
