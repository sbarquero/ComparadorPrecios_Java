package com.sbarquero.comparadorprecios.modelo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Clase Precio. Contiene todos los datos de un precio de un artículo en una tiendas. Se utiliza para mostrar
 * el precio en la vista y para hacer las peticiones a la web API.
 * 
 * Utilizado http://www.jsonschema2pojo.org/ para generar la clase POJO 
 */
public class Precio {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("importe")
    @Expose
    private Double importe;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("articuloId")
    @Expose
    private Integer articuloId;
    @SerializedName("tiendaId")
    @Expose
    private Integer tiendaId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Integer getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(Integer articuloId) {
        this.articuloId = articuloId;
    }

    public Integer getTiendaId() {
        return tiendaId;
    }

    public void setTiendaId(Integer tiendaId) {
        this.tiendaId = tiendaId;
    }

}
