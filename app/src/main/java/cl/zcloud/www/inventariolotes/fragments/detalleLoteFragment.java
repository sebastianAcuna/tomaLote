package cl.zcloud.www.inventariolotes.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.adapters.lotes.AdaptadorListaLotes;
import cl.zcloud.www.inventariolotes.clases.Lotes;

import static android.content.Context.MODE_PRIVATE;

public class detalleLoteFragment extends Fragment {

    RecyclerView recyclerView;
    AdaptadorListaLotes adapterListaLotes;
    ArrayList<Lotes> arrlistaLote;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detalle_lote_fragment, container, false);

        recyclerView = view.findViewById(R.id.detalle_lote_list);


        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
        String fecha_a_detalle = sharedPref.getString("fecha_a_detalle","");
        String ubicacion_a_detalle = sharedPref.getString("ubicacion_a_detalle","");
        int calle_a_detalle = sharedPref.getInt("calle_a_detalle",0);


//cargar arrays;


        prepararListaFechas(fecha_a_detalle, ubicacion_a_detalle, calle_a_detalle);


        return view;
    }

    public void prepararListaFechas(String fecha, String ubicacion, int calle){
        List<Lotes> listaLotes;
        if (ubicacion.equals("")){
            listaLotes = MainActivity.myAppDB.myDao().getLotesByFechaAndCalle(fecha, calle);
        }else{
            listaLotes = MainActivity.myAppDB.myDao().getLotesByFechaUbicacionAndCalle(fecha, ubicacion, calle);
        }


        if (listaLotes.size()>0){
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager lManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()));
            recyclerView.setLayoutManager(lManager);

            arrlistaLote = new ArrayList<>();
            for (Lotes lts : listaLotes){
                Lotes lotes = new Lotes();
                lotes.setFechaDispo(lts.getFechaDispo());
                lotes.setLote(lts.getLote());
                lotes.setUsuarioInventario(lts.getUsuarioInventario());
                lotes.setFechaInventario(lts.getFechaInventario());
                lotes.setIdUbicacionLote(lts.getIdUbicacionLote());
                lotes.setCalle(lts.getCalle());
                lotes.setEstado(lts.getEstado());
                lotes.setDescUbicacionLote(lts.getDescUbicacionLote());
                arrlistaLote.add(lotes);
            }
            adapterListaLotes = new AdaptadorListaLotes( arrlistaLote, new AdaptadorListaLotes.OnItemClickListener() {
                @Override
                public void onItemClick(Lotes item) {
                    SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("fecha_a_detalle", item.getFechaInventario());
                    editor.putString("ubicacion_a_detalle", item.getDescUbicacionLote());
                    editor.putString("calle_a_detalle", item.getCalle()+"");
                    editor.apply();
                }
            });
            recyclerView.setAdapter(adapterListaLotes);
        }
    }
}
