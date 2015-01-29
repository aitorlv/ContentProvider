package com.example.aitor.micontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int INSERTAR=0;
    private static final int MODIFICAR=1;
    private static final int CAMARA=2;
    private static final int ACTIVIDAD2=2;
    private int mover=0;
    private ArrayList <String> imagenesParaMostrar;
    private ListView lv;
    private ImageView img;
    private AdaptadorInmueble adi;
    private Cursor cursor;
    private FragmentoFotos fdos;
    private Inmueble im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cursor = getContentResolver().query(Contrato.TablaInmueble.CONTENT_URI, null,null,null,null);
        getLoaderManager().initLoader(0, null, this);
        lv=(ListView)findViewById(R.id.listaInmueble);
        adi=new AdaptadorInmueble(this,null);
        lv.setAdapter(adi);


        fdos=(FragmentoFotos)getFragmentManager().findFragmentById(R.id.fragment3);
        final boolean horizontal=fdos!=null && fdos.isInLayout();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // getPreferenciasCompartidas();
//              Toast.makeText(MainActivity.this,cursor.getLong(0)+"",Toast.LENGTH_SHORT).show();
                cursor.moveToPosition(position);
                Inmueble t=(Inmueble)Proveedor.getRow(cursor);
                if(horizontal){
                    img=(ImageView)findViewById(R.id.imageView);
                    listarImg(t.getLocalidad(),t.getId());
                }else{
                    Intent i=new Intent(MainActivity.this,Secundaria.class);
                    i.putExtra("inmueble", t);
                    startActivityForResult(i,ACTIVIDAD2);
                }
            }
        });
        registerForContextMenu(lv);
    }

    public void leerPreferenciasCompartidas(){
        SharedPreferences pc;
        pc = getPreferences(Context.MODE_PRIVATE);
        String r = pc.getString("usuario", "usuario");
       tostada(r);
    }

    public void crearPreferenciasCompartidas(String usuario){
        SharedPreferences pc;
        pc = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pc.edit();
        ed.putString("usuario", usuario);
        ed.commit();
    }


    /*------------------------------------METODOS PARA LOS MENUS----------------------------------------------*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_insertar) {
            insertarInmueble();
            return true;
        }else if(id==R.id.action_archivo){
            archivo();
        }else if(id==R.id.action_leer){
            leerPreferenciasCompartidas();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int posicion=info.position;
        cursor.moveToPosition(posicion);
        if (id == R.id.action_modificar) {
            modificarItem(Proveedor.getRow(cursor));
        } else if (id == R.id.action_borrar) {
            borrarItem(cursor.getLong(0));
        }else if(id==R.id.action_fotos){
            im=Proveedor.getRow(cursor);
            tomarfoto();
        }
        return super.onContextItemSelected(item);
    }

    /*----------------------------------------------FINAL DE LOS MENUS-----------------------------------------------------*/

    public void archivo(){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        final View view=inflater.inflate(R.layout.archivo,null);
        alert.setMessage("Inserta tu nombre");
        alert.setView(view);
        //alert.setCancelable(false);
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText ed=(EditText)view.findViewById(R.id.editText);
                if(ed.length()!=0) {
                    crearPreferenciasCompartidas(ed.getText().toString());
                }else {
                    tostada("Nombre inavalido");
                }
            }
        });
        alert.setNegativeButton("Cancelar",null);
        alert.show();
    }
    public void insertarInmueble() {

        Intent intentInsertar = new Intent(this, Insertar.class);
        startActivityForResult(intentInsertar, INSERTAR);
    }

    public void borrarItem(final Long id){
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        final View view=inflater.inflate(R.layout.dialogo_borrar,null);
        alert.setMessage("¿Borrar " +cursor.getString(2) + " de la lista?");
        alert.setView(view);
        //alert.setCancelable(false);
        alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri =Contrato.TablaInmueble.CONTENT_URI;
                String [] parametros={id+""};
                int resultado=getContentResolver().delete(uri,Contrato.TablaInmueble._ID+"  = ?",parametros);
                if(resultado>0) {
                    tostada("Datos borrados");
                }else{
                    tostada("Fallo al borrar");
                }
            }
        });
        alert.setNegativeButton("Cancelar",null);
        alert.show();

    }

    public void modificarItem (Inmueble obj ){
         Bundle b=new Bundle();
        Intent i=new Intent(this,Modificar.class);
        //b.putParcelable("objeto",obj);
        i.putExtra("inmueble",obj);
        tostada(obj+"");
        startActivityForResult(i,MODIFICAR);
    }

    public void tomarfoto(){
        Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMARA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case INSERTAR:
                break;
            case MODIFICAR:
                if(resultCode==RESULT_OK){
                    tostada("Modificacion realizada");
                }else{
                    tostada("Modificacion no realizada");
                }
                break;
            case CAMARA:
                if (resultCode == RESULT_OK) {
                    String fecha = "";
                    Bitmap foto = (Bitmap) data.getExtras().get("data");
                    FileOutputStream salida;
                    Calendar c = Calendar.getInstance();
                    fecha += c.get(Calendar.YEAR) + "_";
                    fecha += c.get(Calendar.MONTH) + "_";
                    fecha += c.get(Calendar.DATE) + "_";
                    fecha += c.get(Calendar.HOUR) + "_";
                    fecha += c.get(Calendar.MINUTE) + "_";
                    fecha += c.get(Calendar.SECOND) + ".";
                    try {
                        salida = new FileOutputStream(getExternalFilesDir(null) + "/" + im.getLocalidad() + "_" + im.getId() + "_" + fecha + "jpg");
                        foto.compress(Bitmap.CompressFormat.JPEG, 90, salida);
                    } catch (FileNotFoundException e) {
                    }
                }
        }



    }

    public void tostada(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();

    }

    /*------------------------------------------------AQUI COMIENZA EL LOADER----------------------------------------*/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = Contrato.TablaInmueble.CONTENT_URI;
        return new CursorLoader(
                this, uri, null, null, null,
                Contrato.TablaInmueble._ID +" collate localized asc");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adi.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adi.swapCursor(null);

    }
    /*------------------------------------------FINAL DEL LOADER---------------------------------------------------*/

    public  void listarImg(String localidad,Long id){
        imagenesParaMostrar=new ArrayList<String>();
        File f = new File(getExternalFilesDir(null),"");
        File file[] = f.listFiles();
        int si=0;
        String nombreArchivo=localidad+"_"+id;
        Log.d("Files", "Size: " + file.length);
        for (int i=0; i < file.length; i++)
        {
            if(nombreArchivo.compareTo(file[i].getName().substring(0,nombreArchivo.length()))==0) {
                imagenesParaMostrar.add(file[i].getName());
            }
        }
        if(imagenesParaMostrar.size()!=0) {
            mostrarEnImageView(imagenesParaMostrar);
        }else{
            Toast.makeText(this,"No hay imagenes",Toast.LENGTH_LONG).show();
        }
    }

    public void atras(View v){
        mover--;
        mostrarEnImageView(imagenesParaMostrar);
    }
    public void delante(View v){
        Log.v("hola","hola");
        mover++;
        mostrarEnImageView(imagenesParaMostrar);
    }

    public void mostrarEnImageView(ArrayList<String>imagenes) {

        if (mover > imagenes.size() - 1 || mover < 0) {
            mover = 0;
            img.setImageBitmap(BitmapFactory.decodeFile(getExternalFilesDir(null) + "/" + imagenes.get(mover)));
        } else {
            img.setImageBitmap(BitmapFactory.decodeFile(getExternalFilesDir(null) + "/" + imagenes.get(mover)));
        }
    }
}
