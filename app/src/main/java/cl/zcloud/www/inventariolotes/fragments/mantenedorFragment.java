package cl.zcloud.www.inventariolotes.fragments;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.adapters.ubicacion.AdaptadorListaUbicacion;
import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;
import es.dmoral.toasty.Toasty;

public class mantenedorFragment extends Fragment {
    private ImageButton imageButton;
    private EditText add_ubicacion_et;
    private RecyclerView recycler_ubicaciones;
    private AdaptadorListaUbicacion adapterUbicaciones;
    private RecyclerView.LayoutManager lManager;

//    private String  REGULAR_EXPRESION = "(\\S\\w+\\s)";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mantenedor_fragment, container, false);

        imageButton = (ImageButton) view.findViewById(R.id.imageButton);
        add_ubicacion_et = (EditText) view.findViewById(R.id.add_ubicacion_et);
        recycler_ubicaciones = (RecyclerView) view.findViewById(R.id.recycler_ubicaciones);

        cargarUbicaciones();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarUbicacion();
            }
        });


        add_ubicacion_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle = false;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    agregarUbicacion();
                    handle = true;
                }
                return handle;
            }
        });

        return view;
    }

    public void agregarUbicacion(){

//        Pattern patron = Pattern.compile(REGULAR_EXPRESION);
//        String stLote = add_ubicacion_et.getText().toString().trim();
        if (TextUtils.isEmpty(add_ubicacion_et.getText())){
            Toasty.warning(Objects.requireNonNull(getActivity()), "Debe ingresar una ubicación", Toast.LENGTH_SHORT, true).show();
            add_ubicacion_et.requestFocus();
        }else {
/*            if( !patron.matcher(stLote).matches()){
                Toasty.warning(Objects.requireNonNull(getActivity()), "Solo se aceptan letras y numeros", Toast.LENGTH_SHORT, true).show();
            }else {*/

                Ubicacion ubicacion = new Ubicacion();
                ubicacion.setDescripcionUbicacion(add_ubicacion_et.getText().toString());

                try {
                    long id = MainActivity.myAppDB.myDao().insertarUbicacion(ubicacion);
                    if (id > 0) {
                        Toasty.success(Objects.requireNonNull(getActivity()), "Insertado con exito", Toast.LENGTH_SHORT, true).show();
                        cargarUbicaciones();
                        add_ubicacion_et.setText("");
                        add_ubicacion_et.requestFocus();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//            }
        }
    }

    public void cargarUbicaciones(){
        final ArrayList<Ubicacion> listaUbicacion = new ArrayList<>();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<Ubicacion> listUb = MainActivity.myAppDB.myDao().getUbicacionOrderDesc();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listUb.size() > 0){
                            listaUbicacion.addAll(listUb);

                            recycler_ubicaciones.setHasFixedSize(true);
                            lManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()));
                            recycler_ubicaciones.setLayoutManager(lManager);
                            adapterUbicaciones = new AdaptadorListaUbicacion(listaUbicacion, new AdaptadorListaUbicacion.OnItemClickListener() {
                                @Override
                                public void onItemClick(Ubicacion item) {
                                    showAlertForUpdate(item.getDescripcionUbicacion(),item.getIdUbicacion());
                                }
                            }, new AdaptadorListaUbicacion.OnLongItemClickLintener() {
                                @Override
                                public void onLongItemClick(Ubicacion item) {
                                    showAlertForDelete(item.getDescripcionUbicacion(),item.getIdUbicacion());
                                }
                            });

                            recycler_ubicaciones.setAdapter(adapterUbicaciones);
                        }
                    }
                });
            }
        });

    }



    private void showAlertForUpdate(final String desc, final int id){

        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_edit_ubicacion,null);
        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle("Editando "+ desc)
                .setPositiveButton("ingresar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setNegativeButton("cancelar",null).create();

        final EditText txt = viewInfalted.findViewById(R.id.edit_ubicacion);
        txt.setText(desc);

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(txt.getText())){
                            Toasty.warning(Objects.requireNonNull(getActivity()), "Debe ingresar una nueva ubicacion", Toast.LENGTH_SHORT, true).show();
                            txt.requestFocus();

                        }else{
                            Ubicacion ubicacion = new Ubicacion();
                            ubicacion.setIdUbicacion(id);
                            ubicacion.setDescripcionUbicacion(txt.getText().toString());
                            try {
                                int idUpdate = MainActivity.myAppDB.myDao().updateUbicacion(ubicacion);
                                if (idUpdate > 0){
                                    cargarUbicaciones();
                                    Toasty.success(Objects.requireNonNull(getActivity()), "Actualizado con exito",Toast.LENGTH_SHORT,true).show();
                                    builder.dismiss();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }



    private void showAlertForDelete(final String desc, final int id){

        final AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        if(desc != null) builder.setTitle("Eliminando "+ desc);
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_delete_ubicacion,null);
        builder.setView(viewInfalted);
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                List<Lotes> lotesList = MainActivity.myAppDB.myDao().getLotesByUbicacion(id);
                if (lotesList.size()>0){
                    Toasty.error(Objects.requireNonNull(getActivity()),
                            "Esta ubicacion se encuentra en " + lotesList.size() + " Lotes, no se eliminará ",
                            Toast.LENGTH_LONG,true).show();
                }else{
                    Ubicacion ubicacion = new Ubicacion();
                    ubicacion.setIdUbicacion(id);
                    ubicacion.setDescripcionUbicacion(desc);

                    try {
                        int idUpdate = MainActivity.myAppDB.myDao().deleteUbicacion(ubicacion);
                        if (idUpdate > 0){
                            cargarUbicaciones();
                            Toasty.success(Objects.requireNonNull(getActivity()), "Eliminado con exito",Toast.LENGTH_SHORT,true).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }
}
