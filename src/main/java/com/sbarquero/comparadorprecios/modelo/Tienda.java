package com.sbarquero.comparadorprecios.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Clase Tienda. Contiene todos los datos de una tiendas. Se utiliza para mostrar los datos en la vista y para
 * hacer las peticiones a la Web API.
 * 
 * Utilizado http://www.jsonschema2pojo.org/ para generar la clase POJO 
 */
public class Tienda {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("latitud")
    @Expose
    private Double latitud;
    @SerializedName("longitud")
    @Expose
    private Double longitud;
    @SerializedName("fechaAlta")
    @Expose
    private String fechaAlta;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

}
