package com.sbarquero.comparadorprecios.auxiliar;

import com.sbarquero.comparadorprecios.modelo.PrecioTienda;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;

/**
 * Clase que hereda y personaliza la clase TableCell para mostrar fecha y hora en formato dd/MM/yyyy HH:mm:ss
 *
 * @author Santiago Barquero <sbarquero AT gmail.com>
 */
public class DateTimeTableCell extends TableCell<PrecioTienda, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText("");
        } else {
            // Formato fecha en BD Postgres
            DateTimeFormatter databaseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            LocalDateTime localDate = LocalDateTime.parse(item, databaseFormatter);
            // Formato a mostrar en la celda
            DateTimeFormatter cellFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            setText(localDate.format(cellFormatter));
        }
    }

}
