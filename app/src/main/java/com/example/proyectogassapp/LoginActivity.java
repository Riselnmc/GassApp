package com.example.proyectogassapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class LoginActivity extends AppCompatActivity {

    EditText correo,pass;
    Button btnIniciar;
    TextView registrar;
    ConexionDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new ConexionDB(this);
        correo = findViewById(R.id.correoE);
        pass = findViewById(R.id.password);
        registrar = findViewById(R.id.register);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveRegistrar = new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(moveRegistrar);
            }
        });

        btnIniciar = findViewById(R.id.btnLogin);
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correoU = correo.getText().toString();
                String contraU = pass.getText().toString();

                if (!correoU.isEmpty() && !contraU.isEmpty()){
                    if (db.ValidarUsuario(correoU,contraU)){
                        Dexter.withActivity(LoginActivity.this)
                                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse response) {
                                        startActivity(new Intent(LoginActivity.this, MapaActivity.class));
                                        finish();
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        if (response.isPermanentlyDenied()){
                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                            builder.setTitle("Permiso denegado")
                                                    .setMessage("Permiso denegado")
                                                    .setNegativeButton("Cancel",null)
                                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Intent intent = new Intent();
                                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                            intent.setData(Uri.fromParts("package", getPackageName(),null));
                                                        }
                                                    })
                                                    .show();
                                        }else {
                                            Toast.makeText(LoginActivity.this, "Permisos denegados", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                        token.continuePermissionRequest();
                                    }
                                })
                                .check();
                    }else{
                        Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }else if (correoU.isEmpty()){
                    correo.setError("El correo no puede estar vacio");
                }else if (contraU.isEmpty()){
                    pass.setError("La contraseña no puede estar vacia");
                }

            }
        });
    }
}
