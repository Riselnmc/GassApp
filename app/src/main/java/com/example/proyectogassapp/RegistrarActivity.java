package com.example.proyectogassapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrarActivity extends AppCompatActivity {

    EditText nombres,apellidos,correo, passw,confirmPass, tele;
    TextView problemaNom,problemaApe,problemaC,problemaP,problemaPC,problemaTele;
    Button btnRegistrarte;
    TextView volver;
    ConexionDB db;
    private static final String TAG = "RegistrarActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        db = new ConexionDB(this);
        nombres = findViewById(R.id.txtNombres);
        nombres.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                problemaNom.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        apellidos = findViewById(R.id.txtApellidos);
        apellidos.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                problemaApe.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        correo = findViewById(R.id.txtCorreo);
        correo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                problemaC.setText("");
                String correoU = correo.getText().toString();
                if (!correoU.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(correoU).matches()){
                    problemaC.setText("");
                }else{
                    problemaC.setText("Correo mal escrito");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passw = findViewById(R.id.txtPassword);
        passw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                problemaP.setText("");
                String passwU = passw.getText().toString();
                String passwConfirm = confirmPass.getText().toString();

                if (passwU.length()<8){
                    problemaP.setText("Contraseña debe ser mayor o igual a 8 caracteres");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirmPass = findViewById(R.id.txtPasswordConfirm);
        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                problemaPC.setText("");
                String passwU = passw.getText().toString();
                String passwConfirm = confirmPass.getText().toString();
                if (passwConfirm.equals(passwU)){
                    problemaPC.setText("");
                    problemaP.setText("");
                }else{
                    problemaP.setText("Las contraseñas no coinciden");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tele = findViewById(R.id.txtTel);
        tele.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                problemaTele.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




        problemaNom = findViewById(R.id.problemaNombre);
        problemaApe = findViewById(R.id.problemaApellido);
        problemaC = findViewById(R.id.problemaCorreo);
        problemaP = findViewById(R.id.problemaPass);
        problemaPC = findViewById(R.id.problemaPassC);
        problemaTele = findViewById(R.id.problemaTele);
        volver = findViewById(R.id.volver);

        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moveLogin = new Intent(RegistrarActivity.this, LoginActivity.class);
                startActivity(moveLogin);
            }
        });

        btnRegistrarte= findViewById(R.id.btnRegistrar);
        btnRegistrarte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombresU = nombres.getText().toString();
                String apellidosU = apellidos.getText().toString();
                String correoU = correo.getText().toString();
                String passwU = passw.getText().toString();
                String passwConfirmU = confirmPass.getText().toString();
                String teleU = tele.getText().toString();

                if (nombresU.isEmpty()){
                    problemaNom.setText("El nombre es obligatorio");
                }else if (apellidosU.isEmpty()){
                    problemaApe.setText("El apellido es obligatorio");
                }else if (correoU.isEmpty()){
                    problemaC.setText("El correo es obligatorio");
                }else if (passwU.isEmpty()){
                    problemaP.setText("La contraseña es obligatoria");
                }else if (passwConfirmU.isEmpty()){
                    problemaPC.setText("Este campo es obligatorio");
                }else if (teleU.isEmpty()){
                    problemaTele.setText("El telefono es obligatorio");
                }else{
                    if (passwU.length()>=8){
                        if (passwU.equals(passwConfirmU)){
                            if (db.validarcorreo(correoU)){

                            }else{
                                long regis = db.AgregarUsuario(nombresU,apellidosU,correoU,passwU,teleU);
                                if (regis>0){
                                    Toast.makeText(RegistrarActivity.this,"Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent moveLogin = new Intent(RegistrarActivity.this, LoginActivity.class);
                                    startActivity(moveLogin);
                                }else{
                                    Toast.makeText(RegistrarActivity.this,"Error al registrar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }

            }
        });
    }
}
