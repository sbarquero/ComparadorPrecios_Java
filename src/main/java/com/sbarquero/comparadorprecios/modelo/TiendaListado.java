package com.sbarquero.comparadorprecios.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Clase TiendaListado. Contiene el id y el nombre de la tiendas. Se utiliza esta forma abreviada para mostrar
 * la listView de Tiendas y la ComboBox de las tiendas que se puede seleccionar.
 * 
 * @author Santiago Barquero <sbarquero AT gmail.com>
 */
public class TiendaListado {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;

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
}
