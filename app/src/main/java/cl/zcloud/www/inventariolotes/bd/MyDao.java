package cl.zcloud.www.inventariolotes.bd;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;

@Dao
public interface MyDao {

//    UBICACION

    @Insert
    public long insertarUbicacion(Ubicacion ubicacion);

    @Update
    public int updateUbicacion(Ubicacion ubicacion);

    @Delete
    public int deleteUbicacion(Ubicacion ubicacion);

    @Query("SELECT id_ubicacion, descripcion_ubicacion FROM ubicacion")
    public List<Ubicacion> getUbicacion();
    @Query("SELECT id_ubicacion, descripcion_ubicacion FROM ubicacion ORDER BY descripcion_ubicacion DESC")
    public List<Ubicacion> getUbicacionOrderDesc();

//    ============================================================================

//   LOTES

    @Query("SELECT * FROM lotes WHERE id_ubicacion_lote = :idUbicacion ")
    public List<Lotes> getLotesByUbicacion(int idUbicacion);

    @Query("SELECT * FROM lotes WHERE lote = :lote  AND fecha_inventario = :fecha")
    public List<Lotes> getLotesByLoteAndFecha(String lote, String fecha);

    @Query("SELECT fecha_inventario FROM lotes GROUP BY fecha_inventario")
    public List<String> getFechasLotes();

    @Query("SELECT desc_ubicacion_lote FROM lotes WHERE fecha_inventario = :fecha GROUP BY desc_ubicacion_lote ORDER BY desc_ubicacion_lote ASC")
    public List<String> getUbicacionesLotesByFecha(String fecha);

    @Query("SELECT lote FROM lotes WHERE fecha_inventario = :fecha AND desc_ubicacion_lote = :ubicacion ORDER BY fecha_inventario DESC")
    public List<String> getLotesByFechaAndUbicacion(String fecha, String ubicacion);


    @Query("SELECT * FROM lotes WHERE estado = :estado ")
    public List<Lotes> getLotesByEstado(int estado);


    @Query("SELECT count(estado) FROM lotes WHERE estado = :numero ")
    public int getCountLotesByEstado(int numero);


    @Insert
    public long insertarLote(Lotes lotes);


//    ============================================================================

}
