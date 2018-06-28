package cl.zcloud.www.inventariolotes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.clases.Lotes;

public class AdaptadorListaLotes extends RecyclerView.Adapter<AdaptadorListaLotes.ViewHolder> {
    private ArrayList<Lotes> items;
    private OnItemClickListener itemClickListener;

    public AdaptadorListaLotes( ArrayList<Lotes> items, OnItemClickListener itemClickListener) {
        this.items = items;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public interface OnItemClickListener { void onItemClick(Lotes item); }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup,int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_fourth_level, viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.bind(items.get(i), itemClickListener, i);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lote, numeroLista;

        public ViewHolder( View tv) {
            super(tv);
            lote = tv.findViewById(R.id.lblListUbicacion);
            numeroLista = tv.findViewById(R.id.numeroLista);
        }

        public void bind(final Lotes item, final OnItemClickListener listener, int position){
            lote.setText(item.getLote());
            numeroLista.setText(position+"");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}