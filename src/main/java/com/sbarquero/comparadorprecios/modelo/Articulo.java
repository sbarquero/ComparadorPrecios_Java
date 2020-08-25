package com.sbarquero.comparadorprecios.modelo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sbarquero.comparadorprecios.auxiliar.FechaHora;

/**
 * Clase Articulo. Contendrá todos los datos de un artículo. Se utiliza para mostrar el artículo en la vista
 * y para hacer las peticiones Web API.
 * 
 * Utilizado http://www.jsonschema2pojo.org/ para generar la clase POJO 
 */
public class Articulo {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("ean")
    @Expose
    private String ean;
    @SerializedName("fechaAlta")
    @Expose
    private String fechaAlta;
    @SerializedName("precios")
    @Expose
    private List<Precio> precios = null;
    @SerializedName("imagen")
    @Expose
    private String imagen;

    public Articulo() {
        fechaAlta = FechaHora.actualBD();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public List<Precio> getPrecios() {
        return precios;
    }

    public void setPrecios(List<Precio> precios) {
        this.precios = precios;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

}
