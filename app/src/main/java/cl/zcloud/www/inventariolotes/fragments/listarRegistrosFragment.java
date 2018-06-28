package cl.zcloud.www.inventariolotes.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.adapters.AdaptadorListaLotes;
import cl.zcloud.www.inventariolotes.adapters.lotes.AdaptadorListaPrimerNivel;
import cl.zcloud.www.inventariolotes.adapters.lotes.AdaptadorListaTercerNivel;
import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;

import static android.content.Context.MODE_PRIVATE;

public class listarRegistrosFragment extends Fragment {

    ExpandableListView expandableListView;
    AdaptadorListaTercerNivel listAdapter;
    ArrayList<Lotes> arrlistaLote;
    ArrayList<String> descFechas,descUbicacion, descCalles;
    LinearLayout mStitchingWorksListView;

    private int mInitialHeight= 0;

    Spinner fechas,ubicaciones,calles;
    RecyclerView RecyclerlistaLotes;
    AdaptadorListaLotes adapterListaLotes;

    ArrayAdapter<String> spinnerAdapterFechas, spinnerAdapterUbicacion, spinnerAdapterCalles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listar_registros_fragment, container, false);

        //lista expandible
//        expandableListView = view.findViewById(R.id.expandable_lv);
        ubicaciones = view.findViewById(R.id.spinner_ubicacion);
        fechas = view.findViewById(R.id.spinner_fechas);
        calles = view.findViewById(R.id.spinner_calles);
        RecyclerlistaLotes = view.findViewById(R.id.lista_lotes);


        //cargar arrays;
        prepararListaFechas();



        /*expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                //Other Expansion/Collapsing Logic
                setListHeightToWrap();
                return true;
            }
        });*/

       /* expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) expandableListView.getLayoutParams();
                //The Logic here will change as per your requirements and the height of each of the children in the group
                if (listAdapter.getChildrenCount(groupPosition) > 6) {
                    params.height = 9 * mInitialHeight;
                } else {
                    params.height = 6 * mInitialHeight;
                }
                //For Last Group in the list and the number of children were less as compared to other groups
                if (groupPosition == listAdapter.getGroupCount() - 1) {
                    params.height = 3 * mInitialHeight;
                }
                expandableListView.setLayoutParams(params);
                expandableListView.refreshDrawableState();
                expandableListView.refreshDrawableState();
            }
        });*/


        return view;
    }



   /*public void setListHeightToWrap(){
       LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) expandableListView.getLayoutParams();
       params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
       expandableListView.setLayoutParams(params);
       expandableListView.refreshDrawableState();
       mScrollView.refreshDrawableState();
    }*/



    private void prepararListaFechas(){

//        listaFechasSpinner = new ArrayList<>();
        descFechas = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<Lotes> listaFechas = MainActivity.myAppDB.myDao().getFechasLotes();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listaFechas.size() > 0){

//                            listaFechasSpinner.addAll(listaFechas);

                            for (int i = 0; i < listaFechas.size(); i++){
//                                idUbicacion.add(listaUbicacion.get(i).getIdUbicacion());
                                descFechas.add(listaFechas.get(i).getFechaInventario());
                            }

                        }else{
                            descFechas.add("No tiene Fechas agregadas");
                        }
                        spinnerAdapterFechas = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,descFechas);
                        fechas.setAdapter(spinnerAdapterFechas);
                        fechas.setSelection(0);

                        fechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                System.out.println(descFechas.get(position));
                               prepararListaUbicacion(descFechas.get(position));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                });
            }
        });
    }

    public void prepararListaUbicacion(final String fecha){

        descUbicacion = new ArrayList<>();
//        listaUbicacionSpinner = new ArrayList<>();
                final List<Lotes> listaUbicaciones = MainActivity.myAppDB.myDao().getUbicacionesLotesByFecha(fecha);
                if (listaUbicaciones.size() > 0){
//                    listaUbicacionSpinner.addAll(listaUbicaciones);
                    for (int i = 0; i < listaUbicaciones.size(); i++){
//                                idUbicacion.add(listaUbicacion.get(i).getIdUbicacion());
//                        System.out.println(listaUbicaciones.get(i).getDescUbicacionLote());
                        descUbicacion.add(listaUbicaciones.get(i).getDescUbicacionLote());
                    }

                }else{
                    descUbicacion.add("No tiene ubicaciones agregadas");
                }
                spinnerAdapterUbicacion = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,descUbicacion);
                ubicaciones.setAdapter(spinnerAdapterUbicacion);
                ubicaciones.setSelection(0);

                ubicaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        prepareCalles(fecha, descUbicacion.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    private void prepareCalles(final String fecha, final String ubicacion) {

        descCalles = new ArrayList<>();
        final List<Lotes> listaCalles = MainActivity.myAppDB.myDao().getLotesByFechaAndUbicacion(fecha,ubicacion);
        if (listaCalles.size() > 0){
            for (int i = 0; i < listaCalles.size(); i++){
                descCalles.add(listaCalles.get(i).getCalle()+"");
            }

        }else{
            descCalles.add("No tiene calles agregadas");
        }
        spinnerAdapterCalles = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,descCalles);
        calles.setAdapter(spinnerAdapterCalles);
        calles.setSelection(0);

        calles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                llenarLista(fecha, ubicacion, Integer.parseInt(descCalles.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }
    public void llenarLista(String fecha, String ubicacion, int calle){

        List<Lotes> listaLotes = MainActivity.myAppDB.myDao().getLotesByFechaUbicacionAndCalle(fecha, ubicacion, calle);

        if (listaLotes.size()>0){
            RecyclerlistaLotes.setHasFixedSize(true);
            RecyclerView.LayoutManager lManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()));
            RecyclerlistaLotes.setLayoutManager(lManager);

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
                }
            });
            RecyclerlistaLotes.setAdapter(adapterListaLotes);
        }
    }


//    private void setExpandableListViewHeight(ExpandableListView listView) {
//        try {
//            ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
//            int totalHeight = 0;
//            for (int i = 0; i < listAdapter.getGroupCount(); i++) {
//                View listItem = listAdapter.getGroupView(i, false, null, listView);
////                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(99999999, View.MeasureSpec.AT_MOST);
//                listItem.measure(0, 0);
//                totalHeight += listItem.getMeasuredHeight();
//            }
//
//            ViewGroup.LayoutParams params = listView.getLayoutParams();
//            int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount()));
//            if (height < 10) height = 300;
//            params.height = height;
//            listView.setLayoutParams(params);
//            listView.requestLayout();
//            mScrollView.post(new Runnable() {
//                public void run() {
//                    mScrollView.fullScroll(ScrollView.FOCUS_UP);
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
