package com.example.aitor.micontentprovider;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Ayudante extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "inmueble.sqlite";
    public static final int DATABASE_VERSION = 1;

    public Ayudante(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;


        sql = "create table " + Contrato.TablaInmueble.TABLA + "( " +
                Contrato.TablaInmueble._ID+ " integer primary key autoincrement, " +
                Contrato.TablaInmueble.LOCALIDAD + " text, " +
                Contrato.TablaInmueble.DIRECCION + " text, " +
                Contrato.TablaInmueble.TIPO+ " text, " +
                Contrato.TablaInmueble.PRECIO + " text, " +
                Contrato.TablaInmueble.SUBIDO+" integer default 0 "+
                ")";
        Log.v("sql", sql);
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}