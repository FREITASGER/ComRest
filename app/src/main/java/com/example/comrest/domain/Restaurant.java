package com.example.comrest.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.PrimitiveIterator;

@Entity(tableName = "restaurants")
public class Restaurant {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long restaurant_id;

    @ColumnInfo
    @NonNull
    private String name;
    @ColumnInfo
    private double latitude; //para poder ubicar en el mapa
    @ColumnInfo
    private double longitude; //para poder ubicar en el mapa
    @ColumnInfo
//    @NonNull
    private String comment;
    @ColumnInfo
    private boolean vegan;

}
