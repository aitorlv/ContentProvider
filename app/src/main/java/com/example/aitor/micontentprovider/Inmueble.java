package com.example.aitor.micontentprovider;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by aitor on 02/12/2014.
 */
public class Inmueble implements Comparable<Inmueble>,Parcelable{

    private Long id;
    private String localidad,direccion,tipo,precio;


    public Inmueble(){

    }

    public Inmueble(Long id, String localidad, String direccion, String tipo, String precio){
        this.id=id;
        this.localidad=localidad;
        this.direccion=direccion;
        this.tipo=tipo;
        this.precio=precio;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    @Override
    public int compareTo(Inmueble inmueble) {
        int ct1 = this.direccion.compareTo(inmueble.direccion);
        if(ct1!=0){
            Collator coll = Collator.getInstance(Locale.getDefault());
            ct1 = coll.compare(this.direccion, inmueble.direccion);
        }
        return ct1;
    }

    public static final Parcelable.Creator<Inmueble> CREATOR =new Parcelable.Creator<Inmueble>(){
        @Override
        public Inmueble createFromParcel(Parcel p) {

            Long id=p.readLong();
            String localidad=p.readString();
            String direccion=p.readString();
            String tipo=p.readString();
            String precio=p.readString();

            return new Inmueble(id,localidad,direccion,tipo,precio);
        }

        @Override
        public Inmueble[] newArray(int size) {
            return new Inmueble[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.localidad);
        parcel.writeString(this.direccion);
        parcel.writeString(this.tipo);
        parcel.writeString(this.precio);

    }

    @Override
    public String toString() {
        return "Inmueble{" +
                "id='" + id + '\'' +
                ", localidad='" + localidad + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tipo='" + tipo + '\'' +
                ", precio='" + precio + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Inmueble)) return false;

        Inmueble inmueble = (Inmueble) o;

        if (direccion != null ? !direccion.equals(inmueble.direccion) : inmueble.direccion != null)
            return false;
        if (id != null ? !id.equals(inmueble.id) : inmueble.id != null) return false;
        if (localidad != null ? !localidad.equals(inmueble.localidad) : inmueble.localidad != null)
            return false;
        if (precio != null ? !precio.equals(inmueble.precio) : inmueble.precio != null)
            return false;
        if (tipo != null ? !tipo.equals(inmueble.tipo) : inmueble.tipo != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (localidad != null ? localidad.hashCode() : 0);
        result = 31 * result + (direccion != null ? direccion.hashCode() : 0);
        result = 31 * result + (tipo != null ? tipo.hashCode() : 0);
        result = 31 * result + (precio != null ? precio.hashCode() : 0);
        return result;
    }
}
