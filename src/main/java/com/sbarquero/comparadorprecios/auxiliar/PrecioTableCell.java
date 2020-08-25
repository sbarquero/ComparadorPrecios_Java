package com.sbarquero.comparadorprecios.auxiliar;

import com.sbarquero.comparadorprecios.modelo.PrecioTienda;
import javafx.scene.control.TableCell;

/**
 * Clase que hereda y personaliza la clase TableCell para mostrar el precio de un artículo con el símbolo €
 *
 * @author Santiago Barquero <sbarquero AT gmail.com>
 */
public class PrecioTableCell extends TableCell<PrecioTienda, Number> {

    @Override
    protected void updateItem(Number item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText("");
        } else {
            setText(String.format("%.2f €", item));
        }
    }

}
