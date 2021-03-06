package cl.zcloud.www.inventariolotes.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import static android.content.ContentValues.TAG;
import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class tomaExistenciaFragment extends Fragment {

    private EditText etPlannedDate, calle, usuario, lote;
    private Spinner ubicacion;
    private TextView lbl_mensaje;

    private String nombreUbicacion;
    private int idUbic, estadoPantalla;

    ArrayList<String> descUbicacion;
    ArrayList<Integer> idUbicacion;
    ArrayAdapter<String> spinnerAdapter;

    private String myIMEI;


//    bluethooth
    static Handler bluetoothIn;
    final int handlerState = 0;//used to identify handler message

    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();



    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
//    private static String address = null;

private static BluetoothDevice adress;

    private BluetoothAdapter btAdapter;
    public static int REQUEST_BLUETOOTH = 1;
    private ArrayList<BluetoothClass.Device> deviceItemList;

    Vibrator v;
/*    public MediaPlayer correct;
    public MediaPlayer alert;
    public MediaPlayer incorrect;*/
    private SoundPool soundPool;
    private int correct=0, alert = 0, incorrect = 0;
    private boolean loaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"HandlerLeak", "HardwareIds"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.toma_existencia_fragment, container, false);

        etPlannedDate = (EditText) view.findViewById(R.id.datePicker);
        ubicacion = (Spinner) view.findViewById(R.id.spinner_hubicacion);
        calle = (EditText) view.findViewById(R.id.lbl_calle);
        lote = (EditText) view.findViewById(R.id.lbl_lote);
        lbl_mensaje = (TextView) view.findViewById(R.id.lbl_mensaje);
        usuario = (EditText) view.findViewById(R.id.et_usuario);

// Set the hardwae buttons to control the music
        Objects.requireNonNull(getActivity()).setVolumeControlStream(AudioManager.STREAM_MUSIC);
// Load the sound
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

                loaded = true;
            }
        });
        correct = soundPool.load(Objects.requireNonNull(getActivity()), R.raw.correct, 1);
        alert = soundPool.load(Objects.requireNonNull(getActivity()), R.raw.alert, 1);
        incorrect = soundPool.load(Objects.requireNonNull(getActivity()), R.raw.incorrect, 1);

        myIMEI  = Settings.Secure.getString(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        lote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() >= 11){
                    hideKeyboard(Objects.requireNonNull(getActivity()));
                    validarCampos();
                }
            }
        });


        llenarSpinner();

        SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me",MODE_PRIVATE);
        String fecha_remember = sharedPref.getString("remember_fecha","");
        String usuario_remember = sharedPref.getString("remember_usuario","");
        estadoPantalla = sharedPref.getInt("remember_estado",0);
        String lote_remember = sharedPref.getString("remember_lote","");
        int calle_remember = sharedPref.getInt("remember_calle",0);


        if (estadoPantalla > 0){
            showAlertForBloqueo();
            calle.setText(calle_remember + "");
            lote.setText(lote_remember);
        }




        etPlannedDate.setText(fecha_remember);
        usuario.setText(usuario_remember);



        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        lote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validarCampos();
                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService( Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(lote.getApplicationWindowToken(), 0);
                    }
                    handled = true;
                }
                return handled;
            }
        });


        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();


        return view;
    }

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onResume() {
        super.onResume();
            checkBTState();
    }

    @Override
    public void onPause()
    {
        super.onPause();

        v = null;
        if (checkBTState()){
            if (btSocket != null){
                try
                {
                    btSocket.close();
                } catch (IOException e2) {
                    System.out.println("error de socket " + e2.getLocalizedMessage());
                }
            }

        }

    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private boolean checkBTState() {
        if(btAdapter==null) {
            Toasty.info(Objects.requireNonNull(getActivity()).getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG,true).show();
            return false;
        } else {
            if (btAdapter.isEnabled()) {
                return true;
            } else {
                Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBT, REQUEST_BLUETOOTH);
                return true;
            }
        }
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
                Toasty.info(Objects.requireNonNull(getActivity()), "Debe completar todos los campos", Toast.LENGTH_LONG, true).show();
            } else {
                if(Integer.parseInt(calle.getText().toString()) <= 999) {
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
                                            v = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
                                            if (v != null) {
                                                v.vibrate(400);
                                            }
                                            reproducirSound(alert);

                                            lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.warning));
                                            lbl_mensaje.setTextColor(getResources().getColor(R.color.black));
                                            String texto = "REPETIDO ! \nLote ya ingresado para este inventario\n" + lote.getText();
                                            lbl_mensaje.setText(texto);
                                            lote.setText("");
//                                        Toasty.warning(Objects.requireNonNull(getActivity()), "Lote ya ingresado para este inventario", Toast.LENGTH_SHORT, true).show();

                                            v = null;
                                        } else {
                                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                            Date da = new Date();
                                            final String fechaFinalDispo = df.format(da);


                                            Lotes lotes = new Lotes();
                                            lotes.setFechaDispo(fechaFinalDispo);
                                            lotes.setCalle(Integer.parseInt(calle.getText().toString()));
                                            lotes.setDescUbicacionLote(nombreUbicacion);
                                            lotes.setFechaInventario(fechaFinal);
                                            lotes.setIdUbicacionLote(idUbic);
                                            lotes.setEstado(0);
                                            lotes.setUsuarioInventario(usuario.getText().toString());
                                            lotes.setLote(lote.getText().toString());
                                            lotes.setImei(myIMEI);
                                            try {
                                                long idLote = MainActivity.myAppDB.myDao().insertarLote(lotes);
                                                if (idLote > 0) {
                                                    v = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
                                                    if (v != null) {
                                                        v.vibrate(400);
                                                    }
                                                    reproducirSound(correct);

                                                    lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.success));
                                                    lbl_mensaje.setTextColor(getResources().getColor(R.color.whiteText));
                                                    lbl_mensaje.setText(R.string.mnsj_lbl_success);
                                                    lote.setText("");
                                                    SharedPreferences sharedPref = Objects.requireNonNull(getActivity()).getSharedPreferences("remember_me", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putInt("remember_calle", Integer.parseInt(calle.getText().toString()));
                                                    editor.apply();
                                                    v = null;
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
                        v = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(400);
                        }
                        reproducirSound(incorrect);
//                    play_sp(2);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("remember_fecha", etPlannedDate.getText().toString());
                        editor.putString("remember_usuario", usuario.getText().toString());
                        editor.putInt("remember_lugar", ubicacion.getSelectedItemPosition());
                        editor.putInt("remember_calle", Integer.parseInt(calle.getText().toString()));
                        editor.putInt("remember_estado", 1);
                        editor.putString("remember_lote", lote.getText().toString());
                        editor.apply();

                        lbl_mensaje.setBackgroundColor(getResources().getColor(R.color.danger));
                        lbl_mensaje.setTextColor(getResources().getColor(R.color.whiteText));
                        String texto = " ERROR! \nERROR DE FORMATO EN ETIQUETA\n" + lote.getText();
                        lbl_mensaje.setText(texto);
                        lote.setText("");
                        showAlertForBloqueo();
                        v = null;
                    }
                }else{
                    v = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
                    if (v != null) {
                        v.vibrate(400);
                    }
                    reproducirSound(alert);
                    showAlertForCalle();
                    v = null;
                }
            }
        }
    }


    private void reproducirSound(int action){

        AudioManager audioManager = (AudioManager) Objects.requireNonNull(getActivity()).getSystemService(AUDIO_SERVICE);
        if (audioManager != null){
            float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;

            if (loaded) {
                soundPool.play(action, volume, volume, 1, 0, 1f);
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

    private void showAlertForCalle(){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_empty,null);

        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle("Dato incorrecto")
                .setMessage("Calle solo puede contener numeros menores o iguales a 999")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
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

        final int password = (Integer.parseInt(fechaAGuardar[0])+ 1);

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
