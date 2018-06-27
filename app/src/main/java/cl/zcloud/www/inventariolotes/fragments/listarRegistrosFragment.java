package cl.zcloud.www.inventariolotes.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.adapters.lotes.AdaptadorListaPrimerNivel;
import cl.zcloud.www.inventariolotes.clases.Lotes;

public class listarRegistrosFragment extends Fragment {

    ExpandableListView expandableListView;
    AdaptadorListaPrimerNivel listAdapter;
    ArrayList<String> listDataHeader;
    HashMap<String, List<String>> listDataUbicacion;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, List<String>> listDataCalles;


//    List<String[]> secondLevel = new ArrayList<>();

//    List<LinkedHashMap<String, String[]>> data = new ArrayList<>();



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listar_registros_fragment, container, false);

        //lista expandible
        expandableListView = view.findViewById(R.id.expandable_lv);

        //cargar arrays;
        prepareListData();

        // parent adapter
//        ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(getActivity(), parent, secondLevel, data);

        listAdapter = new AdaptadorListaPrimerNivel(getActivity(), listDataHeader, /*listDataUbicacion,*/ listDataCalles, listDataChild);

        // set adapter
        expandableListView.setAdapter( listAdapter );





        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getActivity(), "click en child", Toast.LENGTH_SHORT).show();
                return true;
            }

        });

        expandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_GROUP){
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
//                    int childPosition = ExpandableListView.getPackedPositionChild(id);

                    Toast.makeText(getActivity(), "click largo en parent", Toast.LENGTH_SHORT).show();

                    return true;
                }
                return true;
            }
        });


        return view;
    }


    private void prepareListData() {


        List<Lotes> listaFechas = MainActivity.myAppDB.myDao().getFechasLotes();
        List<String> listaLotes;
        List<String> listaUbicaciones;
        List<Lotes> listaCalles = null;


        listDataHeader = new ArrayList<>();  //fechas_ubicacion
        listDataCalles = new HashMap<>();//ubicacion_calle
        listDataChild = new HashMap<>(); //calle_lote
        listDataUbicacion = new HashMap<>();

        if (listaFechas.size() > 0){

            for (Lotes fecha_ubicacion : listaFechas){

                listDataHeader.add(fecha_ubicacion.getFechaInventario() + "_" + fecha_ubicacion.getDescUbicacionLote()); //2018-06-27_ubicacion1
//                System.out.println("SE INSERTAN FECHAS " + fecha_ubicacion);

//                listaUbicaciones = MainActivity.myAppDB.myDao().getUbicacionesLotesByFecha(fecha_ubicacion.getFechaInventario());
//                if (listaUbicaciones.size() > 0){

                    /*ArrayList<String> fechaUbi = new ArrayList<>();
                    for (String ubicaciones : listaUbicaciones) {
                        fechaUbi.add(fecha_ubicacion + "_" + ubicaciones);
                    }*/


//                    listDataUbicacion.put(fecha_ubicacion, fechaUbi);
//                    System.out.println("lista ubicaciones " + listDataUbicacion.values().toString());

//                    for (String ubicaciones : listaUbicaciones){

                        listaCalles = MainActivity.myAppDB.myDao().getLotesByFechaAndUbicacion(fecha_ubicacion.getFechaInventario(),fecha_ubicacion.getDescUbicacionLote());

                        if (listaCalles.size() > 0){
                            ArrayList<String> ubiCalle = new ArrayList<>();
                            for (Lotes calles : listaCalles){
                                ubiCalle.add(fecha_ubicacion.getFechaInventario()+ "_" +calles.getDescUbicacionLote()+"_"+calles.getCalle());
                            }


                            listDataCalles.put(fecha_ubicacion.getFechaInventario() + "_" + fecha_ubicacion.getDescUbicacionLote(), ubiCalle);
                            for (Lotes calles : listaCalles){
//                                System.out.println("SE INSERTAN CALLES " + calles);

                                listaLotes = MainActivity.myAppDB.myDao().getLotesByFechaUbicacionAndCalle(fecha_ubicacion.getFechaInventario(), calles.getDescUbicacionLote(),calles.getCalle());
                                for (int  e = 0 ; e < listaLotes.size(); e++){
                                    listDataChild.put(fecha_ubicacion.getFechaInventario() + "_" + calles.getDescUbicacionLote() + "_" + calles.getCalle(), listaLotes);
//                                    System.out.println("SE INSERTAN lotes " + listaLotes.get(e));
                                }

                            }

                        }
//                    for (int i = 0; i < listaUbicaciones.size();i++){

//                    }

                   /* for (String ubicacion : listaUbicaciones){



                    }*/
//                }

            }
        }


/*        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");


        for (int i = 0; i < listDataHeader.size(); i++){
            Lotes lotes = new Lotes();
            peliculas.setNombre(listDataHeader.get(i));
            peliculas.setPeliculasList(top250);
            listDataChild.put(peliculas.getNombre(), peliculas.getPeliculasList()); // Header, Child data
        }*/
    }
}
