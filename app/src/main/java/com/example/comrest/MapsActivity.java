package com.example.comrest;

import static com.example.comrest.AppDatabase.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.comrest.AppDatabase.AppDatabase;
import com.example.comrest.domain.Restaurant;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity {

    private MapView mapView; //Mapa del layout
    private PointAnnotationManager pointAnnotationManager; //Para recoger un punto y obtener la latitude y longuitude

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapView = findViewById(R.id.mapView); //Cargamos el mapa y lo asignamos al recurso del layout activity_maps
        initializePointManager(); // inicializamos el pointmanager

        /**
         * No traemos todas los restaurantes que hay para pintar todas en un mapa
         */
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        List<Restaurant> restaurantList = db.restaurantDao().getAll(); //Lista de la tareas
        addRestaurantToMap(restaurantList); // se lo pasamos al metodo
    }

    /**
     * Metodo para sacar una lista de restaurantes con un for para crear un point por cada restaurante con la longitude y latitude mas el nombre del restaurante
     * @param
     */
    private void addRestaurantToMap(List<Restaurant> restaurantList) {
        for (Restaurant restaurant : restaurantList) {
            Point point = Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude());
            addMarker(point, restaurant.getName()); //le pasamos el metodo que crea el marker y ponemos el point y nombre del restaurante
        }

        Restaurant lastRestaurant = restaurantList.get(restaurantList.size() - 1); // recogemos el ultimo restaurante
        setCameraPosition(Point.fromLngLat(lastRestaurant.getLongitude(), lastRestaurant.getLatitude())); //Fijamos la camara del mapa en el ultimo restaurante
    }

    /**
     * Inicializamos el pointmanager
     */
    private void initializePointManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);
    }

    /**
     * Para poder crear un marker y que lo pinte por cada restaurante
     * @param point Pasamos el point
     * @param title el nombre del restaurante
     */
    private void addMarker(Point point, String title) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withTextField(title) //asi aparece el nombre en el mapa
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.mipmap.redmarket_icon_foreground));
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    /**
     * Fija la camara del mapa donde nosotros queramos, asi el mapa arranca desde ese punto
     * @param point
     */
    private void setCameraPosition(Point point) {
        CameraOptions cameraPosition = new CameraOptions.Builder()
                .center(point)
                .pitch(0.0)
                .zoom(13.5)
                .bearing(-17.6)
                .build();
        mapView.getMapboxMap().setCamera(cameraPosition);
    }

    /**
     * PAra crear el menu (el actionBar)
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu); //Inflamos el menu
        return true;
    }

    /**
     * Para cuando elegimos una opcion del menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_topbar) { //Para cuando pulsan en la boton del mapa en el actionbar
            Intent intent = new Intent(this, MapsActivity.class); //donde nos manda al pinchar sobre el boton mapas en el action bar
            startActivity(intent);
            return true;
        }
        return false;
    }

}