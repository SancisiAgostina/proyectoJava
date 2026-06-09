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
    void prepararUniversidad() throws DatoInvalidoException {
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
        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarClase(new Clase(
                        "C3", LocalDateTime.of(2026, 3, 3, 8, 0), null)));
    }

    @Test
    void rechazaAlumnoNoInscriptoEnAsignaturaDeLaClase() throws DatoInvalidoException {
        Asignatura optativa = new Optativa("OP1", "Optativa", 1, true);
        universidad.agregarAsignatura(optativa);
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
    void inscripcionRechazaClaseDeOtraAsignatura() throws DatoInvalidoException {
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

    @Test
    void coleccionesDeUniversidadSonDeSoloLectura() {
        assertThrows(UnsupportedOperationException.class,
                () -> universidad.getAlumnos().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> universidad.getAsignaturas().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> universidad.getClases().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> universidad.getInscripciones().clear());
    }

    @Test
    void rechazaDatosDuplicados() {
        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarAlumno(new Alumno(
                        1001, "Otro", "Nombre", LocalDate.of(2001, 1, 1))));
        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarAsignatura(
                        new Obligatoria("OB1", "Otra", 2, false)));
        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarClase(new Clase(
                        "C1", LocalDateTime.of(2026, 4, 1, 8, 0), obligatoria)));
        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarInscripcion(
                        new Inscripcion(alumno, obligatoria, Modalidad.CONDICIONAL)));
    }

    @Test
    void rechazaInscripcionConEntidadesNoRegistradas() throws DatoInvalidoException {
        Alumno otroAlumno = new Alumno(
                2000, "Otro", "Alumno", LocalDate.of(2001, 1, 1));
        Asignatura otraAsignatura = new Optativa("OP2", "Otra", 2, true);

        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarInscripcion(
                        new Inscripcion(otroAlumno, obligatoria, Modalidad.REGULAR)));
        assertThrows(DatoInvalidoException.class,
                () -> universidad.agregarInscripcion(
                        new Inscripcion(alumno, otraAsignatura, Modalidad.REGULAR)));
    }

    @Test
    void conservaAlumnosConMismoApellidoYNombrePeroDistintaMatricula()
            throws DatoInvalidoException {
        universidad.agregarAlumno(new Alumno(
                1002, "Perez", "Ana", LocalDate.of(2001, 1, 1)));

        assertEquals(2, universidad.getAlumnos().size());
    }
}
