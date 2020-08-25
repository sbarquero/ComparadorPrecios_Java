package com.sbarquero.comparadorprecios.controlador;

import com.sbarquero.comparadorprecios.Main;
import com.sbarquero.comparadorprecios.auxiliar.FechaHora;
import com.sbarquero.comparadorprecios.modelo.Precio;
import com.sbarquero.comparadorprecios.modelo.PrecioTienda;
import com.sbarquero.comparadorprecios.modelo.TiendaListado;
import com.sbarquero.comparadorprecios.auxiliar.Util;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

/**
 * Clase controladora de la vista FXML de Precio. En esta clase se implementa todo el funcionamiento para el
 * tratamiento del precio de un artículo.
 *
 * @author Santiago Barquero <sbarquero AT gmail.com>
 */
public class PrecioController implements Initializable {

    private PrecioTienda precioTienda;
    // HashMap con el listado Id y nombre de las tiendas
    private HashMap<Integer, String> listaTiendas;
    // Observable list con las tiendas seleccionables
    private ObservableList<TiendaListado> observableListTiendas;
    // Recursos para la internacionalización
    private ResourceBundle resource;
    // Dirección URL base a la web API
    private final static String URL_BASE = Main.getUrlBase();
    // Window de la aplicación
    private Window window;

    @FXML
    private Label labelId;
    @FXML
    private Label lblArticulo;
    @FXML
    private ComboBox<TiendaListado> cmbTienda;
    @FXML
    private TextField edPrecio;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblFecha;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnBorrar;
    @FXML
    private AnchorPane anchorPanePrecio;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resource = rb;
        window = Main.getWindow();
        precioTienda = new PrecioTienda();
        observableListTiendas = FXCollections.observableArrayList();

        llenarComboBoxTiendas();

        labelId.setText(precioTienda.getId().toString());
        lblArticulo.setText(precioTienda.getDescripcionArticulo());
    }

    /**
     * Método para recibir los parámetros del precio artículo a mostrar en la vista fxml
     * https://stackoverflow.com/questions/14187963/passing-parameters-javafx-fxml
     *
     * @param precioTienda Objeto que contiene los datos para mostrar un precio
     */
    public void initData(PrecioTienda precioTienda) {
        this.precioTienda = precioTienda;
        if (precioTienda.getId() == 0) {
            labelId.setText("");
        } else {
            labelId.setText(precioTienda.getId().toString());
        }
        lblArticulo.setText(precioTienda.getDescripcionArticulo());
        edPrecio.setText(precioTienda.getImporte().toString());
        String fechaBD = precioTienda.getFecha();
        String fechaVista = FechaHora.decodifica(fechaBD);
        lblFecha.setText(fechaVista);

        TiendaListado tienda = new TiendaListado();
        tienda.setId(precioTienda.getTiendaId());
        tienda.setNombre(precioTienda.getNombreTienda());

        int indice = 0;
        // Muestra la tienda del precio en el comboBox
        for (indice = 0; indice < observableListTiendas.size(); indice++) {
            if (observableListTiendas.get(indice).getNombre().equals(tienda.getNombre())) {
                cmbTienda.getSelectionModel().select(indice);
            }
        }
    }

    /**
     * Acción al pulsar el botón cancelar.
     */
    @FXML
    private void onActionBotonCancelar(ActionEvent event) {
        // https://stackoverflow.com/questions/13567019/close-fxml-window-by-code-javafx
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Acción al pulsar el botón Guardar. 
     */
    @FXML
    private void onActionBotonGuardar(ActionEvent event) {
        Precio precio = new Precio();
        double importe = 0;
        
        try {
            importe = Double.parseDouble(edPrecio.getText().replace(',', '.'));
            int indice = cmbTienda.getSelectionModel().getSelectedIndex();
            TiendaListado tienda = observableListTiendas.get(indice);
            precio.setTiendaId(tienda.getId());
            
        } catch (NumberFormatException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.precio_debe_ser_numerico"));
            return;
        } catch (ArrayIndexOutOfBoundsException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.seleccione_tienda"));
            return;
        }

        precio.setImporte(importe);
        precio.setFecha(FechaHora.actualBD());
        precio.setArticuloId(precioTienda.getArticuloId());

        // Si es un precio nuevo hago un POST. Es nuevo cuando el campo de ID en la vista está vacio
        if (labelId.getText().isEmpty()) {
            HttpResponse<JsonNode> response = Unirest.post(URL_BASE + "/Precios")
                    .header("Content-Type", "application/json")
                    .body(precio).asJson();
        } // Si se modifica un precio hago un PUT
        else {
            precio.setId(precioTienda.getId());
            HttpResponse<JsonNode> response = Unirest.put(URL_BASE + "/Precios/" + precio.getId())
                    .header("Content-Type", "application/json")
                    .body(precio).asJson();
        }

        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    /**
     * Método que implementa la acción de borrar un precio. Hace llamada HTTP DELETE a la web API.
     */
    @FXML
    private void onActionBotonBorrar(ActionEvent event) {
        int idBorrar = precioTienda.getId();
        if (precioTienda.getId() != 0) {
            boolean deseaBorrar = Util.confirmar(window, resource.getString("txt.confirmacion"),
                    resource.getString("txt.seguro_borrar_precio") + "\n"
                    + "\n " + resource.getString("lbl.id") + ": " + idBorrar
                    + "\n " + resource.getString("lbl.articulo") + ": " + precioTienda.getDescripcionArticulo()
                    + "\n " + resource.getString("lbl.tienda") + ": " + precioTienda.getNombreTienda()
                    + "\n " + resource.getString("lbl.precio") + ": " + precioTienda.getImporte() + " €");
            if (deseaBorrar) {
                HttpResponse<String> response = Unirest.delete(URL_BASE + "/Precios/" + precioTienda.getId()).asString();
            }
        }
        Stage stage = (Stage) btnBorrar.getScene().getWindow();
        stage.close();

    }

    /**
     * Llena ComboBox con todas las tiendas
     */
    private void llenarComboBoxTiendas() {
        //observableListTiendas.clear();
        HttpResponse<TiendaListado[]> tiendaResponse = Unirest.get(URL_BASE + "/Tiendas").asObject(TiendaListado[].class);

        if (tiendaResponse.isSuccess()) {
            observableListTiendas.setAll(new ArrayList<TiendaListado>(Arrays.asList(tiendaResponse.getBody())));
            //observableListTiendas.addAll(new ArrayList<Tienda>(Arrays.asList(tiendaResponse.getBody())));
        }
        cmbTienda.setItems(observableListTiendas);

        // Doy formato a la celda del ComboBox la lista
        // https://examples.javacodegeeks.com/desktop-java/javafx/combobox/javafx-combobox-example/
        // https://stackoverflow.com/questions/28384803/listview-using-custom-cell-factory-doesnt-update-after-items-deleted
        // Note that CellFactory needs to be set for both the ListView portion
        // and the Button portion of the ComboBox for correct rendering
        cmbTienda.setCellFactory((ListView<TiendaListado> param) -> {
            final ListCell<TiendaListado> cell = new ListCell<TiendaListado>() {

                @Override
                protected void updateItem(TiendaListado item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(item.getNombre());
                    } else {
                        setText(null);
                    }
                }
            };
            return cell;
        });
        cmbTienda.setButtonCell(new ListCell<TiendaListado>() {

            @Override
            protected void updateItem(TiendaListado item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getNombre());
                } else {
                    setText(null);
                }
            }
        });
    }

}
