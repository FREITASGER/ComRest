package com.example.comrest;

import static com.example.comrest.AppDatabase.Constants.DATABASE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.comrest.Adapter.RestaurantAdapter;
import com.example.comrest.AppDatabase.AppDatabase;
import com.example.comrest.domain.Restaurant;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.atomic.AtomicReference;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private RestaurantAdapter adapter; //Para poder conectar con la BBDD

    FloatingActionButton fabDelete; //Botón para borrar desde la vista detalle
    FloatingActionButton fabModify; //Botón para modificar desde la vista detalle

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        fabDelete = findViewById(R.id.fab_delete);
        fabModify = findViewById(R.id.fab_modify);


        AtomicReference<Intent> intent = new AtomicReference<>(getIntent());
        //Recuperar  por el id
        long restaurantId = getIntent().getLongExtra("restaurant_id", 0);

        //Instanciamos la BBDD
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();
        Restaurant restaurant = db.restaurantDao().getById(restaurantId); //creamos el puente por su id
        fillData(restaurant);

        //Método onClick para borrar
        fabDelete.setOnClickListener((view -> {
            /**
             * Dialogo para pregunta antes de si quiere borrar -> https://developer.android.com/guide/topics/ui/dialogs?hl=es-419
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //le pasamos el contexto donde estamos
            builder.setMessage(R.string.do_you_want_delete)
                    .setTitle(R.string.restaurante_delete)
                    .setPositiveButton(R.string.yes, (dialog, id) -> { //Añadimos los botones
                        final AppDatabase dbD = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD -> Pasamos el contexto para saber donde estamos
                                .allowMainThreadQueries().build();
                        dbD.restaurantDao().delete(restaurant);
                        restaurantList();

                    })
                    .setNegativeButton(R.string.not, (dialog, id) -> dialog.dismiss()); //Botones del dialogo que salta
            AlertDialog dialog = builder.create();
            dialog.show();//Importante para que se muestre
        }));

        //Método onClick para modificar
        fabModify.setOnClickListener((view -> {
            /**
             * Dialogo para pregunta antes de si quiere modificar -> https://developer.android.com/guide/topics/ui/dialogs?hl=es-419
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this); //le pasamos el contexto donde estamos
            builder.setMessage(R.string.are_you_sure_modify)
                    .setTitle(R.string.restaurant_modify)
                    .setPositiveButton(R.string.yes, (dialog, id) -> { //Añadimos los botones
                        final AppDatabase dbD = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD -> Pasamos el contexto para saber donde estamos
                                .allowMainThreadQueries().build();

                        intent.set(new Intent(this, RestaurantModifyActivity.class)); //Lo pasamos al activity
                        intent.get().putExtra("restaurant_id", restaurant.getRestaurant_id()); //Recogemos el id
                        this.startActivity(intent.get()); //lanzamos el intent que nos lleva al layout correspondiente

                    })
                    .setNegativeButton(R.string.not, (dialog, id) -> dialog.dismiss()); //Botones del dialogo que salta
            AlertDialog dialog = builder.create();
            dialog.show();//Importante para que se muestre
        }));
    }

    private void fillData (Restaurant restaurant){
        TextView tvName = findViewById(R.id.tv_restaurant_name);
        TextView tvComment = findViewById(R.id.tv_restaurant_comentarios);
        CheckBox cbVegano = findViewById(R.id.cb_checkbox_details);

        tvName.setText(restaurant.getName());
        tvComment.setText(restaurant.getComment());
        cbVegano.setChecked(restaurant.isVegan());

    }

    /**
     * Para volver al listado de puentes despues de borrar desde la vista de detall
     */
    private void restaurantList () {
        Intent intent = new Intent(this, RestaurantAllActivity.class); //Desde la vista que estamos a la vista que queremos ir
        startActivity(intent); //iniciamos el intent
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