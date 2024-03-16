package com.example.pm1e2grupo5;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pm1e2grupo5.Modelo.Contactos;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactosActivity extends AppCompatActivity {

    private ArrayList<Contactos> contactosList;
    private ContactosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        ListView listView = findViewById(R.id.listView);
        contactosList = new ArrayList<>();
        adapter = new ContactosAdapter(this, contactosList);
        listView.setAdapter(adapter);

        obtenerContactosDesdeAPI();
    }

    private void obtenerContactosDesdeAPI() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.20/PM1E2Grupo5Api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ArrayList<Contactos>> call = apiInterface.getContactos();

        call.enqueue(new Callback<ArrayList<Contactos>>() {
            @Override
            public void onResponse(Call<ArrayList<Contactos>> call, Response<ArrayList<Contactos>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    contactosList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ContactosActivity.this, "Error al obtener los contactos desde la API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Contactos>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ContactosActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
