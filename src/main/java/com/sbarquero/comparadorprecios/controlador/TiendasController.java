package com.sbarquero.comparadorprecios.controlador;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import com.sbarquero.comparadorprecios.Main;
import com.sbarquero.comparadorprecios.auxiliar.FechaHora;
import com.sbarquero.comparadorprecios.modelo.Tienda;
import com.sbarquero.comparadorprecios.modelo.TiendaListado;
import com.sbarquero.comparadorprecios.auxiliar.Util;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.http.conn.HttpHostConnectException;

/**
 * Clase controladora de la vista FXML de Tiendas
 *
 * Peticiones a web API http://kong.github.io/unirest-java/#requests
 *
 * @author Santiago Barquero López - 2º DAM <sbarquero AT gmail.com>
 */
public class TiendasController implements Initializable {

    // Dirección URL base de la web API
    private final static String URL_BASE = Main.getUrlBase();
    // Referencia a la aplicación principal
    private MainController mainApp;
    // Tienda seleccionada de la lista
    private Tienda tiendaSeleccionada;
    // ObservableList que contiene la lista de las tiendas
    private ObservableList<TiendaListado> observableListTiendas;
    // Contiene los recursos para internacionalización
    private ResourceBundle resource;
    // Window de la aplicación
    private Window window;

    @FXML
    private ListView<TiendaListado> listViewTiendas;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtLatitud;
    @FXML
    private JFXTimePicker timeTienda;
    @FXML
    private JFXDatePicker dateTienda;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnGuardar;
    @FXML
    private TextField txtLongitud;
    @FXML
    private AnchorPane tiendaPane;
    @FXML
    private WebView webViewMapa;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resource = rb;
        window = Main.getWindow();
        tiendaSeleccionada = new Tienda();
        // Inicilizo lista artículos
        observableListTiendas = FXCollections.observableArrayList();

        Alert cargandoAlert = Util.muestraCargando(window, rb);
        try {
            llenarListViewTiendas();
        } catch (HttpHostConnectException e) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        } finally {
            cargandoAlert.close();
        }

        btnBuscar.setText("\u2315"); // Muestra icono lupa

        inicializaTienda();
    }

    /**
     * Método que recibe referencia a la aplicación principal
     *
     * @param mainApp Enlace al objeto MainControlador
     */
    public void setMainController(MainController mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Método que responde al clic sobre la lista de artículos
     */
    @FXML
    private void onMouseClickedListaTiendas(MouseEvent event) {
        try {
            if (haCambiadoTienda()) {
                boolean descartarCambios = Util.confirmar(window, resource.getString("txt.confirmacion"), resource.getString("txt.quiere_descartar_cambio"));
                if (!descartarCambios) {
                    listViewTiendas.getSelectionModel().clearSelection();
                    btnGuardar.requestFocus();
                    return; // Salgo
                }
            }
            // Identifico el artículo pinchado de la lista
            tiendaSeleccionada.setId(listViewTiendas.getSelectionModel().getSelectedItem().getId());
            visualizarTienda();
            btnBorrar.setDisable(false);
        } catch (NumberFormatException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), ex.getMessage());
        } catch (NullPointerException ex) {
            // Se ha seleccionado un elemento vacío de la lista de tiendas. No hago nada
        } catch (HttpHostConnectException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
    }

    /**
     * Método que responde al clic en el botón buscar
     */
    @FXML
    private void onActionBotonBuscar(ActionEvent event) {
        buscarTienda();
    }

    /**
     * Intercepto la pulsación de la tecla Enter en el TextField de Buscar para que simule la pulsación sobre
     * el botón de la lupa
     */
    @FXML
    private void onKeyPressedEnterBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            buscarTienda();
        }
    }

    /**
     * Acción al pulsar el botón Nuevo de Tiendas
     */
    @FXML
    private void onActionBotonNuevo(ActionEvent event) {
        try {
            // Compruebo si ha habido cambios en los datos de un artículo existente para pedir confirmación 
            if (haCambiadoTienda()) {
                boolean descartarCambios = Util.confirmar(window, resource.getString("txt.confirmacion"), resource.getString("txt.quiere_descartar_cambio"));
                if (!descartarCambios) {
                    return;
                }
            }
            inicializaTienda();
            txtBuscar.setText("");
            buscarTienda();
        } catch (NumberFormatException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), ex.getMessage());
        } catch (Exception ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
        txtNombre.requestFocus();
    }

    /**
     * Acción al pulsar el botón Borrar de Tiendas
     */
    @FXML
    private void onActionBotonBorrar(ActionEvent event) {
        if (txtId.getText().equals("")) {
            inicializaTienda();
            txtBuscar.requestFocus();
        } else {
            boolean deseaBorrar = Util.confirmar(window, resource.getString("txt.confirmacion"), resource.getString("txt.seguro_quiere_borrar"));
            if (deseaBorrar) {
                try {
                    HttpResponse<String> response = Unirest.delete(URL_BASE + "/Tiendas/" + tiendaSeleccionada.getId()).asString();
                    llenarListViewTiendas();
                    inicializaTienda();
                    txtBuscar.requestFocus();
                } catch (Exception ex) {
                    Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
                }
            }
        }
    }

    /**
     * Acción al pulsar el botón Guardar en Tiendas
     */
    @FXML
    private void onActionBotonGuardar(ActionEvent event) throws HttpHostConnectException {
        // Si la descripción está vacía muestra mensaje
        if (txtNombre.getText().equals("")) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.tienda_sin_nombre"));
            return;
        }
        // Recojo datos de la vista
        try {
            tiendaSeleccionada.setNombre(txtNombre.getText());
            // Intento recuperar la latitud
            try {
                Double latitud = txtLatitud.getText().isEmpty() ? null : Double.parseDouble(txtLatitud.getText());
                if (latitud != null && (latitud < -90 || latitud > 90)) {
                    throw new NumberFormatException();
                }
                tiendaSeleccionada.setLatitud(latitud);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(resource.getString("txt.latitud_erronea"));
            }
            // Intento recupera la longitud
            try {
                Double longitud = txtLongitud.getText().isEmpty() ? null : Double.parseDouble(txtLongitud.getText());
                if (longitud != null && (longitud < -180 || longitud > 180)) {
                    throw new NumberFormatException();
                }
                tiendaSeleccionada.setLongitud(longitud);
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(resource.getString("txt.longitud_erronea"));
            }
            tiendaSeleccionada.setFechaAlta(FechaHora.actualBD());
            // Si es un artículo nuevo hago un POST
            if (esTiendaNueva()) {
                HttpResponse<Tienda> response = Unirest.post(URL_BASE + "/Tiendas")
                        .header("Content-Type", "application/json")
                        .body(tiendaSeleccionada).asObject(Tienda.class);
                tiendaSeleccionada = response.getBody();
                visualizarTienda();
            } // Sino hago una actualización con PUT
            else {
                HttpResponse<JsonNode> response = Unirest.put(URL_BASE + "/Tiendas/" + tiendaSeleccionada.getId())
                        .header("Content-Type", "application/json")
                        .body(tiendaSeleccionada).asJson();
            }
            llenarListViewTiendas();
            visualizarTienda();
            btnBorrar.setDisable(false);
        } catch (NumberFormatException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), ex.getMessage());
        } catch (HttpHostConnectException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
    }

    /**
     * Método que hace la consulta de búsqueda a la Web API
     */
    private void buscarTienda() {
        String textoBuscado = txtBuscar.getText();
        try {
            if (!textoBuscado.equals("")) {
                HttpResponse<TiendaListado[]> articulosResponse = Unirest.get(URL_BASE + "/Tiendas?buscar=" + txtBuscar.getText()).asObject(TiendaListado[].class);
                if (articulosResponse.isSuccess()) {
                    observableListTiendas.clear();
                    observableListTiendas.addAll(Arrays.asList(articulosResponse.getBody()));
                }
            } else {
                llenarListViewTiendas();
                inicializaTienda();
            }
        } catch (HttpHostConnectException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
        txtBuscar.setText("");
        listViewTiendas.requestFocus();
    }

    /**
     * Lleno ListView con todas las tiendas
     *
     * https://stackoverflow.com/questions/36657299/how-can-i-populate-a-listview-in-javafx-using-custom-objects
     * https://stackoverflow.com/questions/28384803/listview-using-custom-cell-factory-doesnt-update-after-items-deleted
     */
    private void llenarListViewTiendas() throws HttpHostConnectException {
        observableListTiendas.clear();
        HttpResponse<TiendaListado[]> articulosResponse = Unirest.get(URL_BASE + "/Tiendas").asObject(TiendaListado[].class);

        if (articulosResponse.isSuccess()) {
            observableListTiendas.addAll(new ArrayList<TiendaListado>(Arrays.asList(articulosResponse.getBody())));
        }
        listViewTiendas.setItems(observableListTiendas);

        // Doy formato a la celda de la lista
        listViewTiendas.setCellFactory((ListView<TiendaListado> param) -> {
            ListCell<TiendaListado> cell = new ListCell<TiendaListado>() {

                @Override
                protected void updateItem(TiendaListado tienda, boolean empty) {
                    super.updateItem(tienda, empty);
                    if (empty || tienda == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(tienda.getNombre());
                    }
                }
            };
            return cell;
        });
    }

    /**
     * Método que inicializa la vista de la tienda
     */
    private void inicializaTienda() {
        txtId.setText("");
        txtNombre.setText("");
        txtLatitud.setText("");
        txtLongitud.setText("");
        dateTienda.setValue(LocalDate.now());
        timeTienda.setValue(LocalTime.now());
        btnBorrar.setDisable(true);
        txtBuscar.setFocusTraversable(true);
        txtBuscar.requestFocus();
        webViewMapa.getEngine().load("");
        
        tiendaSeleccionada = new Tienda();
    }

    /**
     * Hace GET del TiendaSeleccionada y la visualiza en la vista
     */
    private void visualizarTienda() throws HttpHostConnectException {
        // Petición GET a la web API del artículo por ID
        HttpResponse<Tienda> articuloResponse = Unirest.get(URL_BASE + "/Tiendas/" + tiendaSeleccionada.getId()).asObject(Tienda.class);
        // Inicializo el artículo
        tiendaSeleccionada = articuloResponse.getBody();
        // Visualizo el artículo
        txtId.setText(tiendaSeleccionada.getId().toString());
        txtNombre.setText(tiendaSeleccionada.getNombre());
        txtLatitud.setText(tiendaSeleccionada.getLatitud() == null ? "" : String.valueOf(tiendaSeleccionada.getLatitud()));
        txtLongitud.setText(tiendaSeleccionada.getLongitud() == null ? "" : String.valueOf(tiendaSeleccionada.getLongitud()));
        // Fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDate localDate = LocalDate.parse(tiendaSeleccionada.getFechaAlta(), formatter);
        dateTienda.setValue(localDate);
        // Hora
        LocalTime localTime = LocalTime.parse(tiendaSeleccionada.getFechaAlta(), formatter);
        timeTienda.setValue(localTime);
        // Visualizar Mapa ubicación

        if (txtLatitud.getText().isEmpty() || txtLongitud.getText().isEmpty()) {
            webViewMapa.getEngine().load("");
        } else {
            webViewMapa.getEngine().load("https://www.google.es/maps/@" + txtLatitud.getText() + "," + txtLongitud.getText() + ",16z");
        }
    }

    /**
     * Devuelve un boolean según la tienda exista o no
     *
     * @return true si la tienda existe y false si la tienda no existe
     */
    private boolean esTiendaNueva() {
        return tiendaSeleccionada.getId() == null;
    }

    /**
     * Devuelve un booleano según haya cambiado la tienda en la vista con respecto a la tienda mostrada
     * inicialmente.
     *
     * @return true si la vista de la tienda se ha modificado y false si no se ha modificado.
     */
    private boolean haCambiadoTienda() throws NumberFormatException {
        // Compruebo si ha cambiado la LATITUD
        boolean haCambiadoLatitud;
        if (tiendaSeleccionada.getLatitud() == null || txtLatitud.getText().isEmpty()) {
            haCambiadoLatitud = tiendaSeleccionada.getLatitud() != null || !txtLatitud.getText().isEmpty();
        } else {
            Double latitud = Double.parseDouble(txtLatitud.getText());
            haCambiadoLatitud = !tiendaSeleccionada.getLatitud().equals(latitud);
        }
        // Compruebo si ha cambiado la LONGITUD
        boolean haCambiadoLongitud;
        if (tiendaSeleccionada.getLongitud() == null || txtLongitud.getText().isEmpty()) {
            haCambiadoLongitud = tiendaSeleccionada.getLongitud() != null || !txtLongitud.getText().isEmpty();
        } else {
            Double longitud = Double.parseDouble(txtLongitud.getText());
            haCambiadoLongitud = !tiendaSeleccionada.getLongitud().equals(longitud);
        }
        // Si Artículo es nuevo y no se ha editado
        if (esTiendaNueva() && txtNombre.getText().equals("") && !haCambiadoLatitud && !haCambiadoLongitud) {
            return false;
        }
        if (!esTiendaNueva()) {
            boolean haCambiadoNombre = !tiendaSeleccionada.getNombre().equals(txtNombre.getText());
            return (haCambiadoNombre || haCambiadoLatitud || haCambiadoLongitud);
        }
        return true;
    }

}
