package com.example.aitor.micontentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;


public class Modificar extends Activity {
    private EditText etmod1, etmod2, etmod3, etmod4;
    private String tipo;
    private TextView tvimagen;
    private Spinner lista;
    private String comprobarLocalidad;
    private Cursor cu;
    private Long id;
    public Inmueble im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);
        im = (Inmueble)getIntent().getExtras().get("inmueble");
        mostrarValores(im);
        String[] valorestipo = new String[]{"Casa", "Piso", "Local", "Cochera"};
        lista = (Spinner) findViewById(R.id.tipomod);
        ArrayAdapter<CharSequence> adapterTipo = ArrayAdapter.createFromResource(this, R.array.tipos, android.R.layout.simple_spinner_item);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lista.setAdapter(adapterTipo);
        for(int i = 0;i<valorestipo.length;i++){

            if (valorestipo[i].compareTo(im.getTipo()) == 0) {
                lista.setSelection(i);
            }
        }

        lista.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            @Override
            public void onItemSelected (AdapterView < ? > adapterView, View view,int i, long l){
                tipo = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected (AdapterView < ? > adapterView){

            }


    });
 }

    public void mostrarValores(Inmueble obj) {
        etmod1 = (EditText) findViewById(R.id.localidadmod);
        etmod2 = (EditText) findViewById(R.id.direccionmod);
        etmod4 = (EditText) findViewById(R.id.preciomod);
        etmod1.setText(obj.getLocalidad().toString());
        etmod2.setText(obj.getDireccion().toString());
        etmod4.setText(obj.getPrecio().toString());

    }

    public void modificar(View v) {
        String localidad,direccion,precio;
        Inmueble obj;
        localidad = etmod1.getText().toString();
        direccion = etmod2.getText().toString();
        precio = etmod4.getText().toString();

            try {
                Uri uri = Contrato.TablaInmueble.CONTENT_URI;
                ContentValues valores = new ContentValues();
                valores.put(Contrato.TablaInmueble.LOCALIDAD, localidad);
                valores.put(Contrato.TablaInmueble.DIRECCION, direccion);
                valores.put(Contrato.TablaInmueble.TIPO, tipo);
                valores.put(Contrato.TablaInmueble.PRECIO, precio);
                int modificados=getContentResolver().update(uri, valores,Contrato.TablaInmueble._ID + " = ?",new String[]{im.getId()+""});
                if(modificados!=0) {
                    Intent i = new Intent();
                    setResult(Activity.RESULT_OK, i);
                    finish();
                }else{
                    Intent i = new Intent();
                    setResult(Activity.RESULT_CANCELED, i);
                    finish();
                }

            }catch (InternalError e){
                Intent i=new Intent();
                setResult(Activity.RESULT_CANCELED,i);
            }
            finish();

        }



    public void cancelar(View v){
        Intent i=new Intent();
        setResult(Activity.RESULT_CANCELED,i);
        finish();
    }

}
