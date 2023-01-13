package com.example.comrest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView; //Mapa del layout
    private PointAnnotationManager pointAnnotationManager; //Para recoger un punto y obtener la latitude y longuitude



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.mapView); //Cargamos el mapa y lo asignamos al recurso del layout activity_maps
//        initializePointManager(); // inicializamos el pointmanager
    }
}