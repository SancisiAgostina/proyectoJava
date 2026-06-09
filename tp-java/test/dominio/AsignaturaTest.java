package dominio;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsignaturaTest {

    @ParameterizedTest(name = "{0}: habilitar {2}%, promocionar {3}%")
    @MethodSource("categoriasYPorcentajes")
    void cadaCategoriaDefineSusPorcentajes(
            String nombre, Asignatura asignatura,
            double porcentajeHabilitar, double porcentajePromocionar) {
        assertEquals(porcentajeHabilitar, asignatura.porcentajeHabilitarRegular());
        assertEquals(porcentajePromocionar, asignatura.porcentajePromocionarRegular());
    }

    private static Stream<Arguments> categoriasYPorcentajes() {
        return Stream.of(
                Arguments.of("Obligatoria",
                        new Obligatoria("OB1", "Obligatoria", 1, true), 60, 80),
                Arguments.of("Optativa",
                        new Optativa("OP1", "Optativa", 1, true), 50, 60),
                Arguments.of("Pasantia",
                        new Pasantia("PA1", "Pasantia", 9, false), 75, -1),
                Arguments.of("Trabajo final",
                        new TrabajoFinal("TF1", "Trabajo final", 10, false), 75, -1)
        );
    }
}
