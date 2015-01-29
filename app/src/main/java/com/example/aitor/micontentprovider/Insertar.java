package com.example.aitor.micontentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class Insertar extends Activity {
    private String tipo;
    private EditText etlocalidad,etdireccion,etprecio;
    private Spinner lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertar);

        lista=(Spinner)findViewById(R.id.tipoin);
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(this, R.array.tipos, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista.setAdapter(adapterTipo);

        lista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipo=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void insertar(View v){
        etlocalidad=(EditText)findViewById(R.id.localidadin);
        etdireccion=(EditText)findViewById(R.id.direccionin);
        etprecio=(EditText)findViewById(R.id.precioin);
        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        ContentValues valores = new ContentValues();
        valores.put(Contrato.TablaInmueble.LOCALIDAD, etlocalidad.getText().toString());
        valores.put(Contrato.TablaInmueble.DIRECCION, etdireccion.getText().toString());
        valores.put(Contrato.TablaInmueble.TIPO, tipo);
        valores.put(Contrato.TablaInmueble.PRECIO, etprecio.getText().toString());
        Log.v("muestra", getContentResolver().insert(uri, valores)+"");
        Intent i=new Intent();
        setResult(Activity.RESULT_OK,i);
        finish();
    }




}
