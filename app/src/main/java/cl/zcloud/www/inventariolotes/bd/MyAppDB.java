package cl.zcloud.www.inventariolotes.bd;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import cl.zcloud.www.inventariolotes.clases.Lotes;
import cl.zcloud.www.inventariolotes.clases.Ubicacion;

@Database(entities = {Ubicacion.class, Lotes.class}, version = 1)
public abstract class MyAppDB  extends RoomDatabase{

    public abstract  MyDao myDao();
}
