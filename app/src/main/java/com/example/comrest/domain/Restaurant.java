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

    public Restaurant() {

    }

    public Restaurant(long restaurant_id, String name, double latitude, double longitude, String comment, boolean vegan) {
        this.restaurant_id = restaurant_id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
        this.vegan = vegan;
    }

    public Restaurant(String name, double latitude, double longitude, String comment, boolean vegan) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.comment = comment;
        this.vegan = vegan;
    }

    public long getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(long restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }
}
