package cl.zcloud.www.inventariolotes.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cl.zcloud.www.inventariolotes.MainActivity;
import cl.zcloud.www.inventariolotes.R;
import cl.zcloud.www.inventariolotes.VerificarInternet;
import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.retrofit.APIService;
import cl.zcloud.www.inventariolotes.retrofit.RetrofitClient;
import cl.zcloud.www.inventariolotes.retrofit.respuesta.RespuestaPost;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class homeFragment extends Fragment {

    private APIService apiService;
    private TextView lbl_total_registros;
    ProgressDialog dialog;
    String myIMEI;
    @SuppressLint("HardwareIds")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        Button btn_subir_lotes = view.findViewById(R.id.btn_subir_lotes);

        lbl_total_registros = view.findViewById(R.id.lbl_total_registro);

       mostrarCountRegistros();

        myIMEI  = Settings.Secure.getString(Objects.requireNonNull(getActivity()).getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        btn_subir_lotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararSubir();
            }
        });

       hideKeyboard(Objects.requireNonNull(getActivity()));

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

    public void mostrarCountRegistros(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final int lotes = MainActivity.myAppDB.myDao().getCountLotesByEstado(0);
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lbl_total_registros.setText(lotes+"");
                    }
                });
            }
        });
    }

    public void prepararSubir(){

        VerificarInternet tarea =
                new VerificarInternet(getActivity(), new VerificarInternet.EntoncesHacer() {
                    @Override
                    public void cuandoHayInternet() {
                        int lotes = MainActivity.myAppDB.myDao().getCountLotesByEstado(0);
                        if (lotes > 0){
                            showAlertForBloqueo("¡Atento!", "Se subirán " + lotes + " al servidor ",1);
                        }else{
                            showAlertForBloqueo("Todo listo", "Todo actualizado, nada que subir",0);
                        }
                    }
                    @Override
                    public void cuandoNOHayInternet() {
                        showAlertForBloqueo("Sin Conexión", "No tiene acceso a internet o no tiene activado el wifi/datos",0);
                    }
                });
        tarea.execute();

    }

    private class ejecutarSubir extends AsyncTask<Void, Integer, Boolean> {
        boolean exito;
        @Override
        protected void onPreExecute () {
            dialog = new ProgressDialog(Objects.requireNonNull(getActivity()));
            // preparamos el cuadro de dialogo
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Subiendo lotes al servidor");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            ArrayList<Lotes> confl = new ArrayList<>();
            List<Lotes> cnf = MainActivity.myAppDB.myDao().getLotesByEstado(0);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date da = new Date();
            final String fechaFinalDispo = df.format(da);

            if (cnf.size() > 0){
                for (Lotes lts : cnf){
                    Lotes lotes = new Lotes();
                    lotes.setDescUbicacionLote(lts.getDescUbicacionLote());
                    lotes.setCalle(lts.getCalle());
                    lotes.setEstado(lts.getEstado());
                    lotes.setIdUbicacionLote(lts.getIdUbicacionLote());
                    lotes.setFechaInventario(lts.getFechaInventario());
                    lotes.setUsuarioInventario(lts.getUsuarioInventario());
                    lotes.setLote(lts.getLote());
                    lotes.setFechaDispo(lts.getFechaDispo());
                    lotes.setIdLotes(lts.getIdLotes());
                    lotes.setImei(lts.getImei());
                    lotes.setFecha_subida(fechaFinalDispo);
                    confl.add(lotes);
                }
                apiService = RetrofitClient.getClient().create(APIService.class);
                Call<RespuestaPost> call = apiService.setLotes(confl);

                call.enqueue(new Callback<RespuestaPost>() {
                    @Override
                    public void onResponse(Call<RespuestaPost> call, Response<RespuestaPost> response) {
                        RespuestaPost respuesta = response.body();
                        if(respuesta != null){
                            if(respuesta.getEstado() == 1){
                                Toasty.info(Objects.requireNonNull(getActivity()), " se actualizó con exito ", Toast.LENGTH_SHORT, true).show();
                                actualizarLotes();
                            }else{
                                Toast.makeText(getActivity(), "No se ha insertado la id del dispositivo al servidor", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(getActivity(), "No se pudo comunicar con el servidor", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RespuestaPost> call, Throwable t) {
                        System.out.println("NO CONECTO INSERTAB CAB " + t.getMessage());
                        exito = false;
                    }
                });
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer...values){
            super.onProgressUpdate();
        }

        @Override
        protected void onPostExecute(Boolean bo) {
            dialog.dismiss();
            dialog = null;
            if (bo){
                Toasty.success(Objects.requireNonNull(getActivity()), "Se subio todo con exito", Toast.LENGTH_LONG, true ).show();
            }else{
                Toasty.error(Objects.requireNonNull(getActivity()), "No se pudo realizar la accion, vuelva a intentarlo", Toast.LENGTH_LONG, true ).show();
            }
        }
    }


    public void actualizarLotes(){
        try{
            int id = MainActivity.myAppDB.myDao().updateLotes();
            if (id > 0){
                mostrarCountRegistros();
                Toasty.success(Objects.requireNonNull(getActivity()), "Lotes locales actualizados con exito", Toast.LENGTH_SHORT, true).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toasty.error(Objects.requireNonNull(getActivity()), "Algo sucedió", Toast.LENGTH_SHORT,true).show();
        }
    }




    private void showAlertForBloqueo(String title, String message, final int accion){
        View viewInfalted = LayoutInflater.from(Objects.requireNonNull(getActivity())).inflate(R.layout.alert_empty,null);

        final AlertDialog builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(viewInfalted)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
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
                        if (accion == 0){
                            builder.dismiss();
                        }else if (accion == 1){
                            new ejecutarSubir().execute();
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
