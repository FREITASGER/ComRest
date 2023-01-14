package com.example.comrest;

import static com.example.comrest.AppDatabase.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.GesturesPlugin;
import com.mapbox.maps.plugin.gestures.GesturesUtils;

public class RestarurantRegisterActivity extends AppCompatActivity {

    private MapView viewMap; //Porque en el layout de registrar tenemos un mapa
    private Point point; //Guardamos el point para gestionar la latitu y longuitud
    private PointAnnotationManager pointAnnotationManager; //Para anotar el point así es común para todos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restarurant_register);

        viewMap = findViewById(R.id.addRestaurantMap); //le pasamos el mapa creado en el layout activity_register

        GesturesPlugin gesturesPlugin = GesturesUtils.getGestures(viewMap);
        gesturesPlugin.addOnMapClickListener(point -> { //Cuando hacemos click en el mapa devolvemos un point
            removeAllMarkers(); //Método creado para borrar los anteriores antes de seleccionar alguna para no tener problemas con los point
            this.point = point; //Ese point lo guardamos para tener la longuitud y latitude
            addMarker(point);
            return true;
        });

        initializePointManager();// Para que se cree nada más arrancar
    }

    /**
     * Metodo que llama el boton add_restaurant_button que tiene definido en onclick saveButtonRestaurant en el layout activity_register_restaurant.xml
     * @param
     */
    public void saveButton(){
        EditText etName = findViewById(R.id.edit_text_name); //recogemos los datos de las cajas de texto del layout
        EditText etComment = findViewById(R.id.edit_text_comment);
        CheckBox cbVegan = findViewById(R.id.checkbox_vegan);

        String name = etName.getText().toString(); //Pasamos la cajas de texto a un String
        String comment = etComment.getText().toString();
        boolean vegan = cbVegan.isChecked();

        //If por si acaso el point no está creado, el usuario no ha selecionado nada en el mapa, asi no da error al crear la tarea porque falte latitude y longuitude
        if (point == null) {
            Toast.makeText(this, R.string.select_one_postion_in_the_map, Toast.LENGTH_LONG).show();
            return;
        }

        Restaurant restaurant = new Restaurant(name, point.latitude(), point.longitude(), comment, vegan); //Creamos un puente con los datos, recogemos de point los datos con el clik del usuario sobre el map
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD, la creamos cada vez que necesitemos meter algo en BBDD
                .allowMainThreadQueries().build();
        //Controlamos que la tarea no esta ya creada en su campo primary key, controlando la excepcion

        try {
            db.restaurantDao().insert(restaurant); // Insertamos el objeto dentro de la BBDD

            Toast.makeText(this, R.string.register_restaurant, Toast.LENGTH_LONG).show();
            etName.setText(""); //Para vaciar las cajas de texto y prepararlas para registrar otra tarea
            etComment.setText("");

            etName.requestFocus(); //recuperamos el foco
        } catch (SQLiteConstraintException sce) {
            Toast.makeText(this, R.string.error_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Para inicializar el Pointmanager y asi la podemos dejar inicializada nada más arracar en onCreate
     */
    private void initializePointManager() {
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(viewMap);
        AnnotationConfig annotationConfig = new AnnotationConfig();
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, annotationConfig);
    }

    /**
     * Metodoque llama el boton back_button que tiene definido en onclick goBackButton en el layout
     * @param
     * //onBackPressed(); Volver atras
     */
    public void goBack() {
        onBackPressed(); //Volver atrás
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
     * PAra crear el menu (el actionBar)
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_add, menu); //Inflamos el menu
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
            saveButton();
            return true;
        } else if (item.getItemId() == R.id.go_back_topbar) {
            Intent intent    = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }
}