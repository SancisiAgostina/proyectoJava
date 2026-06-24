package dominio;

import dominio.excepciones.DatoInvalidoException;
import java.io.Serializable;
import java.util.Objects;

/**
 * Es una clase abstracta que representa una asignatura.
 * Una asignatura tiene un código, un nombre, un cuatrimestre y un indicador de
 * si es promocional.
 * Se hacen las verificacion pertitnentes para que los datos sean validos.
 * Tiene un metodo equals para comparar si dos asignaturas son iguales.
 */

public abstract class Asignatura implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String codigo;
    private final String nombre;
    private final int cuatrimestre;
    private final boolean promocional;

    protected Asignatura(String codigo, String nombre, int cuatrimestre, boolean promocional)
            throws DatoInvalidoException {
        if (codigo == null || codigo.isBlank()) {
            throw new DatoInvalidoException("El código de la asignatura es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre de la asignatura es obligatorio.");
        }
        if (cuatrimestre < 1 || cuatrimestre > 10) {
            throw new DatoInvalidoException(
                    "El cuatrimestre debe estar comprendido entre 1 y 10.");
        }

        this.codigo = codigo;
        this.nombre = nombre;
        this.cuatrimestre = cuatrimestre;
        this.promocional = promocional;
    }

    // devuelve el porcentaje mínimo de asistencia que un alumno regular necesita
    // para quedar en condiciones de habilitar esa asignatura (o sea, poder rendir
    // el final).
    public abstract double porcentajeHabilitarRegular();

    public abstract boolean permitePromocion();

    public double porcentajePromocionarRegular() {
        throw new IllegalStateException(
                "La categoría de asignatura no permite promoción.");
    }

    // devuelve el porcentaje mínimo de asistencia que un alumno regular necesita
    // para quedar en condiciones de promocionar (aprobar sin final).

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCuatrimestre() {
        return cuatrimestre;
    }

    public boolean esPromocional() {
        return promocional;
    }

    @Override
    public String toString() {
        return codigo + " -" + nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj instanceof Asignatura asignatura) {
            return Objects.equals(codigo, asignatura.codigo);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}