package com.example.comrest;

import static com.example.comrest.AppDatabase.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.comrest.AppDatabase.AppDatabase;
import com.example.comrest.domain.Restaurant;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;

import java.util.List;

public class RestaurantModifyActivity extends AppCompatActivity {

    private MapView restaurantMapModify; //Para pintar la ubicacion nueva en el plano
    private Point point; //Guardamos el point para gestionar la latitud y longuitud
    private PointAnnotationManager pointAnnotationManager; //Para anotar el point así es común para todos
    private long restaurantId; //Para guardarnos el id  a modificar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_modify);

        restaurantMapModify = findViewById(R.id.restaurantMapModify); //le pasamos el mapa creado en el layout

        initializePointManager();// Para que se cree nada más arrancar

        Intent intent = new Intent(getIntent());
        restaurantId = getIntent().getLongExtra("restaurant_id", 0); //guardamos el id que nos traemos de la vista general

        //Instanciamos la BBDD
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        Restaurant restaurant = db.restaurantDao().getById(restaurantId); //Creamos el objeto por su Id
        fillData(restaurant); //rellenamos los datos con el método

//        setCameraPosition(Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude())); //Fijamos la camara del mapa en el puente a modificar
//        addMarker(Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude())); //le pasamos el metodo que crea el marker y ponemos el point y nombre del puente

        addRestaurantToMap(restaurant);

        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(restaurantMapModify);
        gesturesPlugin.addOnMapClickListener(point -> { //Cuando hacemos click en el mapa devolvemos un point
            removeAllMarkers(); //Método creado para borrar los anteriores antes de seleccionar alguna para no tener problemas con los point
            this.point = point; //Ese point lo guardamos para tener la longuitud y latitude
            addMarker(point);
            return true;
        });

    }

    /**
     * Metodo que llama el boton modify_save_brigde_button que tiene definido en onclick modifyButton en el layout activity_brigde_modify.xml
     * @param view
     */
    public void modifyButtonRestaurant(View view){
        EditText etName = findViewById(R.id.modify_text_name);
        CheckBox cbVegano = findViewById(R.id.cb_vegan_modify);
        EditText etComment = findViewById(R.id.modify_text_comment);

        String name = etName.getText().toString(); //Pasamos la cajas de texto a un String
        boolean vegano = cbVegano.isChecked();
        String comment = etComment.getText().toString();

        //If por si acaso el point no está creado, el usuario no ha selecionado nada en el mapa, asi no da error al crear la tarea porque falte latitude y longuitude
        if (point == null) {
            Toast.makeText(this, "Selecciona una ubicación", Toast.LENGTH_LONG).show();
//            Snackbar.make(etName, R.string.select_location_message, BaseTransientBottomBar.LENGTH_LONG); //etName porque el Snackbar hay que asociarlo algún componente del layout
            return;
        }

        Restaurant restaurant = new Restaurant(restaurantId, name, point.latitude(), point.longitude(), comment, vegano); //Creamos un restaurante con los datos, recogemos de point los datos con el clik del usuario sobre el map
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD, la creamos cada vez que necesitemos meter algo en BBDD
                .allowMainThreadQueries().build();

        //Controlamos que la tarea no esta ya creada en su campo primary key, controlando la excepcion
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //le pasamos el contexto donde estamos
            builder.setMessage("Quieres modificar el restaurante")
                    .setTitle("Modificar Restaurante")
                    .setPositiveButton("Si", (dialog, id) -> { //Añadimos los botones
                        final AppDatabase dbD = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD -< PAsamos el contexto para saber donde estamos
                                .allowMainThreadQueries().build();

                        db.restaurantDao().update(restaurant); // Modificamos el objeto dentro de la BBDD

                        //Todo devolverlo al detalle
                        Intent intent = new Intent(this, RestaurantAllActivity.class); //Lo devuelvo al details del puente
                        intent.putExtra("restaurant_id", restaurant.getRestaurant_id());
                        this.startActivity(intent); //lanzamos el intent que nos lleva al layout correspondiente
                    })
                    .setNegativeButton("No", (dialog, id) -> dialog.dismiss()); //Botones del dialogo que salta
            AlertDialog dialog = builder.create();
            dialog.show();//Importante para que se muestre

        } catch (SQLiteConstraintException sce) {
            Snackbar.make(etName, "Error al modificar", BaseTransientBottomBar.LENGTH_LONG);
        }
    }

    /**
     * Metodoque llama el boton back_button que tiene definido en onclick goBackButton en el layout de modificar
     * @param view
     * onBackPressed(); Volver atras
     */
    public void goBackButtonModify(View view) {
        onBackPressed(); //Volver atrás
    }


    private void fillData(Restaurant restaurant) {
        EditText etName = findViewById(R.id.modify_text_name);
        CheckBox cbVegano = findViewById(R.id.cb_vegan_modify);
        EditText etComment = findViewById(R.id.modify_text_comment);

        etName.setText(restaurant.getName());
        etComment.setText(restaurant.getComment());
        cbVegano.isChecked();
    }

    /**
     * Metodo para sacar una lista de restaurantes con un for para crear un point por cada restaurante con la longitude y latitude mas el nombre del restaurante
     * @param
     */
    private void addRestaurantToMap(Restaurant restaurant) {

        Point point = Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude());
        addMarker(point); //le pasamos el metodo que crea el marker y ponemos el point y nombre del restaurante

        setCameraPosition(Point.fromLngLat(restaurant.getLongitude(), restaurant.getLatitude())); //Fijamos la camara del mapa en el ultimo restaurante
    }

    /**
     * Para inicializar el Pointmanager y asi la podemos dejar inicializada nada más arracar en onCreate
     */
    private void initializePointManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(restaurantMapModify);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);
    }

    /**
     * Método para añadir un Marker sobre un mapa
     * @param point le pasamos el point con los datos de latitude y longuitude
     * @param "String" le podemos pasar un titulo para que aparezca en el mapa min 54 webinar 4 de hay se puede sacar
     */
    private void addMarker(Point point) {
        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(BitmapFactory.decodeResource(getResources(), R.mipmap.redmarket_icon_foreground)); //le pasamos el dibujo que queremos que pinte como icono, los podemos crea webinar 4 min 54
        pointAnnotationManager.create(pointAnnotationOptions);
    }

    /**
     * Para borrar el marker anterior y no aparezcan todos en el mapa
     */
    private void removeAllMarkers() {
        pointAnnotationManager.deleteAll(); // Se Podria borra uno en concreto pasandole el point exacto
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
        restaurantMapModify.getMapboxMap().setCamera(cameraPosition);
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