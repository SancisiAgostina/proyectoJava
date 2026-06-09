package dominio;

import dominio.enums.Condicion;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CondicionPorCategoriaTest {

    private static final Alumno ALUMNO = new Alumno(
            1, "Perez", "Ana", LocalDate.of(2000, 1, 1));

    @ParameterizedTest(name = "{0}: {2}% => {3}")
    @MethodSource("casosRegulares")
    void calculaCondicionDeAlumnosRegulares(
            String caso, Asignatura asignatura, int asistencias, Condicion esperada)
            throws DatoInvalidoException {
        assertEquals(esperada,
                inscripcion(asignatura, Modalidad.REGULAR, asistencias)
                        .calcularCondicion(100));
    }

    @ParameterizedTest(name = "{0}: {2}% => {3}")
    @MethodSource("casosCondicionales")
    void calculaCondicionDeAlumnosCondicionales(
            String caso, Asignatura asignatura, int asistencias, Condicion esperada)
            throws DatoInvalidoException {
        assertEquals(esperada,
                inscripcion(asignatura, Modalidad.CONDICIONAL, asistencias)
                        .calcularCondicion(100));
    }

    @ParameterizedTest(name = "Oyente en {0}")
    @MethodSource("categorias")
    void oyenteSiempreQuedaLibre(String nombre, Asignatura asignatura)
            throws DatoInvalidoException {
        assertEquals(Condicion.LIBRE,
                inscripcion(asignatura, Modalidad.OYENTE, 100)
                        .calcularCondicion(100));
    }

    private static Stream<Arguments> casosRegulares() {
        return Stream.of(
                caso("Obligatoria debajo de habilitar", obligatoria(true), 59, Condicion.LIBRE),
                caso("Obligatoria habilita", obligatoria(true), 60, Condicion.PUEDE_HABILITAR),
                caso("Obligatoria debajo de promocionar", obligatoria(true), 79, Condicion.PUEDE_HABILITAR),
                caso("Obligatoria promociona", obligatoria(true), 80, Condicion.PUEDE_PROMOCIONAR),
                caso("Obligatoria no promocional", obligatoria(false), 100, Condicion.PUEDE_HABILITAR),
                caso("Optativa debajo de habilitar", optativa(true), 49, Condicion.LIBRE),
                caso("Optativa habilita", optativa(true), 50, Condicion.PUEDE_HABILITAR),
                caso("Optativa debajo de promocionar", optativa(true), 59, Condicion.PUEDE_HABILITAR),
                caso("Optativa promociona", optativa(true), 60, Condicion.PUEDE_PROMOCIONAR),
                caso("Pasantia debajo de habilitar", pasantia(), 74, Condicion.LIBRE),
                caso("Pasantia habilita", pasantia(), 75, Condicion.PUEDE_HABILITAR),
                caso("Trabajo final debajo de habilitar", trabajoFinal(), 74, Condicion.LIBRE),
                caso("Trabajo final habilita", trabajoFinal(), 75, Condicion.PUEDE_HABILITAR)
        );
    }

    private static Stream<Arguments> casosCondicionales() {
        return Stream.of(
                caso("Obligatoria debajo de habilitar", obligatoria(true), 79, Condicion.LIBRE),
                caso("Obligatoria habilita", obligatoria(true), 80, Condicion.PUEDE_HABILITAR),
                caso("Obligatoria debajo de promocionar", obligatoria(true), 99, Condicion.PUEDE_HABILITAR),
                caso("Obligatoria promociona", obligatoria(true), 100, Condicion.PUEDE_PROMOCIONAR),
                caso("Optativa debajo de habilitar", optativa(true), 69, Condicion.LIBRE),
                caso("Optativa habilita", optativa(true), 70, Condicion.PUEDE_HABILITAR),
                caso("Optativa debajo de promocionar", optativa(true), 79, Condicion.PUEDE_HABILITAR),
                caso("Optativa promociona", optativa(true), 80, Condicion.PUEDE_PROMOCIONAR),
                caso("Pasantia debajo de habilitar", pasantia(), 94, Condicion.LIBRE),
                caso("Pasantia habilita", pasantia(), 95, Condicion.PUEDE_HABILITAR),
                caso("Trabajo final debajo de habilitar", trabajoFinal(), 94, Condicion.LIBRE),
                caso("Trabajo final habilita", trabajoFinal(), 95, Condicion.PUEDE_HABILITAR)
        );
    }

    private static Stream<Arguments> categorias() {
        return Stream.of(
                Arguments.of("Obligatoria", obligatoria(true)),
                Arguments.of("Optativa", optativa(true)),
                Arguments.of("Pasantia", pasantia()),
                Arguments.of("Trabajo final", trabajoFinal())
        );
    }

    private static Arguments caso(
            String nombre, Asignatura asignatura, int asistencias, Condicion esperada) {
        return Arguments.of(nombre, asignatura, asistencias, esperada);
    }

    private static Asignatura obligatoria(boolean promocional) {
        return new Obligatoria("OB1", "Obligatoria", 1, promocional);
    }

    private static Asignatura optativa(boolean promocional) {
        return new Optativa("OP1", "Optativa", 1, promocional);
    }

    private static Asignatura pasantia() {
        return new Pasantia("PA1", "Pasantia", 9, false);
    }

    private static Asignatura trabajoFinal() {
        return new TrabajoFinal("TF1", "Trabajo final", 10, false);
    }

    private static Inscripcion inscripcion(
            Asignatura asignatura, Modalidad modalidad, int asistencias)
            throws DatoInvalidoException {
        Inscripcion inscripcion = new Inscripcion(ALUMNO, asignatura, modalidad);
        LocalDateTime inicio = LocalDateTime.of(2026, 3, 1, 8, 0);

        for (int i = 0; i < asistencias; i++) {
            inscripcion.registrarAsistencia(
                    new Clase("C" + i, inicio.plusDays(i), asignatura));
        }
        return inscripcion;
    }
}
