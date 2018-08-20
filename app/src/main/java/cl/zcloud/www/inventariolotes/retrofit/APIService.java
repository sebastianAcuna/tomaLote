package cl.zcloud.www.inventariolotes.retrofit;

import java.util.ArrayList;

import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.retrofit.respuesta.RespuestaPost;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @POST("entregar_2.php")
    Call<RespuestaPost> setLotes(@Body ArrayList<Lotes> lotes);

}
