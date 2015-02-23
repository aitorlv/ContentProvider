package com.example.aitor.micontentprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int INSERTAR=0;
    private static final int MODIFICAR=1;
    private static final int CAMARA=2;
    private static final int ACTIVIDAD2=2;
    private int mover=0;
    private ArrayList <String> imagenesParaMostrar=new ArrayList<String>();
    private ListView lv;
    private ImageView img;
    private AdaptadorInmueble adi;
    private Cursor cursor,cursorParaSubir;
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

    public String leerPreferenciasCompartidas(){
        SharedPreferences pc;
        pc = getPreferences(Context.MODE_PRIVATE);
        String r = pc.getString("usuario", "usuario");
       return r;
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
        }else if(id==R.id.action_subir){
            //tostada("entro");
            subirDatos();
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
                    cursor = getContentResolver().query(Contrato.TablaInmueble.CONTENT_URI, null,null,null,null);
                }else{
                    tostada("Fallo al borrar");
                }
            }
        });
        alert.setNegativeButton("Cancelar",null);
        alert.show();

    }

    public void subirDatos(){
        //tostada("hola");
        Uri uri =Contrato.TablaInmueble.CONTENT_URI_SUBIR;
        String [] proyeccion={Contrato.TablaInmueble.SUBIDO+"  = ?"};
        String[]argumentos={0+""};
        cursorParaSubir = getContentResolver().query(uri,null,Contrato.TablaInmueble.SUBIDO+"  = ?",argumentos,null);
        cursorParaSubir.moveToFirst();
        if(cursorParaSubir.getCount()>0){
            new SubirDatos().execute();
        }else{
            tostada("No hay datos para subir");
        }
        //tostada(cursorParaSubir.getString(0)+" "+cursorParaSubir.getString(1)+" "+cursorParaSubir.getString(2)+" "+cursorParaSubir.getString(3)+" "+cursorParaSubir.getString(4)+" "+cursorParaSubir.getString(5));

    }

    /*--------------------------------------------------------HEBRA PARA SUBIR DATOS------------------------------------------------------*/

    class SubirDatos extends AsyncTask<Void,Integer,String> {
        ProgressDialog pb;
        boolean siguiente=true;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb=ProgressDialog.show(MainActivity.this, "Subida",
                    "Subiendo datos", true);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        @Override
        protected String doInBackground(Void... params) {
            String r = "", url = "", tr = "", idinsert = "";
            cursorParaSubir.moveToFirst();
            while (siguiente) {
                url = "http://192.168.1.35:8080/Inmobiliaria/control?target=inmueble&"
                        + "op=insert&action=opmovil&idAndroid=" + cursorParaSubir.getString(0) + "&localidad=" + cursorParaSubir.getString(1).replace(" ", "%20")
                        + "&direccion=" + cursorParaSubir.getString(2).replace(" ", "%20") + "&tipo=" + cursorParaSubir.getString(3) + "&precio=" + cursorParaSubir.getString(4).replace(" ", "%20")
                        + "&usuario=" + leerPreferenciasCompartidas().replace(" ", "%20");
                r = leerpagina(url);
                if (!r.isEmpty()) {
                    Uri uri = Contrato.TablaInmueble.CONTENT_URI;
                    ContentValues valores = new ContentValues();
                    valores.put(Contrato.TablaInmueble.SUBIDO, 1);
                    getContentResolver().update(uri, valores, Contrato.TablaInmueble._ID + " = ?", new String[]{cursorParaSubir.getString(0) + ""});
                    tr = r.substring(r.lastIndexOf("<tr>"), r.lastIndexOf("</tr>"));
                    String td = tr.substring(tr.lastIndexOf("<td"), tr.lastIndexOf("</td>"));
                    String[] id = td.split(">");
                    listarImgParaSubir(cursorParaSubir.getString(1), Long.parseLong(cursorParaSubir.getString(0)));
                    String urlImagen = "http://192.168.1.35:8080/Inmobiliaria/control?target=imagen&op=insert&action=opmovil";
                    for (int i = 0; i < imagenesParaMostrar.size(); i++) {
                        postFile(urlImagen, "imagen", imagenesParaMostrar.get(i), id[1]);
                    }
                    siguiente=cursorParaSubir.moveToNext();
                }
                //}
                Log.v("ruta", imagenesParaMostrar.get(0) + "");

            }
            return "Datos subidos";
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            Toast.makeText(MainActivity.this,strings,Toast.LENGTH_SHORT).show();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            pb.dismiss();
        }

        public String leerpagina(String data){

            URL url;
            InputStream is = null;
            BufferedReader br;
            String line,out="";
            try{
                url = new URL(data);
                is = url.openStream();  // throws an IOException
                br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    out+=line+"\n";
                }
                br.close();
                is.close();
                return out;
            }catch(IOException e){
                Log.v("error",e.toString());
            }
            return "no se ha podido leer";
        }
        public String postFile(String urlPeticion, String nombreParametro, String archivoAsubir,String id) {
            //urlPeticion es la URL de envío
            //nombreParametro es el name del input del html
            //nombreArchivo es el uri.getPath()
            Log.v("ruta archivo",archivoAsubir);
            String resultado="";
            int status=0;
            try {
                URL url = new URL(urlPeticion);
                HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                conexion.setDoOutput(true);
                conexion.setRequestMethod("POST");
                FileBody fileBody = new FileBody(new File(archivoAsubir));
                Log.v("filebody",fileBody+"");
                MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.STRICT);
                multipartEntity.addPart(nombreParametro, fileBody);
                multipartEntity.addPart("id", new StringBody(id));
                //multipartEntity.addPart("nombre", new StringBody("valor"));
                conexion.setRequestProperty("Content-Type", multipartEntity.getContentType().getValue());
                OutputStream out = conexion.getOutputStream();
                try {
                    multipartEntity.writeTo(out);
                } catch (Exception e){
                    Log.v("primero",e.toString());
                    return e.toString();
                }finally {
                    out.close();
                }
                BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    resultado+=decodedString+"\n";
                }
                in.close();
                status = conexion.getResponseCode();
            } catch (MalformedURLException ex) {
                Log.v("segundo",ex.toString());
                return null;
            } catch (IOException ex) {
                Log.v("tercero",ex.toString());
                return null;
            }
            return resultado+"\n"+status;
        }


    }


    /*--------------------------------------------------------FIN HEBRA PARA SUBIR DATOS--------------------------------------------------*/
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
                cursor = getContentResolver().query(Contrato.TablaInmueble.CONTENT_URI, null,null,null,null);
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
    public void listarImgParaSubir(String localidad,Long id){
        imagenesParaMostrar.clear();
        File f = new File(getExternalFilesDir(null),"");
        File file[] = f.listFiles();
        int si=0;
        String nombreArchivo=localidad+"_"+id;
        Log.d("Files", "Size: " + file.length);
        for (int i=0; i < file.length; i++)
        {
            if(nombreArchivo.compareTo(file[i].getName().substring(0,nombreArchivo.length()))==0) {
                imagenesParaMostrar.add(file[i].getAbsolutePath());
            }
        }
    }

    public  void listarImg(String localidad,Long id){
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
