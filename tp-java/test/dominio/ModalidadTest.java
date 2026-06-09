package dominio;

import dominio.enums.Modalidad;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModalidadTest {

    @ParameterizedTest
    @EnumSource(Modalidad.class)
    void cadaModalidadDefineSuAjuste(Modalidad modalidad) {
        int ajusteEsperado = switch (modalidad) {
            case REGULAR -> 0;
            case CONDICIONAL -> 20;
            case OYENTE -> -1;
        };

        assertEquals(ajusteEsperado, modalidad.getAjuste());
    }
}
