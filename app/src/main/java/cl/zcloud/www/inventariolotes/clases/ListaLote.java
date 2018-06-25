package cl.zcloud.www.inventariolotes.clases;

import java.util.List;

public class ListaLote {
    private List<String> fecha;
    private List<String> ubicaciones;
    private List<String> calles;
    private List<String> lotes;


    public ListaLote(List<String> fecha, List<String> ubicaciones, List<String> calles, List<String> lotes) {
        this.fecha = fecha;
        this.ubicaciones = ubicaciones;
        this.calles = calles;
        this.lotes = lotes;
    }

    public List<String> getFecha() {
        return fecha;
    }

    public void setFecha(List<String> fecha) {
        this.fecha = fecha;
    }

    public List<String> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<String> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }

    public List<String> getCalles() {
        return calles;
    }

    public void setCalles(List<String> calles) {
        this.calles = calles;
    }

    public List<String> getLotes() {
        return lotes;
    }

    public void setLotes(List<String> lotes) {
        this.lotes = lotes;
    }
}
