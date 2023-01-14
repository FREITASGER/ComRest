package com.example.comrest.AppDatabase;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.comrest.domain.Restaurant;

/**
 * Esta clase nos va a permitir instanciar la BBDD
 * @Database(entities = {Restaurant.class }, version = 1):
 * Para mas clases añadir más @Database
 * más la version de la BBDD para poder actualizar mediante este número la apliacion y si existen nuevas tablas no se pierdan los datos mediante una serie de metodos
 * Necesitamos poner en Grandle Scripts/build.grandle la librería y la anotación
 *     implentation "androidx.room:room-runtime:2.4.3"
 *     annotationProcessor "androidx.room:room-runtime:2.4.3"
 * Por cada Tabla que tengamos necesitamos un Dao
 */
@Database(entities = {Restaurant.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RestaurantDao restaurantDao();


}
