package dominio;

import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidacionesConstructoresTest {

    private Alumno alumno;
    private Asignatura asignatura;

    @BeforeEach
    void prepararDatosValidos() throws DatoInvalidoException {
        alumno = new Alumno(1, "Perez", "Ana", LocalDate.of(2000, 1, 1));
        asignatura = new Obligatoria("OB1", "Obligatoria", 1, true);
    }

    @Test
    void alumnoRechazaDatosInvalidos() {
        assertThrows(DatoInvalidoException.class,
                () -> new Alumno(0, "Perez", "Ana", LocalDate.of(2000, 1, 1)));
        assertThrows(DatoInvalidoException.class,
                () -> new Alumno(1, " ", "Ana", LocalDate.of(2000, 1, 1)));
        assertThrows(DatoInvalidoException.class,
                () -> new Alumno(1, "Perez", null, LocalDate.of(2000, 1, 1)));
        assertThrows(DatoInvalidoException.class,
                () -> new Alumno(1, "Perez", "Ana", null));
        assertThrows(DatoInvalidoException.class,
                () -> new Alumno(1, "Perez", "Ana", LocalDate.now().plusDays(1)));
    }

    @Test
    void asignaturaRechazaDatosInvalidos() {
        assertThrows(DatoInvalidoException.class,
                () -> new Obligatoria(" ", "Obligatoria", 1, true));
        assertThrows(DatoInvalidoException.class,
                () -> new Obligatoria("OB1", null, 1, true));
        assertThrows(DatoInvalidoException.class,
                () -> new Obligatoria("OB1", "Obligatoria", 0, true));
        assertThrows(DatoInvalidoException.class,
                () -> new Obligatoria("OB1", "Obligatoria", 11, true));
    }

    @Test
    void categoriasSinPromocionRechazanConfiguracionPromocional() {
        assertThrows(DatoInvalidoException.class,
                () -> new Pasantia("PA1", "Pasantia", 9, true));
        assertThrows(DatoInvalidoException.class,
                () -> new TrabajoFinal("TF1", "Trabajo final", 10, true));
    }

    @Test
    void claseRechazaDatosInvalidos() {
        LocalDateTime fechaHora = LocalDateTime.of(2026, 3, 1, 8, 0);

        assertThrows(DatoInvalidoException.class,
                () -> new Clase(" ", fechaHora, asignatura));
        assertThrows(DatoInvalidoException.class,
                () -> new Clase("C1", null, asignatura));
        assertThrows(DatoInvalidoException.class,
                () -> new Clase("C1", fechaHora, null));
    }

    @Test
    void inscripcionRechazaDatosInvalidos() {
        assertThrows(DatoInvalidoException.class,
                () -> new Inscripcion(null, asignatura, Modalidad.REGULAR));
        assertThrows(DatoInvalidoException.class,
                () -> new Inscripcion(alumno, null, Modalidad.REGULAR));
        assertThrows(DatoInvalidoException.class,
                () -> new Inscripcion(alumno, asignatura, null));
    }

    @Test
    void permiteConstruirObjetosConDatosValidos() {
        assertDoesNotThrow(() -> new Alumno(
                2, "Gomez", "Luis", LocalDate.of(2001, 2, 3)));
        assertDoesNotThrow(() -> new Optativa("OP1", "Optativa", 2, false));
        assertDoesNotThrow(() -> new Pasantia("PA1", "Pasantia", 9, false));
        assertDoesNotThrow(() -> new TrabajoFinal("TF1", "Trabajo final", 10, false));
        assertDoesNotThrow(() -> new Clase(
                "C1", LocalDateTime.of(2026, 3, 1, 8, 0), asignatura));
        assertDoesNotThrow(() -> new Inscripcion(alumno, asignatura, Modalidad.REGULAR));
    }
}
