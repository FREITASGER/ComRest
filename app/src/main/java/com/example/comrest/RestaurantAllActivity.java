package com.example.comrest;

import static com.example.comrest.AppDatabase.Constants.DATABASE_NAME;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.os.Bundle;

import com.example.comrest.Adapter.RestaurantAdapter;
import com.example.comrest.AppDatabase.AppDatabase;
import com.example.comrest.domain.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAllActivity extends AppCompatActivity {

    private List<Restaurant> restaurantList; //Lista de puentes para obtener los todos los puentes de la BBDD
    private RestaurantAdapter adapter; //Para poder conectar con la BBDD

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_all);

        restaurantList = new ArrayList<>(); //Instanciamos la lista a vacio la seguimos usando como referencia pero la llenamos con la BBDD

        /**
         * Pauta generales para trabajar con recyclerView. Para que se ajuste al layout y nos haga caso
         */
        RecyclerView recyclerView = findViewById(R.id.rc_restaurant_all); //Nos hacemos con el RecyclerView que en el layout activity_main.xml le hemos llamado brigde_list
        recyclerView.setHasFixedSize(true); //para decirle que tiene un tamaño fijo y ocupe xtodo lo que tiene asigando
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //Para decirle al activity que lo va a gestionar un layoutManager
        recyclerView.setLayoutManager(layoutManager); // para que el reciclerView se ciña al layoutManager
        /**
         * FIN Pauta generales para trabajar con recyclerView
         */

        adapter = new RestaurantAdapter(this, restaurantList);  //creamos el adapter y le pasamos la vista actual y la lista de puentes
        recyclerView.setAdapter(adapter); //el adaptador que sabe como poblar de datos la lista en android
    }

    /**
     * COmo pasamos tanto al crear la mainActivity principal como al volver de segundo plano, aqui es donde conectamos con
     * la BBDD para actualizar los datos
     */
    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Llamamos a la BBDD mediante la clase AppDatabase
         * AppDatabase.class la clase que tiene la conexion con la BBDD
         * this -> activity en la que estamos
         * nombre de la BBDD
         * .allowMainThreadQueries().build(); -> Es para que android nos deje hacerlo sin tener de concurrencia
         */
        final AppDatabase db = Room.databaseBuilder(this, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries().build();

        restaurantList.clear(); //Vaciamos la lista por si tuviera algo
        restaurantList.addAll(db.restaurantDao().getAll()); //Añadimos xtodo lo que la BBDD nos devuelve
        adapter.notifyDataSetChanged(); //Para que actualice desde la BBDD
    }

}