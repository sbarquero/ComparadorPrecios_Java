package com.sbarquero.comparadorprecios.auxiliar;

import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * Clase Util. Contiene métodos estáticos para mostrar distintos tipos de ventanas de diálogo.
 * 
 * https://code.makery.ch/blog/javafx-dialogs-official/
 * https://stackoverflow.com/questions/33838293/how-to-set-alert-box-position-over-current-primarystage-javafx
 *
 * @author Santiago Barquero López 2ºDAM <sbarquero@gmail.com>
 */
public class Util {

    /**
     * Función que utilizo para mostrar ventana de dialog de la clase Alert con una pregunta y que devuelve
     * true o false según se elija "Sí" o "No".
     *
     * @param titulo Título de la ventana
     * @param mensaje Pregunta que mostrará en la ventana
     * @return True si elige el botón "Sí" y False en caso de elegir "No"
     */
    public static boolean confirmar(Window ownerWindow, String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        alert.initOwner(ownerWindow);
        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.YES;
    }

    /**
     * Método estático que utilizo para mostrar una ventana con un mensaje de información.
     *
     * @param titulo Título de la ventana
     * @param mensaje Mensaje que mostrará en la ventana
     */
    public static void mensajeInformacion(Window ownerWindow, String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.initOwner(ownerWindow);
        alert.showAndWait();
    }

    /**
     * Método estático que utilizo para mostrar una ventana con un mensaje de error.
     *
     * @param titulo Título de la ventana
     * @param mensaje Mensaje que mostrará en la ventana
     */
    public static void mensajeError(Window ownerWindow, String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.initOwner(ownerWindow);
        alert.showAndWait();
    }

    /**
     * Muestra ventana cargando, para indicar un tiempo de espera en la carga de los datos.
     * @param ownerWindow Ventana padre
     * @param rb recursos para la internacionalización
     * @return 
     */
    public static Alert muestraCargando(Window ownerWindow, ResourceBundle rb) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setHeaderText(null);
        alert.setTitle(rb.getString("nombre_aplicacion"));
        alert.setContentText(rb.getString("lbl.cargando"));
        alert.initOwner(ownerWindow);
        alert.show();
        return alert;
    }
    
}
