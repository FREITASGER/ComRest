package com.example.comrest.AppDatabase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.comrest.domain.Restaurant;

import java.util.List;

/**
 * Contiene los metodos que queremos publicar, es como el repository en Java
 * Solamente los Query Methods
 * Por cada tabla de BBDD tenemos que tener un Dao
 *
 */
@Dao
public interface RestaurantDao {

    /**
     * Para realizar una Query de todos los Restaurantes
     */
    @Query(value = "SELECT * FROM restaurants")
    List<Restaurant> getAll();

    /**
     * Para realizar una Query de todos los Restaurantes por id
     */
    @Query(value = "SELECT * FROM restaurants WHERE restaurant_id = :restaurant_id")
    Restaurant getById(long restaurant_id);

    /**
     * Para insertar en la BBDD
     */
    @Insert
    void insert (Restaurant restaurant);

    /**
     * Para borrar en la BBDD
     */
    @Delete
    void delete(Restaurant restaurant);

    /**
     * Para modificar en la BBDD
     */
    @Update
    void update(Restaurant restaurant);
}
