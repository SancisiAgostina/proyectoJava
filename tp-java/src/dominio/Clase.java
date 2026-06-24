package dominio;

import dominio.excepciones.DatoInvalidoException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa una clase de una asignatura.
 * Tiene un identificador, una fecha y hora y una asignatura.
 * Tiene un metodo toString para mostrar la informacion de la clase.
 * Tiene un metodo equals para comparar si dos clases son iguales.
 * Tiene un metodo hashCode para obtener el hash code de la clase.
 */

public class Clase implements Serializable {
    public static final long serialVersionUID = 1l;

    private final String id;
    private final LocalDateTime fechahora;
    private final Asignatura asignatura;

    public Clase(String id, LocalDateTime fechahora, Asignatura asignatura)
            throws DatoInvalidoException {
        if (id == null || id.isBlank()) {
            throw new DatoInvalidoException("El identificador de la clase es obligatorio.");
        }
        if (fechahora == null) {
            throw new DatoInvalidoException("La fecha y hora de la clase son obligatorias.");
        }
        if (asignatura == null) {
            throw new DatoInvalidoException("La asignatura de la clase es obligatoria.");
        }

        this.id = id;
        this.fechahora = fechahora;
        this.asignatura = asignatura;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getFechahora() {
        return fechahora;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    @Override
    public String toString() {
        return "Clase " + id + " - " + fechahora;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Clase s)
            return id.equals(s.id);

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}