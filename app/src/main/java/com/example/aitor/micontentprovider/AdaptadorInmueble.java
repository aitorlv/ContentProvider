package com.example.aitor.micontentprovider;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by aitor on 27/01/2015.
 */
public class AdaptadorInmueble extends CursorAdapter{
    private Context contexto;
    private int recurso;
    private LayoutInflater i;
    private TextView tv1,tv2,tv3;
    ImageView img;

    public AdaptadorInmueble(Context context, Cursor c) {
        super(context, c,true);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater i = LayoutInflater.from(parent.getContext());
        View v = i.inflate(R.layout.detalle_inmueble, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        img=(ImageView)view.findViewById(R.id.imagen);
        tv1 = (TextView)view.findViewById(R.id.localidad);
        tv3 = (TextView)view.findViewById(R.id.precio);
        tv2 = (TextView)view.findViewById(R.id.direccion);
        Inmueble obj = Proveedor.getRow(cursor);
        tv1.setText(obj.getLocalidad());
        tv2.setText(obj.getDireccion());
        tv3.setText(obj.getPrecio());
        if(obj.getTipo().compareTo("Casa")==0) {
            img.setImageResource(R.drawable.casa);
        }else if(obj.getTipo().compareTo("Piso")==0) {
            img.setImageResource(R.drawable.piso);
        }else if(obj.getTipo().compareTo("Local")==0) {
            img.setImageResource(R.drawable.local);
        }else if(obj.getTipo().compareTo("Cochera")==0) {
            img.setImageResource(R.drawable.cochera);
        }
    }
}
