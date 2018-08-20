package cl.zcloud.www.inventariolotes.clases;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "lotes")
public class Lotes {
    @SerializedName("id_lote")
    @ColumnInfo(name = "id_lote")
    @PrimaryKey(autoGenerate = true)
    private int idLotes;

    @SerializedName("fecha_inventario")
    @ColumnInfo(name = "fecha_inventario")
    private String fechaInventario;

    @SerializedName("usuario_inventario")
    @ColumnInfo(name = "usuario_inventario")
    private String usuarioInventario;

    @SerializedName("id_ubicacion_lote")
    @ColumnInfo(name = "id_ubicacion_lote")
    private int idUbicacionLote;

    @SerializedName("desc_ubicacion_lote")
    @ColumnInfo(name = "desc_ubicacion_lote")
    private String descUbicacionLote;

    @SerializedName("calle")
    @ColumnInfo(name = "calle")
    private int calle;

    @SerializedName("lote")
    @ColumnInfo(name = "lote")
    private String lote;

    @SerializedName("estado")
    @ColumnInfo(name = "estado")
    private int estado;

    @SerializedName("fecha_telefono")
    @ColumnInfo(name = "fecha_telefono")
    private  String fechaDispo;

    @SerializedName("imei")
    @ColumnInfo(name = "imei")
    private String imei;

    @SerializedName("fecha_subida")
    @ColumnInfo(name = "fecha_subida")
    private String fecha_subida;

    public Lotes() {
    }

    public int getIdLotes() {
        return idLotes;
    }
    public void setIdLotes(int idLotes) {
        this.idLotes = idLotes;
    }

    public String getFechaInventario() {
        return fechaInventario;
    }
    public void setFechaInventario(String fechaInventario) {
        this.fechaInventario = fechaInventario;
    }

    public String getUsuarioInventario() {
        return usuarioInventario;
    }
    public void setUsuarioInventario(String usuarioInventario) {
        this.usuarioInventario = usuarioInventario;
    }

    public int getIdUbicacionLote() {
        return idUbicacionLote;
    }
    public void setIdUbicacionLote(int idUbicacionLote) {
        this.idUbicacionLote = idUbicacionLote;
    }

    public String getDescUbicacionLote() {
        return descUbicacionLote;
    }
   public void setDescUbicacionLote(String descUbicacionLote) {
        this.descUbicacionLote = descUbicacionLote;
    }

    public int getCalle() {
        return calle;
    }
    public void setCalle(int calle) {
        this.calle = calle;
    }

    public String getLote() {
        return lote;
    }
    public void setLote(String lote) {
        this.lote = lote;
    }

    public int getEstado() {
        return estado;
    }
    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getFechaDispo() {
        return fechaDispo;
    }
    public void setFechaDispo(String fechaDispo) {
        this.fechaDispo = fechaDispo;
    }

    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getFecha_subida() {
        return fecha_subida;
    }
    public void setFecha_subida(String fecha_subida) {
        this.fecha_subida = fecha_subida;
    }
}
