package dominio;

import dominio.excepciones.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsignaturaTest {

    @ParameterizedTest(name = "{0}: habilitar {2}%")
    @MethodSource("categoriasYPorcentajeHabilitar")
    void cadaCategoriaDefineSuPorcentajeParaHabilitar(
            String nombre, Asignatura asignatura, double porcentajeHabilitar) {
        assertEquals(porcentajeHabilitar, asignatura.porcentajeHabilitarRegular());
    }

    @Test
    void obligatoriaYOptativaPermitenPromocion() throws DatoInvalidoException {
        Asignatura obligatoria = new Obligatoria("OB1", "Obligatoria", 1, true);
        Asignatura optativa = new Optativa("OP1", "Optativa", 1, true);

        assertTrue(obligatoria.permitePromocion());
        assertEquals(Obligatoria.PORCENTAJE_PROMOCIONAR_REGULAR,
                obligatoria.porcentajePromocionarRegular());
        assertTrue(optativa.permitePromocion());
        assertEquals(Optativa.PORCENTAJE_PROMOCIONAR_REGULAR,
                optativa.porcentajePromocionarRegular());
    }

    @Test
    void pasantiaYTrabajoFinalNoPermitenPromocion() throws DatoInvalidoException {
        Asignatura pasantia = new Pasantia("PA1", "Pasantia", 9, false);
        Asignatura trabajoFinal = new TrabajoFinal("TF1", "Trabajo final", 10, false);

        assertFalse(pasantia.permitePromocion());
        assertFalse(trabajoFinal.permitePromocion());
        assertThrows(IllegalStateException.class, pasantia::porcentajePromocionarRegular);
        assertThrows(IllegalStateException.class, trabajoFinal::porcentajePromocionarRegular);
    }

    @Test
    void porcentajesSonConstantesCompartidasPorCategoria() {
        assertEquals(60, Obligatoria.PORCENTAJE_HABILITAR_REGULAR);
        assertEquals(80, Obligatoria.PORCENTAJE_PROMOCIONAR_REGULAR);
        assertEquals(50, Optativa.PORCENTAJE_HABILITAR_REGULAR);
        assertEquals(60, Optativa.PORCENTAJE_PROMOCIONAR_REGULAR);
        assertEquals(75, Pasantia.PORCENTAJE_HABILITAR_REGULAR);
        assertEquals(75, TrabajoFinal.PORCENTAJE_HABILITAR_REGULAR);
    }

    private static Stream<Arguments> categoriasYPorcentajeHabilitar()
            throws DatoInvalidoException {
        return Stream.of(
                Arguments.of("Obligatoria",
                        new Obligatoria("OB1", "Obligatoria", 1, true),
                        Obligatoria.PORCENTAJE_HABILITAR_REGULAR),
                Arguments.of("Optativa",
                        new Optativa("OP1", "Optativa", 1, true),
                        Optativa.PORCENTAJE_HABILITAR_REGULAR),
                Arguments.of("Pasantia",
                        new Pasantia("PA1", "Pasantia", 9, false),
                        Pasantia.PORCENTAJE_HABILITAR_REGULAR),
                Arguments.of("Trabajo final",
                        new TrabajoFinal("TF1", "Trabajo final", 10, false),
                        TrabajoFinal.PORCENTAJE_HABILITAR_REGULAR)
        );
    }
}
