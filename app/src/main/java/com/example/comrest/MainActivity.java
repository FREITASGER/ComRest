package com.example.comrest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button addRestaurant;
    Button listRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addRestaurant= findViewById(R.id.add_restaurant);
        addRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(this, RestarurantRegisterActivity.class); //donde nos manda al pinchar sobre el boton mapas en el action bar
            startActivity(intent);
        });

        listRestaurant = findViewById(R.id.list_restaurant);
        listRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(this, RestaurantAllActivity.class); //donde nos manda al pinchar sobre el boton mapas en el action bar
            startActivity(intent);
        });
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