package cl.zcloud.www.inventariolotes.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
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

public class homeFragment extends Fragment {

    private APIService apiService;
    private TextView lbl_total_registros;
    ProgressDialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_fragment, container, false);

        Button btn_subir_lotes = view.findViewById(R.id.btn_subir_lotes);

        lbl_total_registros = view.findViewById(R.id.lbl_total_registro);

       mostrarCountRegistros();


        btn_subir_lotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepararSubir();
            }
        });


        return view;
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
            dialog = new ProgressDialog(getActivity());
            // preparamos el cuadro de dialogo
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("Subiendo lotes al servidor");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            ArrayList<Lotes> confl = new ArrayList<>();
            List<Lotes> cnf = MainActivity.myAppDB.myDao().getLotesByEstado(1);

            if (cnf.size() > 0){
                apiService = RetrofitClient.getClient().create(APIService.class);
                Call<RespuestaPost> call = apiService.setLotes(confl);

                call.enqueue(new Callback<RespuestaPost>() {
                    @Override
                    public void onResponse(Call<RespuestaPost> call, Response<RespuestaPost> response) {
                        RespuestaPost respuesta = response.body();
                        if(respuesta != null){
                            if(respuesta.getEstado() == 1){
                                Toasty.success(Objects.requireNonNull(getActivity()), " :) ", Toast.LENGTH_SHORT, true).show();
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
                }).create();

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
                            new ejecutarSubir();
                        }

                    }
                });
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

}
