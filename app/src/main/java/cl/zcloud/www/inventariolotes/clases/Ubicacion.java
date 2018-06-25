package cl.zcloud.www.inventariolotes.clases;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "ubicacion")
public class Ubicacion {

    @ColumnInfo(name = "id_ubicacion")
    @PrimaryKey(autoGenerate = true)
    private int idUbicacion;

    @ColumnInfo(name = "descripcion_ubicacion")
    private String DescripcionUbicacion;

    public Ubicacion() {
    }

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getDescripcionUbicacion() {
        return DescripcionUbicacion;
    }

    public void setDescripcionUbicacion(String descripcionUbicacion) {
        DescripcionUbicacion = descripcionUbicacion;
    }
}
