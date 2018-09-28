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

    @Query("UPDATE ubicacion SET descripcion_ubicacion = :newUbicacion WHERE descripcion_ubicacion = :oldUbicacion")
    public int updateUbicacionList(String newUbicacion, String oldUbicacion);


    @Query("DELETE FROM ubicacion")
    public int deleteUbicaciones();

//    ============================================================================

//   LOTES

    @Query("SELECT * FROM lotes WHERE id_ubicacion_lote = :idUbicacion ")
    public List<Lotes> getLotesByUbicacion(int idUbicacion);

    @Query("SELECT * FROM lotes WHERE lote = :lote  AND fecha_inventario = :fecha")
    public List<Lotes> getLotesByLoteAndFecha(String lote, String fecha);

    @Query("SELECT * FROM lotes GROUP BY fecha_inventario")
    public List<Lotes> getFechasLotes();

    @Query("SELECT * FROM lotes WHERE fecha_inventario = :fecha GROUP BY desc_ubicacion_lote ORDER BY desc_ubicacion_lote ASC")
    public List<Lotes> getUbicacionesLotesByFecha(String fecha);

    @Query("SELECT id_lote, id_ubicacion_lote, calle, COUNT(lote) as estado , fecha_inventario, desc_ubicacion_lote FROM lotes WHERE fecha_inventario = :fecha AND desc_ubicacion_lote = :ubicacion GROUP BY calle ORDER BY fecha_inventario DESC")
    public List<Lotes> getLotesByFechaAndUbicacion(String fecha, String ubicacion);

    @Query("SELECT id_lote, id_ubicacion_lote, calle, COUNT(lote) as estado , fecha_inventario, desc_ubicacion_lote FROM lotes WHERE fecha_inventario = :fecha GROUP BY calle ORDER BY fecha_inventario DESC")
    public List<Lotes> getLotesByFecha(String fecha);

    @Query("SELECT * FROM lotes WHERE fecha_inventario = :fecha AND desc_ubicacion_lote = :ubicacion AND calle = :calle ORDER BY fecha_inventario DESC")
    public List<Lotes> getLotesByFechaUbicacionAndCalle(String fecha, String ubicacion, int calle);

    @Query("SELECT * FROM lotes WHERE fecha_inventario = :fecha AND calle = :calle ORDER BY fecha_inventario DESC")
    public List<Lotes> getLotesByFechaAndCalle(String fecha, int calle);

    @Query("SELECT * FROM lotes WHERE estado = :estado ")
    public List<Lotes> getLotesByEstado(int estado);

    @Query("SELECT count(estado) FROM lotes WHERE estado = :numero ")
    public int getCountLotesByEstado(int numero);

    @Query("SELECT * FROM lotes")
    public List<Lotes> getLotes();

    @Query("UPDATE lotes SET estado = 1 WHERE estado = 0 ")
    public int updateLotes();

    @Query("UPDATE lotes SET desc_ubicacion_lote = :newUbicacion WHERE desc_ubicacion_lote = :oldUbicacion  AND fecha_inventario = :fecha ")
    public int updateLotesByUbicacion(String newUbicacion, String oldUbicacion, String fecha);

    @Query("UPDATE lotes SET calle = :newCalle WHERE calle = :oldCalle AND  desc_ubicacion_lote = :ubicacion AND fecha_inventario = :fecha ")
    public int updateLotesByCalle(int newCalle, int oldCalle, String ubicacion, String fecha);

    @Query("DELETE FROM lotes")
    public int deleteLotes();

    @Query("DELETE FROM lotes WHERE estado = :sync")
    public int deleteLotesBySync(int sync);

    @Insert
    public long insertarLote(Lotes lotes);


//    ============================================================================

}
