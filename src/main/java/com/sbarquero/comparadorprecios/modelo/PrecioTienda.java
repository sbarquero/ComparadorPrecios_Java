package com.sbarquero.comparadorprecios.modelo;

import com.sbarquero.comparadorprecios.auxiliar.FechaHora;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Objeto PrecioTienda. Se utiliza para mantener los precios que se mostrarán en la tabla y en la vista de 
 * precios.
 * 
 * Utilizado http://www.jsonschema2pojo.org/ para generar la clase POJO 
 */
public class PrecioTienda {

    private final IntegerProperty id;
    private final DoubleProperty importe;
    private final StringProperty fecha;
    private final IntegerProperty articuloId;
    private final StringProperty descripcionArticulo;
    private final IntegerProperty tiendaId;
    private final StringProperty nombreTienda;

    public PrecioTienda() {
        this(0, 0, FechaHora.actual(), 1, "", 0, "");
    }
    
    public PrecioTienda(int id, double importe, String fecha, int articuloId, String descripcionArticulo, int tiendaId, String nombreTienda) {
        this.id = new SimpleIntegerProperty(id);
        this.importe = new SimpleDoubleProperty(importe);
        this.fecha = new SimpleStringProperty(fecha);
        this.articuloId = new SimpleIntegerProperty(articuloId);
        this.descripcionArticulo = new SimpleStringProperty(descripcionArticulo);
        this.tiendaId = new SimpleIntegerProperty(tiendaId);
        this.nombreTienda = new SimpleStringProperty(nombreTienda);
    }
    
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }
    
    public Double getImporte() {
        return importe.get();
    }

    public void setImporte(Double importe) {
        this.importe.set(importe);
    }
    
    public DoubleProperty importeProperty() {
        return importe;
    }

    public String getFecha() {
        return fecha.get();
    }

    public void setFecha(String fecha) {
        this.fecha.set(fecha);
    }

    public StringProperty fechaProperty() {
        return fecha;
    }
    
    public Integer getArticuloId() {
        return articuloId.get();
    }

    public void setArticuloId(Integer articuloId) {
        this.articuloId.set(articuloId);
    }
    
    public IntegerProperty articuloIdProperty() {
        return articuloId;
    }

    public String getDescripcionArticulo() {
        return descripcionArticulo.get();
    }
    
    public void setDescripcionArticulo(String descripcionArticulo) {
        this.descripcionArticulo.set(descripcionArticulo);
    }
    
    public StringProperty descripcionArticulo() {
        return descripcionArticulo;
    }
    
    public Integer getTiendaId() {
        return tiendaId.get();
    }

    public void setTiendaId(Integer tiendaId) {
        this.tiendaId.set(tiendaId);
    }

    public IntegerProperty tiendaIdProperty() {
        return tiendaId;
    }
    
    public String getNombreTienda() {
        return nombreTienda.get();
    }

    public void setNombreTienda(String nombreTienda) {
        this.nombreTienda.set(nombreTienda);
    }
    
    public StringProperty nombreTiendaProperty() {
        return nombreTienda;
    }
}
