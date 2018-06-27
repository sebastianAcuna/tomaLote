package cl.zcloud.www.inventariolotes.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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

import static android.content.Context.AUDIO_SERVICE;
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


//    bluethooth
    static Handler bluetoothIn;
    final int handlerState = 0;//used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address = null;


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

    @SuppressLint("HandlerLeak")
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

/*        correct = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        alert = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);
        incorrect = new SoundPool(8, AudioManager.STREAM_MUSIC, 0);*/

// Set the hardware buttons to control the music
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
                    InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(lote.getApplicationWindowToken(), 0);
                    /* Write your logic here that will be executed when user taps next button */
                    handled = true;
                }
                return handled;
            }
        });

                bluetoothIn = new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        if (msg.what == handlerState) {	//if message is what we want
                            String readMessage = (String) msg.obj; // msg.arg1 = bytes from connect thread

                            lote.setText(readMessage);

                            if (!TextUtils.isEmpty(lote.getText())){
                                validarCampos();
                            }
//                    recDataString.append(readMessage); //keep appending to string until ~
//                    int endOfLineIndex = recDataString.indexOf("~"); // determine the end-of-line
/*                    if (endOfLineIndex > 0) { // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);  // extract string
                        lote.setText("Datos recibidos = " + dataInPrint);
                        int dataLength = dataInPrint.length();	//get length of data received
                        usuario.setText("Tamaño del String = " + String.valueOf(dataLength));

                        *//*if (recDataString.charAt(0) == '#')	//if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);
                            //sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }*//*
                        recDataString.delete(0, recDataString.length()); 					//clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }*/
                        }
                    }
                };

                btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
                if (checkBTState()){
                    comprobarPareja();
                }


        return view;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onResume() {
        super.onResume();

        if (checkBTState()){
            comprobarPareja();
        }


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {	//if message is what we want
                    String readMessage = (String) msg.obj; // msg.arg1 = bytes from connect thread

                    lote.setText(readMessage);

                    if (!TextUtils.isEmpty(lote.getText())){
                        validarCampos();
                    }
//                    recDataString.append(readMessage); //keep appending to string until ~
//                    int endOfLineIndex = recDataString.indexOf("~"); // determine the end-of-line
/*                    if (endOfLineIndex > 0) { // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);  // extract string
                        lote.setText("Datos recibidos = " + dataInPrint);
                        int dataLength = dataInPrint.length();	//get length of data received
                        usuario.setText("Tamaño del String = " + String.valueOf(dataLength));

                        *//*if (recDataString.charAt(0) == '#')	//if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);
                            //sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                        }*//*
                        recDataString.delete(0, recDataString.length()); 					//clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }*/
                }
            }
        };
        //Get MAC address from DeviceListActivity via intent
//        Intent intent = Objects.requireNonNull(getActivity()).getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
//        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        //Log.i("ramiro", "adress : " + address);


        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
//        mConnectedThread.write("x");
    }


    public void comprobarPareja(){
        // Get a set of currently paired devices and append to 'pairedDevices'
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        // Add previosuly paired devices to the array
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice btd : pairedDevices){
                address = btd.getAddress();
            }
            echarAndarBT();
            Toasty.success(Objects.requireNonNull(getActivity()),"Emparejado ", Toast.LENGTH_SHORT,true).show();
        } else {
            Toasty.error(Objects.requireNonNull(getActivity()),"Sin pareja ", Toast.LENGTH_SHORT,true).show();
        }
    }



    public void echarAndarBT(){
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toasty.error(Objects.requireNonNull(getActivity()).getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG,true).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                Toasty.error(Objects.requireNonNull(getActivity()).getBaseContext(), "La creacción del Socket fallo intento n°2", Toast.LENGTH_LONG,true).show();
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }


    @Override
    public void onPause()
    {
        super.onPause();

        v = null;
        if (checkBTState()){
            try
            {
                //Don't leave Bluetooth sockets open when leaving activity
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
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
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
                return true;
            }
        }
    }


    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException ignored) { Toasty.info(Objects.requireNonNull(getActivity()),"Se perdio la conexión con el dispositivo", Toast.LENGTH_SHORT,false).show();  }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;
            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(Objects.requireNonNull(getActivity()).getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                Objects.requireNonNull(getActivity()).finish();

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
                                        lbl_mensaje.setText(R.string.mnsj_lbl_warning);
//                                        Toasty.warning(Objects.requireNonNull(getActivity()), "Lote ya ingresado para este inventario", Toast.LENGTH_SHORT, true).show();

                                        v = null;
                                    } else {
                                        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                                        Date da = new Date();
                                        final String fechaFinal = df.format(da);


                                        Lotes lotes = new Lotes();
                                        lotes.setFechaDispo(fechaFinal);
                                        lotes.setCalle(Integer.parseInt(calle.getText().toString()));
                                        lotes.setDescUbicacionLote(nombreUbicacion);
                                        lotes.setFechaInventario(fechaFinal);
                                        lotes.setIdUbicacionLote(idUbic);
                                        lotes.setUsuarioInventario(usuario.getText().toString());
                                        lotes.setLote(lote.getText().toString());
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
//                                                Toasty.success(Objects.requireNonNull(getActivity()), "OK!", Toast.LENGTH_SHORT, true).show();
//                                                v.cancel();
//                                                stop_mp();
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
                    String texto = " ERROR! \nERROR DE FORMATO EN ETIQUETA\n"+ lote.getText();
                    lbl_mensaje.setText(texto);
//                    Toasty.error(Objects.requireNonNull(getActivity()), "ERROR DE FORMATO EN ETIQUETA", Toast.LENGTH_SHORT, true).show();
                    showAlertForBloqueo();
//                    v.cancel();
//                    stop_mp();
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
