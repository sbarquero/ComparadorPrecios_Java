package com.sbarquero.comparadorprecios.controlador;

import com.sbarquero.comparadorprecios.Main;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import com.sbarquero.comparadorprecios.auxiliar.Util;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

/**
 * FXML Controller class
 *
 * @author Santiago Barquero López - 2º DAM <sbarquero AT gmail.com>
 */
public class MainController implements Initializable {

    // URL base de la web API
    private final static String URL_BASE = Main.getUrlBase();
    private FXMLLoader loader;
    // Recursos para la internacionalización
    private ResourceBundle resourceBundle; // Internacionalización
    // Contenedor principal
    private BorderPane rootLayout;
    // Window de la aplicación
    private Window window;
    // Indica si se tiene conexión con la web API
    private boolean conectado = false;

    @FXML
    private Label lblNombreVentana;
    @FXML
    private MenuItem menuArticulos;
    @FXML
    private MenuItem menuTiendas;
    @FXML
    private Label lblInformativo;
    @FXML
    private VBox mainPane;
    @FXML
    private Button btnCerrar;
    @FXML
    private BorderPane borderPaneVentana;

    /**
     * Inicializa el controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        resourceBundle = rb;
        rootLayout = borderPaneVentana;
        rootLayout.setVisible(false);
        lblNombreVentana.setVisible(false);
        lblInformativo.setVisible(true);

        // Runnable que se ejecuta después de mostrar la vista.
        // Intenta la conexión y muestra conectado o no según tenga conexión con la web API
        // https://riptutorial.com/javafx/example/7291/updating-the-ui-using-platform-runlater
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                window = Main.getWindow();
                probarWebApi();
            }
        };
        Platform.runLater(runnable);
    }

    /**
     * Método que se ejecuta al elegir Artículos en el menú. Abre la ventana de los Artículos
     *
     * @param event
     * @throws IOException
     */
    @FXML
    private void onActionArticulos(ActionEvent event) throws IOException {
        rootLayout.setVisible(true);
        if (conectado) {
            loader = new FXMLLoader(MainController.class.getResource("../vista/Articulos.fxml"), resourceBundle);

            AnchorPane articulosPane = (AnchorPane) loader.load();

            rootLayout.setCenter(articulosPane);

            ArticulosController controller = loader.getController();

            controller.setMainController(this);

            // Configuro la etiqueta superior con el nombre de la ventana abierta.
            lblNombreVentana.setText(resourceBundle.getString("menu.articulos"));
            lblNombreVentana.setVisible(true);
            lblInformativo.setVisible(false);
        } else {
            probarWebApi();
        }
    }

    /**
     * Método que se ejecuta al elegir Tiendas en el menú. Abre la ventana de las Tiendas
     */
    @FXML
    private void onActionTiendas(ActionEvent event) throws IOException {
        rootLayout.setVisible(true);
        if (conectado) {
            //lblInformativo.setVisible(true);
            loader = new FXMLLoader(MainController.class.getResource("../vista/Tiendas.fxml"), resourceBundle);

            AnchorPane tiendaPane = (AnchorPane) loader.load();

            rootLayout.setCenter(tiendaPane);

            TiendasController controller = loader.getController();

            controller.setMainController(this);

            // Configuro la etiqueta superior con el nombre de la ventana abierta.
            lblNombreVentana.setText(resourceBundle.getString("menu.tiendas"));
            lblNombreVentana.setVisible(true);
            lblInformativo.setVisible(false);
        } else {
            probarWebApi();
        }
    }

    /**
     * Método que se ejecuta al elegir Cambiar idioma en el menú. Graba el idioma seleccionado en un fichero
     * de propiedades, para que la próxima vez que arranque la aplicación arranque con el idioma seleccionado.
     */
    @FXML
    private void onActionCambiarIdioma() {
        Properties prop = new Properties();
        try {
            prop.load(new FileReader("configuracion.properties"));
            String idioma = prop.getProperty("idioma");
            if (idioma.equals("es")) {
                prop.setProperty("idioma", "en");
            } else {
                prop.setProperty("idioma", "es");
            }
            Util.mensajeInformacion(window, resourceBundle.getString("txt.cambio_idioma"), resourceBundle.getString("txt.cambio_idioma_mensaje"));
            prop.store(new FileWriter("configuracion.properties"), idioma);
        } catch (IOException ex) {
            System.out.println(resourceBundle.getString("txt.error_acceso_configuracion"));
        }

    }

    /**
     * Método que se ejecuta al elegir Salir en el menú. Permite salir del programa previa confirmación.
     */
    @FXML
    private void onActionSalir() {
        boolean isSalir = Util.confirmar(window, resourceBundle.getString("txt.salir"), resourceBundle.getString("txt.salir_mensaje"));
        if (isSalir) {
            //https://stackoverflow.com/questions/46053974/using-platform-exit-and-system-exitint-together
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * Método que se ejecuta al elegir "Acerca de" en el menú. Abre la ventana con la información del
     * programa.
     */
    @FXML
    private void onActionAcercaDe() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(resourceBundle.getString("menu.acerca_de"));
        alert.setGraphic(null);
        alert.initStyle(StageStyle.UTILITY); // Quita icono ventana
        alert.setHeaderText(null); // Quita cabecera
        alert.setContentText(
                resourceBundle.getString("nombre_aplicacion") + "\n\n"
                + "Santiago Barquero López - 2º DAM" + "\n\n"
                + " " + resourceBundle.getString("txt.creditos") + "\n"
                + "  " + resourceBundle.getString("txt.credito1"));
        alert.initOwner(window);
        alert.showAndWait();
    }

    /**
     * Método que implementa la acción de cerrar una ventana.
     * @param event 
     */
    @FXML
    private void onActionBotonCerrar(ActionEvent event) {
        rootLayout.setVisible(false);
    }

    /**
     * Comprueba si hay conexión con la web API y recupera la versión de la API.
     */
    private void probarWebApi() {
        lblInformativo.setVisible(true);
        try {
            lblInformativo.setText(resourceBundle.getString("lbl.conectando"));
            Main.setTitle(resourceBundle.getString("lbl.conectando"));
            HttpResponse<String> response = Unirest.get(URL_BASE + "/Version").asString();
            lblInformativo.setVisible(false);
            Main.setTitle(resourceBundle.getString("lbl.version") + " " + response.getBody());
            conectado = true;
        } catch (UnirestException ex) {
            lblInformativo.setText(resourceBundle.getString("lbl.no_conectado"));
            Main.setTitle(resourceBundle.getString("lbl.no_conectado"));
            Util.mensajeError(window, resourceBundle.getString("txt.error"), resourceBundle.getString("txt.error_conexion_web_api"));
            conectado = false;
        }
    }

}
