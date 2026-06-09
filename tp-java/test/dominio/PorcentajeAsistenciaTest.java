package dominio;

import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PorcentajeAsistenciaTest {

    private final Alumno alumno = new Alumno(
            1, "Perez", "Ana", LocalDate.of(2000, 1, 1));
    private final Asignatura asignatura =
            new Obligatoria("OB1", "Obligatoria", 1, true);

    @Test
    void sinClasesDictadasDevuelveCero() {
        assertEquals(0, nuevaInscripcion().porcentajeAsistencia(0));
    }

    @Test
    void sinAsistenciasDevuelveCero() {
        assertEquals(0, nuevaInscripcion().porcentajeAsistencia(10));
    }

    @Test
    void calculaPorcentajeExacto() throws DatoInvalidoException {
        Inscripcion inscripcion = nuevaInscripcion();
        inscripcion.registrarAsistencia(clase("C1", 1));
        inscripcion.registrarAsistencia(clase("C2", 2));
        inscripcion.registrarAsistencia(clase("C3", 3));

        assertEquals(75, inscripcion.porcentajeAsistencia(4));
    }

    @Test
    void conservaPrecisionDecimal() throws DatoInvalidoException {
        Inscripcion inscripcion = nuevaInscripcion();
        inscripcion.registrarAsistencia(clase("C1", 1));

        assertEquals(100.0 / 3.0, inscripcion.porcentajeAsistencia(3), 0.0001);
    }

    private Inscripcion nuevaInscripcion() {
        return new Inscripcion(alumno, asignatura, Modalidad.REGULAR);
    }

    private Clase clase(String id, int dia) {
        return new Clase(id, LocalDateTime.of(2026, 3, dia, 8, 0), asignatura);
    }
}
