package cl.zcloud.www.inventariolotes.fragments;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.adapters.lotes.AdaptadorDetalleListaLote;
import cl.zcloud.www.inventariolotes.adapters.lotes.AdaptadorListaTercerNivel;
import cl.zcloud.www.inventariolotes.clases.Lotes;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class listarRegistrosFragment extends Fragment {

    ExpandableListView expandableListView;
    AdaptadorListaTercerNivel listAdapter;
    ArrayList<Lotes> arrlistaLote;
    ArrayList<String> descFechas,descUbicacion, descCalles,DescCallesTotal;
    LinearLayout mStitchingWorksListView;

    private int mInitialHeight= 0;

    Spinner fechas,ubicaciones,calles;
    RecyclerView RecyclerlistaLotes;
    AdaptadorDetalleListaLote adapterListaLotes;

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
//        calles = view.findViewById(R.id.spinner_calles);
        RecyclerlistaLotes = view.findViewById(R.id.lista_lotes);

        //cargar arrays;
        prepararListaFechas();

        //preparar longclicks
        longClickSpinners();

        return view;
    }



    private void longClickSpinners(){
/*        final Handler longFechaHandler = new Handler();
        final Runnable runnableFechas = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), fechas.getSelectedItem().toString()+"Long click performed", Toast.LENGTH_SHORT).show();
            }
        };
        fechas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    longFechaHandler.postDelayed(runnableFechas, 1000);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    longFechaHandler.removeCallbacks(runnableFechas);
                }
                return false;

            }
        });*/

        final Handler actionUbicacionHandler = new Handler();
        final Runnable runnableUbicacion = new Runnable() {
            @Override
            public void run() {
                if (!ubicaciones.getSelectedItem().toString().equals("Todos")){
                showAlertForEdit(
                        "Editando Ubicacion",
                        "Se editará la ubicacion a todos los lotes que pertenezcan a " + ubicaciones.getSelectedItem().toString(),
                        ubicaciones.getSelectedItem().toString(),
                        fechas.getSelectedItem().toString(),
                        0,
                        0);
                }else{
                    Toasty.error(Objects.requireNonNull(getActivity()), "No puede editar \"Todos\" ", Toast.LENGTH_SHORT,true).show();
                }
            }
        };

        ubicaciones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    actionUbicacionHandler.postDelayed(runnableUbicacion, 1000);
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    actionUbicacionHandler.removeCallbacks(runnableUbicacion);
                }
                return false;

            }
        });
    }

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

                        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
                        int fecha_a_detalle_position = sharedPref.getInt("fecha_a_detalle_position",0);
                        fechas.setSelection(fecha_a_detalle_position);

                        fechas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("fecha_a_detalle_position", fechas.getSelectedItemPosition());
                                editor.apply();
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
                    descUbicacion.add("Todos");
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

                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
                int fecha_a_detalle_position = sharedPref.getInt("fecha_a_ubicacion_position",0);
                ubicaciones.setSelection(fecha_a_detalle_position);


                ubicaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt("fecha_a_ubicacion_position", ubicaciones.getSelectedItemPosition());
                        editor.apply();

                        llenarLista(fecha, descUbicacion.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

    }

    public void llenarLista(String fecha, final String ubicacion){

        final List<Lotes> listaCalles;
        if (ubicacion.equals("Todos")){
            listaCalles = MainActivity.myAppDB.myDao().getLotesByFecha(fecha);
        }else{
            listaCalles = MainActivity.myAppDB.myDao().getLotesByFechaAndUbicacion(fecha,ubicacion);
        }


        if (listaCalles.size()>0){
            RecyclerlistaLotes.setHasFixedSize(true);
            RecyclerView.LayoutManager lManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()));
            RecyclerlistaLotes.setLayoutManager(lManager);

            arrlistaLote = new ArrayList<>();
            for (Lotes lts : listaCalles){
                Lotes lotes = new Lotes();
                lotes.setFechaInventario(lts.getFechaInventario());
                lotes.setCalle(lts.getCalle());
                lotes.setEstado(lts.getEstado());
                lotes.setDescUbicacionLote(lts.getDescUbicacionLote());
                arrlistaLote.add(lotes);
            }
            adapterListaLotes = new AdaptadorDetalleListaLote(arrlistaLote, new AdaptadorDetalleListaLote.OnItemClickListener() {
                @Override
                public void onItemClick(Lotes item) {
                    SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("fecha_a_detalle", item.getFechaInventario());
                    if (!ubicacion.equals("Todos")){
                        editor.putString("ubicacion_a_detalle", item.getDescUbicacionLote());
                    }else{
                        editor.putString("ubicacion_a_detalle", "");
                    }
                    editor.putInt("calle_a_detalle", item.getCalle());
                    editor.apply();

                    Class fragmentClass = detalleLoteFragment.class;
                    cambiarFragment(fragmentClass);
                }
            }, new AdaptadorDetalleListaLote.OnLongClickListener() {
                @Override
                public void OnLongItemClick(Lotes item) {
                    showAlertForEdit(
                            "Editando calle ",
                            "Se editaran todos los lotes con la calle " + item.getCalle(),
                            ubicaciones.getSelectedItem().toString(),
                            fechas.getSelectedItem().toString(),
                            item.getCalle(),
                            1);
                }
            });
            RecyclerlistaLotes.setAdapter(adapterListaLotes);
        }
    }

    public void cambiarFragment( Class fragmentClass){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).addToBackStack(null).commit();
//        .addToBackStack(null)
    }


    private void showAlertForEdit(String title, String message, final String oldData , final String fecha, final int oldCalle, final int accion) {
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_edit_list_todos, null);

        final TextView tv_txt = viewInfalted.findViewById(R.id.texto_edit_todo);
        final EditText txt = viewInfalted.findViewById(R.id.et_edit_todo);

        if (accion == 1){
            txt.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        tv_txt.setText(message);
        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle(title)
                .setPositiveButton("Editar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setNegativeButton("cancelar",null).create();

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(txt.getText())){
                            Toasty.error(Objects.requireNonNull(getActivity()), "Debe ingresar algo.", Toast.LENGTH_SHORT, true).show();
                        }else {
                            if (accion == 0){
                                try{
                                    int cambioUbic = MainActivity.myAppDB.myDao().updateUbicacionList(txt.getText().toString(), oldData);
                                    if (cambioUbic > 0){
                                        int cambio = MainActivity.myAppDB.myDao().updateLotesByUbicacion(txt.getText().toString(), oldData, fecha); //nueva ubicacion , antigua ubicacion, fecha
                                        if (cambio > 0){
                                            Toasty.success(Objects.requireNonNull(getActivity()), "Se actualizo la ubicacion en " + cambio + " lotes ").show();
                                            prepararListaFechas();
                                        }
                                    }
                                }catch (Exception e ){
                                    e.printStackTrace();
                                }
                            }

                            if (accion == 1){
                                try{
                                    int cambio = MainActivity.myAppDB.myDao().updateLotesByCalle(Integer.parseInt(txt.getText().toString()), oldCalle, oldData, fecha);
                                    if (cambio > 0){
                                        Toasty.success(Objects.requireNonNull(getActivity()), "Se actualizo la calle en " + cambio + " lotes ").show();
                                        prepararListaFechas();
                                    }
                                }catch (Exception e ){
                                    e.printStackTrace();
                                }
                            }
                            builder.dismiss();
                        }
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
