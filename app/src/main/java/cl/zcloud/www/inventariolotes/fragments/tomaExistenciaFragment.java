package cl.zcloud.www.inventariolotes.fragments;


import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;
import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

public class tomaExistenciaFragment extends Fragment {

    private EditText etPlannedDate, calle, usuario, lote;
    private Spinner ubicacion;
    private TextView lbl_mensaje;

    private String nombreUbicacion;
    private int idUbic,estadoPantalla;

    ArrayList<String> descUbicacion;
    ArrayList<Integer> idUbicacion;
    ArrayAdapter<String> spinnerAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toma_existencia_fragment, container, false);

        etPlannedDate = (EditText) view.findViewById(R.id.datePicker);
        ubicacion = (Spinner) view.findViewById(R.id.spinner_hubicacion);
        calle = (EditText) view.findViewById(R.id.lbl_calle);
        lote = (EditText) view.findViewById(R.id.lbl_lote);
        Button button = (Button) view.findViewById(R.id.comprobar_btn);
        lbl_mensaje = (TextView) view.findViewById(R.id.lbl_mensaje);
        usuario = (EditText) view.findViewById(R.id.et_usuario);

        llenarSpinner();

        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
        String fecha_remember = sharedPref.getString("remember_fecha","");
        String usuario_remember = sharedPref.getString("remember_usuario","");
        estadoPantalla = sharedPref.getInt("remember_estado",0);


        if (estadoPantalla > 0){
            showAlertForBloqueo();
        }


        etPlannedDate.setText(fecha_remember);
        usuario.setText(usuario_remember);



        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarCampos();
            }
        });

        return view;
    }

    public void llenarSpinner(){

        final ArrayList<Ubicacion> listaUbicacion = new ArrayList<>();
        descUbicacion = new ArrayList<>();
        idUbicacion = new ArrayList<>();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<Ubicacion> listUb = MainActivity.myAppDB.myDao().getUbicacion();
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (listUb.size() > 0){

                            listaUbicacion.addAll(listUb);

                            for (int i = 0; i < listaUbicacion.size(); i++){
                                idUbicacion.add(listaUbicacion.get(i).getIdUbicacion());
                                descUbicacion.add(listaUbicacion.get(i).getDescripcionUbicacion());
                            }

                        }else{
                            idUbicacion.add(0);
                            descUbicacion.add("No tiene ubicaciones agregadas");
                        }
                        spinnerAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),android.R.layout.simple_list_item_1,descUbicacion);
                        ubicacion.setAdapter(spinnerAdapter);

                        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
                        int spinnerValue = sharedPref.getInt("remember_lugar",-1);
                        if(spinnerValue != -1){ ubicacion.setSelection(spinnerValue);}else{ubicacion.setSelection(0);}

                        ubicacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                idUbic = idUbicacion.get(position);
                                nombreUbicacion = descUbicacion.get(position);

                                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                int pc = ubicacion.getSelectedItemPosition();
                                editor.putInt("remember_lugar", pc); //SE ASIGNA VARIABLE
                                editor.apply();
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

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }



    private void validarCampos(){

        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
        estadoPantalla = sharedPref.getInt("remember_estado",0);

        if (estadoPantalla > 0 ){
            showAlertForBloqueo();
        }else {

            if (TextUtils.isEmpty(etPlannedDate.getText()) || TextUtils.isEmpty(usuario.getText()) || TextUtils.isEmpty(calle.getText()) || TextUtils.isEmpty(lote.getText())) {
                Toasty.info(Objects.requireNonNull(getActivity()), "Debe completar todos los campos", Toast.LENGTH_SHORT, true).show();
            } else {
                lbl_mensaje.setText("");
                lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.whiteText));
                lbl_mensaje.setTextColor(getResources().getColor(R.color.whiteText));

                String REGULAR_EXPRESION = "([L]{2}[0]+[1-4]+[A-Z]{2}[0-9]{5})";
                Pattern patron = Pattern.compile(REGULAR_EXPRESION);
                String stLote = lote.getText().toString().trim();
                if (patron.matcher(stLote).matches()) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("remember_fecha", etPlannedDate.getText().toString());
                    editor.putString("remember_usuario", usuario.getText().toString());
                    editor.putInt("remember_lugar", ubicacion.getSelectedItemPosition());
                    editor.putInt("remember_calle", Integer.parseInt(calle.getText().toString()));
                    editor.apply();

                    String[] fechaAGuardar = TextUtils.split(etPlannedDate.getText().toString(), "-");
                    final String fechaFinal = fechaAGuardar[2] + "-" + fechaAGuardar[1] + "-" + fechaAGuardar[0];

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<Lotes> loteList = MainActivity.myAppDB.myDao().getLotesByLoteAndFecha(lote.getText().toString(), fechaFinal);
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (loteList.size() > 0) {
                                        lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.warning));
                                        lbl_mensaje.setTextColor(getResources().getColor(R.color.black));
                                        lbl_mensaje.setText(R.string.mnsj_lbl_warning);
                                        Toasty.warning(Objects.requireNonNull(getActivity()), "Lote ya ingresado para este inventario", Toast.LENGTH_SHORT, true).show();
                                    } else {
                                        Lotes lotes = new Lotes();
                                        lotes.setCalle(Integer.parseInt(calle.getText().toString()));
                                        lotes.setDescUbicacionLote(nombreUbicacion);
                                        lotes.setFechaInventario(fechaFinal);
                                        lotes.setIdUbicacionLote(idUbic);
                                        lotes.setUsuarioInventario(usuario.getText().toString());
                                        lotes.setLote(lote.getText().toString());
                                        try {
                                            long idLote = MainActivity.myAppDB.myDao().insertarLote(lotes);
                                            if (idLote > 0) {
                                                lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.success));
                                                lbl_mensaje.setTextColor(getResources().getColor(R.color.whiteText));
                                                lbl_mensaje.setText(R.string.mnsj_lbl_success);
                                                lote.setText("");
                                                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me", MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPref.edit();
                                                editor.putInt("remember_calle", Integer.parseInt(calle.getText().toString()));
                                                editor.apply();
                                                Toasty.success(Objects.requireNonNull(getActivity()), "OK!", Toast.LENGTH_SHORT, true).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                } else {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("remember_fecha", etPlannedDate.getText().toString());
                    editor.putString("remember_usuario", usuario.getText().toString());
                    editor.putInt("remember_lugar", ubicacion.getSelectedItemPosition());
                    editor.putInt("remember_calle", Integer.parseInt(calle.getText().toString()));
                    editor.putInt("remember_estado", 1);
                    editor.apply();
                    lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.danger));
                    lbl_mensaje.setTextColor(getResources().getColor(R.color.whiteText));
                    lbl_mensaje.setText(R.string.mnsj_lbl_danger);
                    Toasty.error(Objects.requireNonNull(getActivity()), "ERROR DE FORMATO EN ETIQUETA", Toast.LENGTH_SHORT, true).show();
                    showAlertForBloqueo();
                }
            }
        }
    }


    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = twoDigits(day) + "-" + twoDigits(month+1) + "-" + year;
                etPlannedDate.setText(selectedDate);
            }
        });
        newFragment.show(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), "datePicker");
    }


    private void showAlertForBloqueo(){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_bloqueo,null);
        final EditText txt = viewInfalted.findViewById(R.id.edit_bloqueo);

        SimpleDateFormat fcA = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date fHa = new Date();
        String fActual = fcA.format(fHa);

        String[] fechaAGuardar = TextUtils.split(fActual, "-");

        final int password = ((Integer.parseInt(fechaAGuardar[0]) * 4) - 3);

        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle("APLICACION BLOQUEADA")
                .setPositiveButton("ingresar", new DialogInterface.OnClickListener(){
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
                            Toasty.error(Objects.requireNonNull(getActivity()), "Debe ingresar la contraseña", Toast.LENGTH_SHORT, true).show();
                        }else{
                            if (Integer.parseInt(txt.getText().toString()) == password){
                                Toasty.success(Objects.requireNonNull(getActivity()), "Aplicacion Desbloqueada", Toast.LENGTH_SHORT, true).show();

                                SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("remember_estado", 0);
                                editor.apply();


                                builder.dismiss();
                            }else{
                                Toasty.error(Objects.requireNonNull(getActivity()), "Incorrecto", Toast.LENGTH_SHORT, true).show();
                            }

                        }
                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
