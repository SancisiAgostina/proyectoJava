package servicio;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.Obligatoria;
import dominio.Optativa;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniversidadTest {

    private Universidad universidad;
    private Alumno alumno;
    private Asignatura obligatoria;
    private Clase claseObligatoria;
    private Inscripcion inscripcion;

    @BeforeEach
    void prepararUniversidad() {
        universidad = new Universidad();
        alumno = new Alumno(1001, "Perez", "Ana", LocalDate.of(2000, 1, 1));
        obligatoria = new Obligatoria("OB1", "Obligatoria", 1, true);
        claseObligatoria = new Clase(
                "C1", LocalDateTime.of(2026, 3, 1, 8, 0), obligatoria);
        inscripcion = new Inscripcion(alumno, obligatoria, Modalidad.REGULAR);

        universidad.agregarAlumno(alumno);
        universidad.agregarAsignatura(obligatoria);
        universidad.agregarClase(claseObligatoria);
        universidad.agregarInscripcion(inscripcion);
    }

    @Test
    void registraAsistenciaCuandoAlumnoEstaInscripto() throws DatoInvalidoException {
        universidad.registrarAsistencia(1001, "C1");

        assertTrue(inscripcion.getClasesAsistidas().contains(claseObligatoria));
        assertEquals(1, inscripcion.getClasesAsistidas().size());
    }

    @Test
    void rechazaAlumnoInexistente() {
        assertThrows(DatoInvalidoException.class,
                () -> universidad.registrarAsistencia(9999, "C1"));
    }

    @Test
    void rechazaClaseInexistente() {
        assertThrows(DatoInvalidoException.class,
                () -> universidad.registrarAsistencia(1001, "NO_EXISTE"));
    }

    @Test
    void rechazaIdentificadorDeClaseVacio() {
        assertThrows(DatoInvalidoException.class,
                () -> universidad.registrarAsistencia(1001, " "));
    }

    @Test
    void rechazaClaseSinAsignaturaAsociada() {
        universidad.agregarClase(new Clase(
                "C3", LocalDateTime.of(2026, 3, 3, 8, 0), null));

        assertThrows(DatoInvalidoException.class,
                () -> universidad.registrarAsistencia(1001, "C3"));
    }

    @Test
    void rechazaAlumnoNoInscriptoEnAsignaturaDeLaClase() {
        Asignatura optativa = new Optativa("OP1", "Optativa", 1, true);
        universidad.agregarClase(new Clase(
                "C2", LocalDateTime.of(2026, 3, 2, 8, 0), optativa));

        assertThrows(DatoInvalidoException.class,
                () -> universidad.registrarAsistencia(1001, "C2"));
    }

    @Test
    void rechazaAsistenciaDuplicada() throws DatoInvalidoException {
        universidad.registrarAsistencia(1001, "C1");

        assertThrows(DatoInvalidoException.class,
                () -> universidad.registrarAsistencia(1001, "C1"));
        assertEquals(1, inscripcion.getClasesAsistidas().size());
    }

    @Test
    void inscripcionRechazaClaseDeOtraAsignatura() {
        Asignatura optativa = new Optativa("OP1", "Optativa", 1, true);
        Clase claseOptativa = new Clase(
                "C2", LocalDateTime.of(2026, 3, 2, 8, 0), optativa);

        assertThrows(DatoInvalidoException.class,
                () -> inscripcion.registrarAsistencia(claseOptativa));
    }

    @Test
    void noPermiteModificarAsistenciasDesdeElGetter() {
        assertThrows(UnsupportedOperationException.class,
                () -> inscripcion.getClasesAsistidas().add(claseObligatoria));
    }
}
