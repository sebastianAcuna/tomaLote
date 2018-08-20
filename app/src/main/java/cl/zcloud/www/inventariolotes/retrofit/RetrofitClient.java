package cl.zcloud.www.inventariolotes.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if(retrofit==null){
//            http://190.13.170.26/llasa_android/
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://190.13.170.26/llasa_android/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}

