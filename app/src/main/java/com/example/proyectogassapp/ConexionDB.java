package com.example.proyectogassapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;


public class ConexionDB extends SQLiteOpenHelper {

    public static final String NAME_DATABASE = "register.db";
    public static final String NAME_TABLE = "registrarUsuarios";


    public ConexionDB(Context context) {
        super(context, NAME_DATABASE, null, 1);
    }


    public boolean ValidarUsuario (String correo, String password){
        String sql = "Select count(*) from registrarUsuarios where correo ='"+correo+"' and password ='"+password+"'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long valor = statement.simpleQueryForLong();
        statement.close();
        if (valor == 1){
            return true;
        }else {
            return false;
        }
    }

    public boolean validarcorreo(String correo){
        String sql = "Select count(*) from registrarUsuarios where correo='"+correo+"'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long valor = statement.simpleQueryForLong();
        statement.close();
        if (valor==1){
            return true;
        }else
            return false;
    }

    public long AgregarUsuario(String nombres, String apellidos, String correo, String password, String telefono){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues registros = new ContentValues();
        registros.put("nombres",nombres);
        registros.put("apellidos",apellidos);
        registros.put("correo",correo);
        registros.put("password",password);
        registros.put("telefono",telefono);
        long res = db.insert("registrarUsuarios",null,registros);
        db.close();
        return res;
    }


    @Override
    public void onCreate(SQLiteDatabase GassApp) {
        GassApp.execSQL("create table registrarUsuarios (id INTEGER PRIMARY KEY AUTOINCREMENT, nombres TEXT, apellidos TEXT, correo TEXT, password TEXT, telefono TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase GassApp, int oldVersion, int newVersion) {
        GassApp.execSQL("DROP TABLE IF EXISTS "+ NAME_TABLE);
        onCreate(GassApp);
    }
}

