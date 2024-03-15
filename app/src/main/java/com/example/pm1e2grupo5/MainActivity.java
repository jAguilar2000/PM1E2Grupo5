package com.example.pm1e2grupo5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText txtNombre, txtTelefono, txtLatitud, txtLongitud;

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


    }
}