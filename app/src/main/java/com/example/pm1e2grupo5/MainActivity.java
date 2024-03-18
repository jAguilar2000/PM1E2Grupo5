package com.example.pm1e2grupo5;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pm1e2grupo5.Infrastructure.ImageCompresors.SignatureExtractor;
import com.example.pm1e2grupo5.Modelo.Contactos;
import com.example.pm1e2grupo5.Modelo.RestApiMethods;
import com.example.pm1e2grupo5.Modelo.RestApiMethodsL;
import com.example.pm1e2grupo5.Validations.GeolocationValidation;
import com.example.pm1e2grupo5.Validations.NameValidations;
import com.example.pm1e2grupo5.Validations.PhoneValidations;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE = 101;
    TextInputEditText txtNombre, txtTelefono, txtLatitud, txtLongitud;
    MaterialButton btnfirma, btnguardar, btncontatos;
    SignaturePad firma;
    public static String latitud = "";
    public static String longitud = "";
    private RequestQueue resquestQueue;

    String currentPhotoPath;

    SignatureExtractor signatureExtractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

        firma = (SignaturePad) findViewById(R.id.signature_pad);
        txtNombre =(TextInputEditText) findViewById(R.id.txtNombre);
        txtTelefono =(TextInputEditText) findViewById(R.id.txtTelefono);
        txtLatitud =(TextInputEditText) findViewById(R.id.txtLatitud);
        txtLongitud =(TextInputEditText) findViewById(R.id.txtLongitud);
       // btnfirma =(MaterialButton) findViewById(R.id.btnfirma);
        btnguardar =(MaterialButton) findViewById(R.id.btnCrear);
        btncontatos =(MaterialButton) findViewById(R.id.btnlistado);

        txtNombre.addTextChangedListener(new NameValidations(txtNombre));
        txtTelefono.addTextChangedListener(new PhoneValidations(txtTelefono));
        txtLatitud.addTextChangedListener(new GeolocationValidation(txtLatitud, txtLongitud));
        signatureExtractor = new SignatureExtractor();
        txtLatitud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Localizacion localizacion = new Localizacion();
                localizacion.setMainActivity(MainActivity.this);

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
                SendData();
            }
        });



        btncontatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ContactosActivity.class);
                startActivity(intent);
            }
        });

    }

    private void captureSignature() {
        Bitmap signatureBitmap = firma.getSignatureBitmap();
        convertSignatureToBase64();

        showToast("Imagen capturada!");
    }



    private String convertSignatureToBase64() {


        Bitmap signatureBitmap = firma.getSignatureBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static boolean isValidBase64(String input) {
        return Pattern.matches("^[a-zA-Z0-9+/]*={0,2}$", input);
    }

    //Enviar Data
    private void SendData()
    {

        resquestQueue = Volley.newRequestQueue(this);
        Contactos contactos = new Contactos();


        String nombre = Objects.requireNonNull(txtNombre.getText()).toString();
        String telefono = Objects.requireNonNull(txtTelefono.getText()).toString();
        String latitud = Objects.requireNonNull(txtLatitud.getText()).toString();
        String longitud = Objects.requireNonNull(txtLongitud.getText()).toString();

        Predicate<String> nombreValidator = input -> input.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+");
        Predicate<String> telefonoValidator = input -> input.matches("\\+504\\d{8}");
        Predicate<String> latitudValidator = input -> input.matches("-?\\d+(\\.\\d+)?");
        Predicate<String> longitudValidator = input -> input.matches("-?\\d+(\\.\\d+)?");
        Predicate<String> firmaBase64Validator = input -> isValidBase64(input);

        String firmaBase64 = signatureExtractor.convertSignatureToBase64(firma);

        if (firmaBase64Validator.test(firmaBase64)) {
            Toast.makeText(getApplicationContext(), "Firma Invalida, Debes firmar por huevos.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nombreValidator.test(nombre)) {
            txtNombre.setError("Nombre inválido. Debe contener solo letras y espacios.");
            return;
        }

        if (!telefonoValidator.test(telefono)) {
            txtTelefono.setError("Número de teléfono inválido. Debe estar en formato +504XXXXXXXX.");
            return;
        }

        if (!latitudValidator.test(latitud)) {
            txtLatitud.setError("Latitud inválida. Debe ser un número decimal.");
            return;
        }

        if (!longitudValidator.test(longitud)) {
            txtLongitud.setError("Longitud inválida. Debe ser un número decimal.");
            return;
        }


        contactos.setNombre(nombre);
        contactos.setTelefono(telefono);
        contactos.setLatitud(latitud);
        contactos.setLongitud(longitud);
        contactos.setFirma(firmaBase64);


        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("nombre", contactos.getNombre());
            jsonObject.put("telefono", contactos.getTelefono());
            jsonObject.put("latitud", contactos.getLongitud());
            jsonObject.put("longitud", contactos.getLatitud());
            jsonObject.put("firma",contactos.getFirma());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                                                               RestApiMethodsL.EndpointPostContacto,
                jsonObject, response -> {
                        try {
                            String mensaje = response.getString("message");
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                            System.out.println(mensaje);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                }, error -> {

                String err = error.getMessage().toString();

                Toast.makeText(getApplicationContext(), err,
                                Toast.LENGTH_LONG).show();
            });

            resquestQueue.add(request);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public class Localizacion implements LocationListener {
        MainActivity mainActivity;

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }

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

    public static void setLatitud(String latitud) {
        MainActivity.latitud = latitud;
    }



    public static void setLongitud(String longitud) {
        MainActivity.longitud = longitud;
    }


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
        final boolean gpsEnabled = !mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsEnabled) {

            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }

}