package com.sbarquero.comparadorprecios;

import com.sbarquero.comparadorprecios.auxiliar.Util;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;
import javafx.scene.image.Image;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * Clase principal de la aplicación. Contiene el método main, punto de entrada de la aplicación
 *
 * @author Santiago Barquero López - 2º DAM <sbarquero AT gmail.com>
 */
public class Main extends Application {

    @SuppressWarnings("unused")
    private static Stage stageInicial;
    // Recursos para la internacionalización
    private static ResourceBundle resourceBundle;
    // Almacena URL base de la web API
    private static String URL_BASE;

    @Override
    public void start(Stage stageInicial) {
        // Guardo el Stage inicial
        this.stageInicial = stageInicial;

        establecerConfiguracion();

        try {
            // Inicializo el root layout
            Parent root = FXMLLoader.load(getClass().getResource("vista/Main.fxml"), resourceBundle);

            Scene scene = new Scene(root);

            stageInicial.setScene(scene);

            // Configuro el icono de la aplicación
            stageInicial.getIcons().add(new Image("/com/sbarquero/comparadorprecios/resources/images/cesta.png"));

            // Configuro el título
            setTitle("Conectando");
            // Establezco un ancho y alto mínimo
            stageInicial.setMinWidth(850);
            stageInicial.setMinHeight(650);

            stageInicial.show();

            // Captura el evento de cierre de la ventana (stage)
            stageInicial.setOnCloseRequest((WindowEvent event) -> {
                boolean salir = Util.confirmar(getWindow(), resourceBundle.getString("txt.salir"), resourceBundle.getString("txt.salir_mensaje"));
                if (!salir) {
                    event.consume(); //Mantengo abierta
                } else {
                    stageInicial.close(); //Cierro ventana
                    System.exit(0);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Método principal de la aplicacióm.
     *
     * @param args Sin argumentos
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Método que carga la configuración de la aplicación como el idioma y la URL a la Web API. Leo la
     * configuración de un fichero de propiedades durante el arranque de la aplicación y establezco el idioma.
     */
    private void establecerConfiguracion() {

        Properties prop = new Properties();
        String idioma = "es";

        try {
            prop.load(new FileReader("configuracion.properties"));
            idioma = prop.getProperty("idioma", "es");
            URL_BASE = prop.getProperty("URL_base", "http://localhost:50145/api");
        } catch (IOException ex) {
            prop.setProperty("idioma", "es");
            prop.setProperty("URL_base", URL_BASE);
            try {
                prop.store(new FileWriter("configuracion.properties"), "Fichero configuración ComparadorPrecios");
            } catch (IOException ex1) {
                System.out.println(resourceBundle.getString("txt.problemas_crear_fichero"));
            }
        }

        // Establezco el idioma
        Locale.setDefault(new Locale(idioma));
        resourceBundle = ResourceBundle.getBundle("com/sbarquero/comparadorprecios/i18n/mensajes");
    }

    /**
     * Método estático público que permite establecer el título de la ventana. El título se compone del nombre
     * de la aplicación, la URL de la web API y un mensaje que se recibe como argumento.
     *
     * @param mensaje Mensaje que se mostrará en el título de la ventana tras el nombre de la app y la URL de
     * la web API.
     */
    public static void setTitle(String mensaje) {
        stageInicial.setTitle(resourceBundle.getString("nombre_aplicacion") + " - URL: " + URL_BASE + " - " + mensaje);
    }

    /**
     * Método publico estático que devuelve la cadena de la URL base de la web API. Es llamada desde los otro
     * controladores secundarios como los artículo y las tiendas.
     *
     * @return Cadena con la URL base de la web API.
     */
    public static String getUrlBase() {
        return URL_BASE;
    }

    /**
     * Método que devuelve enlace al objeto Window de la aplicación.
     *
     * @return
     */
    public static Window getWindow() {
        return stageInicial.getScene().getWindow();
    }
}
