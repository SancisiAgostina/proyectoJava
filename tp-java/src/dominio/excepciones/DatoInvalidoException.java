package dominio.excepciones;

/**
 * Clase que representa una excepción que se lanza cuando un dato es inválido.
 */

public class DatoInvalidoException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor que recibe un mensaje de error.
     * 
     * @param mensaje El mensaje de error.
     */
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}