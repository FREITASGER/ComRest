package com.example.comrest.Adapter;

import static com.example.comrest.AppDatabase.Constants.DATABASE_NAME;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.comrest.AppDatabase.AppDatabase;
import com.example.comrest.R;
import com.example.comrest.RestaurantDetailsActivity;
import com.example.comrest.RestaurantModifyActivity;
import com.example.comrest.domain.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantHolder>{

    private Context context; //Pasamos el contexto donde estamos
    private List<Restaurant> restaurantList;

    /**
     * 1) Constructor que creamos para pasarle los datos que queremos que pinte
     * el contexto y la lista de puentes
     * @param dataList Lista de restaurantes que le pasamos
     */
    public RestaurantAdapter(Context context, List<Restaurant> dataList) {
        this.context = context;
        this.restaurantList = dataList;
    }


    /**
     * Metodo con el que Android va a inflar, va a crear cada estructura del layout donde irán los datos de cada puente.
     * Vista detalle de cada puente
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RestaurantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false); // el layout item para cada restaurante
        return new RestaurantHolder(view); //Creamos un holder para cada una de las estructuras que infla el layout
    }

    /**
     * Metodo que estamos obligados para hacer corresponder los valores de la lista y pintarlo en cada elemento de layout
     * es para poder recorrer en el bucle por cada elemento de la lista y poder pintarlo
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RestaurantHolder holder, int position) {
        holder.restaurantName.setText(restaurantList.get(position).getName());
        holder.restaurantCommet.setText(restaurantList.get(position).getComment());
    }

    /**
     * Metodo que estamos obligados a hacer para que devuelva el número de elementos y android pueda hacer sus calculos y pintar xtodo en base a esos calculos
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    /**
     * 5) Holder son las estructuras que contienen los datos y los rellenan luego
     * Creamos todos los componentes que tenemos
     */
    public class RestaurantHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName;
        public TextView restaurantCommet;
//        public CheckBox restaurantVegan;
        public Button detailsRestaurantButton;
        public Button modifyRestaurantButton;
        public Button deleteRestaurantButton;

        public View parentView; //vista padre - como el recyclerView


        /**
         * 5) Consturctor del Holder
         *
         * @param view
         */
        public RestaurantHolder(View view) {
            super(view); //Vista padre
            parentView = view; //Guardamos el componente padre

            restaurantName = view.findViewById(R.id.it_name);
            restaurantCommet = view.findViewById(R.id.it_comment);
            detailsRestaurantButton = view.findViewById(R.id.it_details_button);
            modifyRestaurantButton = view.findViewById(R.id.it_details_modify);
            deleteRestaurantButton = view.findViewById(R.id.it_delete_button);


            //Para decirle que hace el boton cuando pulsamos sobre el
            // Ver detalles
            detailsRestaurantButton.setOnClickListener(v -> detailsRestaurantButton(getAdapterPosition())); //al pulsar lo llevamos al método detailsRestaurantButton
            // Modifica
            modifyRestaurantButton.setOnClickListener(v -> modifyRestaurantButton(getAdapterPosition()));
            // Eliminar
            deleteRestaurantButton.setOnClickListener(v -> deleteRestaurantButton(getAdapterPosition()));
        }

        /**
         * De momento modificamos desde la vista general
         * @param position
         */
        private void modifyRestaurantButton(int position) {

            /**
             * Dialogo para pregunta antes de si quiere modificar -> https://developer.android.com/guide/topics/ui/dialogs?hl=es-419
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(context); //le pasamos el contexto donde estamos
            builder.setMessage(R.string.are_you_sure_you_modify)
                    .setTitle(R.string.restaurant_modify)
                    .setPositiveButton(R.string.yes, (dialog, id) -> { //Añadimos los botones
                        final AppDatabase dbD = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD -> Pasamos el contexto para saber donde estamos
                                .allowMainThreadQueries().build();

                        Restaurant restaurant = restaurantList.get(position);

                        Intent intent = new Intent(context, RestaurantModifyActivity.class);
                        intent.putExtra("restaurant_id", restaurant.getRestaurant_id());
                        context.startActivity(intent);//lanzamos el intent que nos lleva al layout correspondiente

                    })
                    .setNegativeButton(R.string.not, (dialog, id) -> dialog.dismiss()); //Botones del dialogo que salta
            AlertDialog dialog = builder.create();
            dialog.show();//Importante para que se muestre
        }

        /**
         * Método para borrar por posición desde el Recycler asociado al onclik del layout item
         * @param position
         */
        private void deleteRestaurantButton(int position) {
            /**
             * Dialogo para pregunta antes de si quiere borrar -> https://developer.android.com/guide/topics/ui/dialogs?hl=es-419
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(context); //le pasamos el contexto donde estamos
            builder.setMessage(R.string.are_you_sure_you_want_delete)
                    .setTitle(R.string.restaurant_delete)
                    .setPositiveButton(R.string.yes, (dialog, id) -> { //Añadimos los botones
                        final AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME) //Instanciamos la BBDD -< Pasamos el contexto para saber donde estamos
                                .allowMainThreadQueries().build();
                        Restaurant restaurant = restaurantList.get(position); //Recuperamos el objeto po su posicion para pasarselo al delete
                        db.restaurantDao().delete(restaurant); //Borramos de la BBDD

                        restaurantList.remove(position); //Borra solo de la lista que muestra no de la BBDD
                        notifyItemRemoved(position); // Para notificar a Android que hemos borrado algo y refrescar la lista
                    })
                    .setNegativeButton(R.string.not, (dialog, id) -> dialog.dismiss()); //Botones del dialogo que salta
            AlertDialog dialog = builder.create();
            dialog.show();//Importante para que se muestre
        }

        /**
         * Métodos de los botones del layout que se pinta en el recyclerView
         */
        private void detailsRestaurantButton(int position) {
            Restaurant restaurant = restaurantList.get(position); //recuperamos el puente por su posicion

            Intent intent = new Intent(context, RestaurantDetailsActivity.class); //Lo pasamos al activity para pintar el detalle de la tarea
            intent.putExtra("restaurant_id", restaurant.getRestaurant_id()); //Recogemos el id
            context.startActivity(intent); //lanzamos el intent que nos lleva al layout correspondiente
        }
    }
}
