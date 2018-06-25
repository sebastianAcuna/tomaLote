package cl.zcloud.www.inventariolotes.retrofit.respuesta;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespuestaPost {

    @SerializedName("estado")
    @Expose
    private int estado;

    @SerializedName("respuesta")
    @Expose
    private String Respuesta;


    public int getEstado() {
        return estado;
    }
    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getRespuesta() {
        return Respuesta;
    }
    public void setRespuesta(String respuesta) {
        Respuesta = respuesta;
    }
}
