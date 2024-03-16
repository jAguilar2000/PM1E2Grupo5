package com.example.pm1e2grupo5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm1e2grupo5.Modelo.Contactos;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.pm1e2grupo5.Modelo.RestApiMethods;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText txtNombre, txtTelefono, txtLatitud, txtLongitud;

    private RequestQueue requestQueue;

    public static String latitud = "";
    public static String longitud = "";

    private MaterialButton btnfirma, btnguardar, btncontatosS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre =(TextInputEditText) findViewById(R.id.txtNombre);
        txtTelefono =(TextInputEditText) findViewById(R.id.txtTelefono);
        txtLatitud =(TextInputEditText) findViewById(R.id.txtLatitud);
        txtLongitud =(TextInputEditText) findViewById(R.id.txtLongitud);
        btnfirma =(MaterialButton) findViewById(R.id.btnfirma);
        btnguardar =(MaterialButton) findViewById(R.id.btnSalvarG);
        btncontatosS =(MaterialButton) findViewById(R.id.btncontatosS);

        //LLENA LAS COORDENADAS PERO NO CONSULTA SE OTRORGA PERMISO SOLO

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1001);

        }
        else {
            getLocation();
        }


        txtLatitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el LocationManager y la Localizacion
                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Localizacion localizacion = new Localizacion();
                localizacion.setMainActivity(MainActivity.this);

                // Solicitar actualizaciones de ubicación
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                }
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, localizacion);
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, localizacion);
            }
        });

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //   validarDatos();
                SendData();
            }
        });

    }

    private void validarDatos() {
        if(txtNombre.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Debe de escribir un nombre" ,Toast.LENGTH_LONG).show();
        }else if (txtTelefono.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un telefono" ,Toast.LENGTH_LONG).show();
        }else if (txtLatitud.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un latitud" ,Toast.LENGTH_LONG).show();
        }else if (txtLongitud.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Debe de escribir un longitud" ,Toast.LENGTH_LONG).show();
        }
        else {
            SendData();
        }
    }
    private void SendData()
    {
        requestQueue = Volley.newRequestQueue(this);
        Contactos person = new Contactos();

        person.setNombre(txtNombre.getText().toString());
        person.setTelefono(Integer.parseInt(txtTelefono.getText().toString()));
        person.setLatitud(txtLatitud.getText().toString());
        person.setLongitud(txtLongitud.getText().toString());
        //person.setFoto(ConvertImageBase64(currentPhotoPath));

        JSONObject jsonperson = new JSONObject();

        try
        {
            jsonperson.put("nombre",person.getNombre() );
            jsonperson.put("telefono",person.getTelefono() );
            jsonperson.put("latitud",person.getLatitud() );
            jsonperson.put("longitud",person.getLongitud() );
            // jsonperson.put("foto",person.getFoto() );

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        JsonObjectRequest request  = new JsonObjectRequest(Request.Method.POST,
                RestApiMethods.EndpointPostContacto, jsonperson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                try
                {
                    String mensaje = response.getString("messaje");
                    Toast.makeText(getApplicationContext(), mensaje,Toast.LENGTH_LONG).show();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), error.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
        limpiarCampos();
    }
    //METODO PARA RECIBIR LAS COORDENADAS DE LOCALIZACION
    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

        //METODO CAPTURA EL CAMBIO DE LA LOCACION REVISANDO EL GPS
        @Override
        public void onLocationChanged(Location loc) {

            loc.getLatitude();
            loc.getLongitude();

            String Text = "Mi ubicacion actual es: " + "\n Lat = "
                    + loc.getLatitude() + "\n Long = " + loc.getLongitude();


            MainActivity.setLatitud(loc.getLatitude() + "");
            MainActivity.setLongitud(loc.getLongitude() + "");

            txtLatitud.setText(loc.getLatitude() + "");
            txtLongitud.setText(loc.getLongitude() + "");
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
    //METODO PARA OBTENER LA LATITUD
    public static void setLatitud(String latitud) {
        MainActivity.latitud = latitud;
    }


    //METODO PARA OBTENER LA LONGITUD
    public static void setLongitud(String longitud) {
        MainActivity.longitud = longitud;
    }

    //METODO PARA LA setear la LOCAION
    public void setLocation(Location loc) {

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void getLocation() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            //SE VA A LA CONFIGURACION DEL SISTEMA PARA QUE ACTIVE EL GPS UNA VEZ QUE INICIA LA APLICACION
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        txtNombre.requestFocus();
        //  imageView.setImageDrawable(null);
    }




}