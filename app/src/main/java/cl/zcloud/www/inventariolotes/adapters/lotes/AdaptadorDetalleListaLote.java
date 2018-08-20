package cl.zcloud.www.inventariolotes.adapters.lotes;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.clases.Lotes;

public class AdaptadorDetalleListaLote extends RecyclerView.Adapter<AdaptadorDetalleListaLote.ViewHolder> {
    private ArrayList<Lotes> items;
    private OnItemClickListener itemClickListener;
    private OnLongClickListener itemLongClickListener;

    public AdaptadorDetalleListaLote( ArrayList<Lotes> items, OnItemClickListener itemClickListener,OnLongClickListener itemLongClickListener ) {
        this.items = items;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public interface OnItemClickListener { void onItemClick(Lotes item); }

    public interface OnLongClickListener{ void OnLongItemClick(Lotes item); }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_third_level, viewGroup,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.bind(items.get(i), itemClickListener, itemLongClickListener, i);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView calle, cantidadCalle;

        public ViewHolder( View tv) {
            super(tv);
            calle = tv.findViewById(R.id.lblListCalle);
            cantidadCalle = tv.findViewById(R.id.lblListCantidadCalle);
        }

        public void bind(final Lotes item, final OnItemClickListener listener, final OnLongClickListener longListener, int position){
            String cantidadLote = " Cantidad Lotes ( " + item.getEstado() + " )";
            String calles = "Calle : " + item.getCalle() + " ";

            calle.setText(calles);
            cantidadCalle.setText(cantidadLote);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longListener.OnLongItemClick(item);
                    return true;
                }
            });
        }
    }
}