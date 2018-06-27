package cl.zcloud.www.inventariolotes.clases;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "lotes")
public class Lotes {
    @ColumnInfo(name = "id_lote")
    @PrimaryKey(autoGenerate = true)
    private int idLotes;
    @ColumnInfo(name = "fecha_inventario")
    private String fechaInventario;
    @ColumnInfo(name = "usuario_inventario")
    private String usuarioInventario;
    @ColumnInfo(name = "id_ubicacion_lote")
    private int idUbicacionLote;
    @ColumnInfo(name = "desc_ubicacion_lote")
    private String descUbicacionLote;
    @ColumnInfo(name = "calle")
    private int calle;
    @ColumnInfo(name = "lote")
    private String lote;
    @ColumnInfo(name = "estado")
    private int estado;
    @ColumnInfo(name = "fecha_telefono")
    private  String fechaDispo;

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
}
