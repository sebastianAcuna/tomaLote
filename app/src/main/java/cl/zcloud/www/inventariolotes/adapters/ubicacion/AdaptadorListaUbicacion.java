package cl.zcloud.www.inventariolotes.adapters.ubicacion;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;

public class AdaptadorListaUbicacion extends RecyclerView.Adapter<AdaptadorListaUbicacion.ViewHolder> implements Filterable{
    private List<Ubicacion> items;
    private List<Ubicacion> mStringFilterList;
    private ValueFilter valueFilter;
    private OnItemClickListener itemClickListener;
    private OnLongItemClickLintener longItemClickLintener;


    public AdaptadorListaUbicacion(List<Ubicacion> items, OnItemClickListener itemClickListener, OnLongItemClickLintener onLongClickListener) {
        this.items = items;
        this.mStringFilterList = items;
        this.itemClickListener = itemClickListener;
        this.longItemClickLintener = onLongClickListener;
        getFilter();
    }

    @NonNull
    @Override
    public AdaptadorListaUbicacion.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_ubicacion, parent, false);

        return new AdaptadorListaUbicacion.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorListaUbicacion.ViewHolder holder, int position) {
        holder.bind(items.get(position), itemClickListener, longItemClickLintener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        if (valueFilter==null){valueFilter=new ValueFilter();}
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results=new FilterResults();
            if(constraint!=null && constraint.length()>0){
                ArrayList<Ubicacion> filterList=new ArrayList<>();
                if (mStringFilterList.size() > 0){
                    for (int i = 0; i < mStringFilterList.size(); i++){
                        if ((mStringFilterList.get(i).getDescripcionUbicacion().toUpperCase()).contains(constraint.toString().toUpperCase())){
                            Ubicacion ubicacion = new Ubicacion();
                            ubicacion.setIdUbicacion(mStringFilterList.get(i).getIdUbicacion());
                            ubicacion.setDescripcionUbicacion(mStringFilterList.get(i).getDescripcionUbicacion());

                            filterList.add(ubicacion);
                        }
                    }
                }
                results.count= filterList.size();
                results.values= filterList;

            }else{
                results.count=mStringFilterList.size();
                results.values=mStringFilterList;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items=(ArrayList<Ubicacion>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener { void onItemClick(Ubicacion item); }
    public interface OnLongItemClickLintener { void onLongItemClick(Ubicacion item); }


    public static  class ViewHolder extends RecyclerView.ViewHolder{
        private TextView et_nombre;

        public ViewHolder(View itemView) {
            super(itemView);
            et_nombre = itemView.findViewById(R.id.et_nombre);
        }
        public void bind(final Ubicacion item, final AdaptadorListaUbicacion.OnItemClickListener listener, final AdaptadorListaUbicacion.OnLongItemClickLintener longListener) {

            if (item != null) {
                et_nombre.setText(item.getDescripcionUbicacion());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(item);
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        longListener.onLongItemClick(item);
                        return true;
                    }
                });

            } else {
                et_nombre.setText("Nada que mostrar");
            }
        }
    }
}
