package cl.zcloud.www.inventariolotes.fragments;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class adminFragment extends Fragment {
    private Button deleteAll,deleteCon,deleteSin, btn_delete_ubicaciones;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment, container,false);

        deleteAll = view.findViewById(R.id.btn_delete_all);
        deleteCon = view.findViewById(R.id.btn_delete_sinc);
        deleteSin = view.findViewById(R.id.btn_delete_no_sinc);
        btn_delete_ubicaciones = view.findViewById(R.id.btn_delete_ubicaciones);

        showAlertForBloqueo();


        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForDesition(2);
            }
        });


        deleteCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForDesition(1);
            }
        });


        deleteSin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForDesition(0);
            }
        });


        btn_delete_ubicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForDesitionUbicacion();
            }
        });
        return view;
    }

    private void deleteData(final int i) {
        List<Lotes> listLotes;
                if (i < 2){
                     listLotes = MainActivity.myAppDB.myDao().getLotesByEstado(i);
                }else{
                    listLotes = MainActivity.myAppDB.myDao().getLotes();
                }

                if (listLotes.size() > 0) {
                    int deleteds = 0;
                    if (i < 2) {
                        deleteds = MainActivity.myAppDB.myDao().deleteLotesBySync(i);
                    } else {
                        deleteds = MainActivity.myAppDB.myDao().deleteLotes();
                    }
                    if (deleteds > 0) {
                        Toasty.success(Objects.requireNonNull(getActivity()), "Datos eliminados con exito", Toast.LENGTH_SHORT, true).show();
                    } else {
                        Toasty.error(Objects.requireNonNull(getActivity()), "Algo sucedio, no se elimino todo ", Toast.LENGTH_SHORT, true).show();
                    }
                } else {
                    showAlertForError("Todo bien", "No hay datos para eliminar");
                }
    }

    private void deleteUbic() {
        List<Ubicacion> listUbicaciones = MainActivity.myAppDB.myDao().getUbicacion();
        if (listUbicaciones.size() > 0) {
            int deleteds  = MainActivity.myAppDB.myDao().deleteUbicaciones();

            if (deleteds > 0) {
                Toasty.success(Objects.requireNonNull(getActivity()), "Datos eliminados con exito", Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.error(Objects.requireNonNull(getActivity()), "Algo sucedio, no se elimino todo ", Toast.LENGTH_SHORT, true).show();
            }
        } else {
            showAlertForError("Todo bien", "No hay datos para eliminar");
        }
    }


    private void showForDesition(final int accion){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_empty,null);
        String msj = "";
        switch (accion){
            case 2: msj = "todos los lotes "; break;
            case 1: msj = "los lotes sincronizados "; break;
            case 0: msj = "los lotes sin sincronizar"; break;
            default: msj = "";
        }

        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle(" ¿ Esta seguro que desea eliminar " + msj + " ? ")
                .setPositiveButton("si, estoy seguro ", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setNegativeButton("cancelar",null).create();

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                Button n = builder.getButton(AlertDialog.BUTTON_NEGATIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteData(accion);
                        builder.dismiss();
                    }
                });

                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    private void showForDesitionUbicacion(){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_empty,null);
        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle(" ¡ ALERTA  ! ")
                .setMessage("¿Está seguro que desea eliminar todas las ubicaciones ?")
                .setPositiveButton("si, estoy seguro ", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setNegativeButton("cancelar",null).create();

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                Button n = builder.getButton(AlertDialog.BUTTON_NEGATIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteUbic();
                        builder.dismiss();
                    }
                });

                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    private void showAlertForError(String title, String message){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_empty,null);
        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ok ", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create();

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       builder.dismiss();
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    private void showAlertForBloqueo(){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_bloqueo,null);
        final EditText txt = viewInfalted.findViewById(R.id.edit_bloqueo);

        SimpleDateFormat fcA = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date fHa = new Date();
        String fActual = fcA.format(fHa);

        String[] fechaAGuardar = TextUtils.split(fActual, "-");
        //String[] diaFecha = TextUtils.split(fechaAGuardar[0],"");

        final int password = (Integer.parseInt(fechaAGuardar[0]) +1);

        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle("SOLO ADMINISTRADORES")
                .setPositiveButton("ingresar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setNegativeButton("cancelar",null).create();

        builder.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                Button n = builder.getButton(AlertDialog.BUTTON_NEGATIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(txt.getText())){
                            Toasty.error(Objects.requireNonNull(getActivity()), "Debe ingresar la contraseña", Toast.LENGTH_SHORT, true).show();
                        }else{
                            if (Integer.parseInt(txt.getText().toString()) == password){
                                Toasty.success(Objects.requireNonNull(getActivity()), "Acceso consedido.", Toast.LENGTH_SHORT, true).show();
                                builder.dismiss();
                            }else{
                                Toasty.error(Objects.requireNonNull(getActivity()), "De vuelta a inicio", Toast.LENGTH_SHORT, true).show();
                                Class fragmentClass = homeFragment.class;
                                cambiarFragment(fragmentClass);
                                builder.dismiss();
                            }
                        }
                    }
                });

                n.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Class fragmentClass = homeFragment.class;
                        cambiarFragment(fragmentClass);
                        builder.dismiss();
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    public void cambiarFragment( Class fragmentClass){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
//        .addToBackStack(null)
    }
}
