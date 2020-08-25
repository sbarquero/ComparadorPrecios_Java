package com.sbarquero.comparadorprecios.controlador;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import com.sbarquero.comparadorprecios.Main;
import com.sbarquero.comparadorprecios.auxiliar.DateTimeTableCell;
import com.sbarquero.comparadorprecios.auxiliar.FechaHora;
import com.sbarquero.comparadorprecios.auxiliar.PrecioTableCell;
import com.sbarquero.comparadorprecios.modelo.Articulo;
import com.sbarquero.comparadorprecios.modelo.ArticuloListado;
import com.sbarquero.comparadorprecios.modelo.Precio;
import com.sbarquero.comparadorprecios.modelo.PrecioTienda;
import com.sbarquero.comparadorprecios.modelo.TiendaListado;
import com.sbarquero.comparadorprecios.auxiliar.Util;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.http.conn.HttpHostConnectException;
import sun.misc.BASE64Decoder;

/**
 * Clase controladora de la vista FXML de Artículos
 *
 * Peticiones a web API http://kong.github.io/unirest-java/#requests
 *
 * @author Santiago Barquero López - 2º DAM <sbarquero AT gmail.com>
 */
public class ArticulosController implements Initializable {

    // Referencia a la aplicación principal
    private MainController mainApp;
    // URL base a la web API
    private final static String URL_BASE = Main.getUrlBase();

    // Artículo seleccionado de la lista
    private Articulo articuloSeleccionado;
    // ObservableList que contiene la lista de los artículos
    private ObservableList<ArticuloListado> observableListArticulos;
    // HashMap con el listado Id y nombre de las tiendas
    private HashMap<Integer, String> hashMapTiendas;
    // ObservableList con la lista de precios
    private ObservableList<PrecioTienda> observableListPrecios;
    // Contiene los recursos para internacionalización
    private ResourceBundle resource;
    // Contiene enlace a la ventana de Main
    private Window window;

    @FXML
    private ListView<ArticuloListado> listViewArticulos;
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private TextField txtEan;
    @FXML
    private ImageView imgArticulo;
    @FXML
    private JFXTimePicker timeArticulo;
    @FXML
    private TableColumn<PrecioTienda, Number> columnId;
    @FXML
    private TableColumn<PrecioTienda, Number> columnPrecio;
    @FXML
    private TableColumn<PrecioTienda, String> columnTienda;
    @FXML
    private TableColumn<PrecioTienda, String> columnFecha;
    @FXML
    private JFXDatePicker dateArticulo;
    @FXML
    private AnchorPane anchorPaneListaArticulos;
    @FXML
    private TextField txtBuscar;
    @FXML
    private Button btnBuscar;
    @FXML
    private TableView<PrecioTienda> tableViewPrecios;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnBorrar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnNuevoPrecio;
    @FXML
    private AnchorPane paneArticulo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resource = rb;
        // Obtengo el enlace al Window de Main
        window = Main.getWindow();
        // Artículo que se está visualizando
        articuloSeleccionado = new Articulo();
        // Inicilizo lista artículos
        observableListArticulos = FXCollections.observableArrayList();
        // Inicializo listaPrecios
        observableListPrecios = FXCollections.observableArrayList();

        // Muesto ventana cargando mientas se descarga los artículos
        Alert cargandoAlert = Util.muestraCargando(window, rb);

        try {
            hashMapTiendas = crearHashMapTiendas();
            llenarListViewArticulos();
        } catch (Exception e) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        } finally {
            cargandoAlert.close();
        }

        btnBuscar.setText("\u2315"); // Muestra icono lupa

        // Asocia propiedades a la tabla de precios
        // http://tutorials.jenkov.com/javafx/tableview.html
        columnId.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        columnPrecio.setCellValueFactory(cellData -> cellData.getValue().importeProperty());
        columnTienda.setCellValueFactory(cellData -> cellData.getValue().nombreTiendaProperty());
        columnFecha.setCellValueFactory(cellData -> cellData.getValue().fechaProperty());

        // Personalizar columna precio de la tabla
        columnPrecio.setCellFactory(celda -> {
            return new PrecioTableCell();
        });

        // Personalizar columna fecha de la tabla
        columnFecha.setCellFactory(celda -> {
            return new DateTimeTableCell();
        });

        inicializaArticulo();
    }

    /**
     * Método que recibe referencia a la aplicación principal
     * https://code.makery.ch/es/library/javafx-tutorial/
     * @param mainApp
     */
    public void setMainController(MainController mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Método que responde al clic sobre la lista de artículos
     */
    @FXML
    private void onMouseClickedListaArticulos(MouseEvent event) {
        try {
            if (haCambiadoArticulo()) {
                boolean descartarCambios = Util.confirmar(window, resource.getString("txt.confirmacion"), resource.getString("txt.quiere_descartar_cambio"));
                if (!descartarCambios) {
                    listViewArticulos.getSelectionModel().clearSelection();
                    btnGuardar.requestFocus();
                    return; // Salgo si se elige descartar los cambios
                }
            }
            // Identifico el artículo pinchado de la lista
            articuloSeleccionado.setId(listViewArticulos.getSelectionModel().getSelectedItem().getId());
            visualizarArticulo();
            btnBorrar.setDisable(false);
            btnNuevoPrecio.setDisable(false);
        } catch (HttpHostConnectException ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        } catch (NullPointerException ex) {
            // Se ha seleccionado un elemento vacío de la lista. No hago nada.
        }
    }

    /**
     * Método que responde al clic en el botón buscar
     */
    @FXML
    private void onActionBotonBuscar(ActionEvent event) {
        buscarArticulo();
    }

    /**
     * Intercepto la pulsación de la tecla Enter en el TextField de Buscar para que simule la pulsación sobre
     * el botón de la lupa
     */
    @FXML
    private void onKeyPressedEnterBuscar(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            buscarArticulo();
        }
    }

    /**
     * Acción al pulsar el botón de Nuevo artículo.
     */
    @FXML
    private void onActionBotonNuevo(ActionEvent event) {
        // Compruebo si ha habido cambios en los datos de un artículo existente para pedir confirmación 
        if (haCambiadoArticulo()) {
            boolean descartarCambios = Util.confirmar(window, resource.getString("txt.confirmacion"), resource.getString("txt.quiere_descartar_cambio"));
            if (!descartarCambios) {
                return;
            }
        }
        inicializaArticulo();
        txtBuscar.setText("");
        try {
            buscarArticulo();
        } catch (Exception ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
        txtDescripcion.requestFocus();
    }

    /**
     * Acción al pulsar botón de Borrar un artículo.
     */
    @FXML
    private void onActionBotonBorrar(ActionEvent event) {
        if (txtId.getText().equals("")) {
            inicializaArticulo();
            txtBuscar.requestFocus();
        } else {
            boolean deseaBorrar = Util.confirmar(window, resource.getString("txt.confirmacion"), resource.getString("txt.seguro_quiere_borrar"));
            if (deseaBorrar) {
                try {
                    HttpResponse<String> response = Unirest.delete(URL_BASE + "/Articulos/" + articuloSeleccionado.getId()).asString();
                    llenarListViewArticulos();
                    inicializaArticulo();
                    txtBuscar.requestFocus();
                } catch (Exception ex) {
                    Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
                }
            }
        }
    }

    /**
     * Acción al pulsar botón Guardar un artículo
     */
    @FXML
    private void onActionBotonGuardar(ActionEvent event) throws HttpHostConnectException {
        // Si la descripción está vacía muestra mensaje
        if (txtDescripcion.getText().equals("")) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.articulo_sin_descripcion"));
            return;
        }
        // Recojo datos de la vista
        articuloSeleccionado.setDescripcion(txtDescripcion.getText());
        articuloSeleccionado.setEan(txtEan.getText());
        articuloSeleccionado.setFechaAlta(FechaHora.actualBD());
        try {
            // Si es un artículo nuevo hago un POST
            if (esArticuloNuevo()) {
                HttpResponse<Articulo> response = Unirest.post(URL_BASE + "/Articulos")
                        .header("Content-Type", "application/json")
                        .body(articuloSeleccionado).asObject(Articulo.class);
                articuloSeleccionado = response.getBody();
                visualizarArticulo();
            } // Sino hago una actualización con PUT
            else {
                HttpResponse<JsonNode> response = Unirest.put(URL_BASE + "/Articulos/" + articuloSeleccionado.getId())
                        .header("Content-Type", "application/json")
                        .body(articuloSeleccionado).asJson();
            }
            llenarListViewArticulos();
            btnBorrar.setDisable(false);
        } catch (Exception ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
    }

    /**
     * Acción que se produce al pinchar sobre un precio en la tabla de precios.
     */
    @FXML
    private void onMouseClickedPrecio(MouseEvent event) throws HttpHostConnectException {
        int indice = tableViewPrecios.getSelectionModel().getSelectedIndex();
        if (indice == -1) {
            return;
        }

        PrecioTienda precioTienda = observableListPrecios.get(indice);

        // Abro nueva ventana para modificar el precio 
        mostrarVentanaPrecio(resource.getString("txt.actualizar_precio"), precioTienda);
        // Actualiza artículo tras cerrar ventana precio
        visualizarArticulo();
    }

    /**
     * Acción al pulsar botón de nuevo Precio.
     */
    @FXML
    private void onActionBotonNuevoPrecio(ActionEvent event) {
        if (esArticuloNuevo()) {
            //Window window = btnNuevoPrecio.getScene().getWindow();
            Util.mensajeInformacion(window, resource.getString("txt.informacion"), resource.getString("txt.debe_guardar_articulo"));
            return;
        }
        // Creo objeto PrecioTienda vacio para mostrar en la ventana
        PrecioTienda precioTienda = new PrecioTienda();
        // Inicializo objeto con los datos conocidos
        precioTienda.setArticuloId(articuloSeleccionado.getId());
        precioTienda.setDescripcionArticulo(articuloSeleccionado.getDescripcion());
        precioTienda.setFecha(FechaHora.actualBD());

        //precioTienda.setTiendaId(1);
        //precioTienda.setNombreTienda("Tienda 1");
        String titulo = resource.getString("txt.nuevo_precio");
        try {
            mostrarVentanaPrecio(titulo, precioTienda);
            // Actualiza artículo tras cerrar ventana precio
            visualizarArticulo();
        } catch (Exception ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
    }

    /**
     * Método que hace la consulta de búsqueda a la Web API
     */
    private void buscarArticulo() {
        String textoBuscado = txtBuscar.getText();
        try {
            if (!textoBuscado.equals("")) {
                HttpResponse<ArticuloListado[]> articulosResponse = Unirest.get(URL_BASE + "/Articulos?buscar=" + txtBuscar.getText()).asObject(ArticuloListado[].class);
                if (articulosResponse.isSuccess()) {
                    observableListArticulos.clear();
                    observableListArticulos.addAll(Arrays.asList(articulosResponse.getBody()));
                }
            } else {
                llenarListViewArticulos();
                inicializaArticulo();
            }
        } catch (Exception ex) {
            Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_conexion_web_api"));
        }
        txtBuscar.setText("");
        listViewArticulos.requestFocus();
    }

    /**
     * Creo HashMap tiendas con todas la tiendas que devuelve la Web API
     */
    private HashMap<Integer, String> crearHashMapTiendas() throws HttpHostConnectException {
        HashMap<Integer, String> tiendas = new HashMap<>();
        // Obtener lista tiendas de Web API
        HttpResponse<TiendaListado[]> response = Unirest.get(URL_BASE + "/Tiendas").asObject(TiendaListado[].class);
        // Si GET devuelve respuesta cargo lista
        if (response.isSuccess()) {
            for (TiendaListado t : response.getBody()) {
                tiendas.put(t.getId(), t.getNombre());
            }
        }
        return tiendas;
    }

    /**
     * Lleno ListView con todos los artículos
     *
     * https://stackoverflow.com/questions/36657299/how-can-i-populate-a-listview-in-javafx-using-custom-objects
     * https://stackoverflow.com/questions/28384803/listview-using-custom-cell-factory-doesnt-update-after-items-deleted
     */
    private void llenarListViewArticulos() throws HttpHostConnectException {
        observableListArticulos.clear();
        HttpResponse<ArticuloListado[]> articulosResponse = Unirest.get(URL_BASE + "/Articulos").asObject(ArticuloListado[].class);

        if (articulosResponse.isSuccess()) {
            observableListArticulos.addAll(new ArrayList<ArticuloListado>(Arrays.asList(articulosResponse.getBody())));
        }
        listViewArticulos.setItems(observableListArticulos);

        // Doy formato a la celda de la lista
        listViewArticulos.setCellFactory((ListView<ArticuloListado> param) -> {
            ListCell<ArticuloListado> cell = new ListCell<ArticuloListado>() {

                @Override
                protected void updateItem(ArticuloListado articulo, boolean empty) {
                    super.updateItem(articulo, empty);
                    if (empty || articulo == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(articulo.getDescripcion());
                    }
                }
            };
            return cell;
        });
    }

    /**
     * Genero una observableListPrecios con los precios artículos en las tiendas y después la asigno al
     * TableView de tablePrecios
     */
    private void visualizarListaPrecios(Articulo articulo) {
        observableListPrecios.clear();
        for (Precio p : articulo.getPrecios()) {
            PrecioTienda precioTienda = new PrecioTienda();
            precioTienda.setId(p.getId());
            precioTienda.setImporte(p.getImporte());
            precioTienda.setFecha(p.getFecha());
            precioTienda.setArticuloId(p.getArticuloId());
            precioTienda.setDescripcionArticulo(articulo.getDescripcion());
            precioTienda.setTiendaId(p.getTiendaId());
            precioTienda.setNombreTienda(hashMapTiendas.get(p.getTiendaId()));
            observableListPrecios.add(precioTienda);
        }
        tableViewPrecios.setItems(observableListPrecios);
    }

    /**
     * Muestra ventana Precio con los detalles del PrecioTienda pasado como parámetro.
     * @param titulo Título que mostrará la ventana.
     * @param precioTienda Objeto que contiene el precioTienda pasado.
     * @throws HttpHostConnectException Error producido por fallo al hacer la petición a la web API.
     */
    private void mostrarVentanaPrecio(String titulo, PrecioTienda precioTienda) throws HttpHostConnectException {
        Stage stagePrecio = new Stage();
        stagePrecio.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("../vista/Precio.fxml"), resource);
        try {
            Parent root = loader.load();
            PrecioController precioController = loader.getController();

            // https://stackoverflow.com/questions/14187963/passing-parameters-javafx-fxml
            precioController.initData(precioTienda);
            Scene scenePrecio = new Scene(root);
            stagePrecio.setScene(scenePrecio);
            stagePrecio.getIcons().add(new Image("/com/sbarquero/comparadorprecios/resources/images/cesta.png"));
            stagePrecio.setTitle(titulo);
            stagePrecio.setResizable(false);
            // Posiciono ventana precio en la mitad de la pantalla actual
            stagePrecio.initOwner(window);
            stagePrecio.setX(window.getX() + window.getWidth() / 2 - 225);
            stagePrecio.setY(window.getY() + window.getHeight() / 2 - 155);
            stagePrecio.showAndWait();
        } catch (IOException ex) {
            Util.mensajeError(window, resource.getString("txt.errir"), resource.getString("txt.error_mostrar_precio"));
        }
    }

    /**
     * Método que inicializa la vista del artículo y crea un objeto artículo vacío.
     */
    private void inicializaArticulo() {
        txtId.setText("");
        txtDescripcion.setText("");
        txtEan.setText("");
        imgArticulo.setImage(null);
        dateArticulo.setValue(LocalDate.now());
        timeArticulo.setValue(LocalTime.now());
        btnBorrar.setDisable(true);
        observableListPrecios.clear();
        txtBuscar.setFocusTraversable(true);
        txtBuscar.requestFocus();

        articuloSeleccionado = new Articulo();
    }

    /**
     * Hace GET del ArtículoSeleccionado y lo visualiza en la vista.
     */
    private void visualizarArticulo() throws HttpHostConnectException {
        // Petición GET a la web API del artículo por ID
        HttpResponse<Articulo> articuloResponse = Unirest.get(URL_BASE + "/Articulos/" + articuloSeleccionado.getId()).asObject(Articulo.class);
        // Inicializo el artículo
        articuloSeleccionado = articuloResponse.getBody();
        // Visualizo el artículo
        txtId.setText(articuloSeleccionado.getId().toString());
        txtDescripcion.setText(articuloSeleccionado.getDescripcion());
        txtEan.setText(articuloSeleccionado.getEan());
        // Fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDate localDate = LocalDate.parse(articuloSeleccionado.getFechaAlta(), formatter);
        dateArticulo.setValue(localDate);
        // Hora
        LocalTime localTime = LocalTime.parse(articuloSeleccionado.getFechaAlta(), formatter);
        timeArticulo.setValue(localTime);
        //Imagen
        String imagenBase64 = articuloSeleccionado.getImagen();
        if (imagenBase64 != null) {
            // https://stackoverflow.com/questions/11537434/javafx-embedding-encoded-image-in-fxml-file/12000160
            BASE64Decoder base64Decoder = new BASE64Decoder();
            try {
                ByteArrayInputStream entrada = new ByteArrayInputStream(base64Decoder.decodeBuffer(imagenBase64));
                Image imagen = new Image(entrada);
                imgArticulo.setImage(imagen);
            } catch (Exception ex) {
                Util.mensajeError(window, resource.getString("txt.error"), resource.getString("txt.error_base64"));
            }
        } else {
            imgArticulo.setImage(null);
        }

        visualizarListaPrecios(articuloSeleccionado);
    }

    /**
     * Método que devuelve booleano según sea un artículo nuevo o no.
     *
     * @return true si el artículo es nuevo y false si el artículo ya existe.
     */
    private boolean esArticuloNuevo() {
        return articuloSeleccionado.getId() == null;
    }

    /**
     * Método que devuelve booleano según haya cambiado el artículo en la vista con respecto al artículo
     * inicial.
     *
     * @return true si ha cambiado y false si no ha cambiado el artículo
     */
    private boolean haCambiadoArticulo() {
        // Si Artículo es nuevo y no se ha editado
        if (esArticuloNuevo() && txtDescripcion.getText().equals("") && txtEan.getText().equals("")) {
            return false;
        }
        if (!esArticuloNuevo()) {
            boolean haCambiadoDescripcion = !articuloSeleccionado.getDescripcion().equals(txtDescripcion.getText());
            // Compruebo si ha cambiado el código EAN
            boolean haCambiadoEan;
            if (articuloSeleccionado.getEan() == null || txtEan.getText() == null) {
                haCambiadoEan = articuloSeleccionado.getEan() != txtEan.getText();
            } else {
                haCambiadoEan = !articuloSeleccionado.getEan().equals(txtEan.getText());
            }
            return (haCambiadoDescripcion || haCambiadoEan);
        }
        return true;
    }

}
