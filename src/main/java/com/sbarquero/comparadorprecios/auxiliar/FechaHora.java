package com.sbarquero.comparadorprecios.auxiliar;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase con métodos estáticos para la conversión de formatos de fecha entre el formato de base de datos y el
 * formato de visualización
 *
 * @author Santiago Barquero <sbarquero AT gmail.com>
 */
public class FechaHora {

    /**
     * Método que recibe la fecha y hora de la vista que es normalmente la fecha y hora legible y devuelve la
     * fecha codificada en formato para la base de datos.
     *
     * @param fechaVista Fecha y hora en formato legible para la vista.
     * @return Fecha y hora en formato base de datos.
     */
    public static String codifica(String fechaVista) {
        DateTimeFormatter formatterVista = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDate = LocalDateTime.parse(fechaVista, formatterVista);
        DateTimeFormatter formatterBD = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String fechaBD = localDate.format(formatterBD);
        return fechaBD;
    }

    /**
     * Método que recibe la fecha y hora de la base de datos y devuelve la fecha y hora legible para la vista.
     *
     * @param fechaBD Fecha y hora en formato base de datos.
     * @return Fecha y hora en formato legible para la vista.
     */
    public static String decodifica(String fechaBD) {
        DateTimeFormatter formatterBD = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime localDate = LocalDateTime.parse(fechaBD, formatterBD);
        DateTimeFormatter formatterVista = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String fechaVista = localDate.format(formatterVista);
        return fechaVista;
    }
    
    /**
     * Método que devuelve una cadena con la fecha y hora actual en formato para la BD.
     * @return Cadena con la fecha y hora actual en formato para la BD.
     */
    public static String actualBD() {
        DateTimeFormatter formatterBD = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        String fechaActualFormatoBD = fechaHoraActual.format(formatterBD);
        return fechaActualFormatoBD;
    }
    
    /**
     * Método que devuelve una cadena con la fecha y hora actual en formato legible para la vista.
     * @return Cadena con la fecha y hora actual en formato legible para la vista.
     */
    public static String actual() {
        DateTimeFormatter formatterVista = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime fechaHoraActual = LocalDateTime.now();
        String fechaActualFormatoVista = fechaHoraActual.format(formatterVista);
        return fechaActualFormatoVista;
    }

}
