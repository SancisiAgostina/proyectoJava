package dominio;

import dominio.enums.Modalidad;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModalidadTest {

    @ParameterizedTest
    @EnumSource(Modalidad.class)
    void cadaModalidadDefineSuAjuste(Modalidad modalidad) {
        int ajusteEsperado = switch (modalidad) {
            case REGULAR -> 0;
            case CONDICIONAL -> 20;
            case OYENTE -> 0;
        };

        assertEquals(ajusteEsperado, modalidad.getAjuste());
    }

    @Test
    void regularYCondicionalPermitenAcreditarPeroOyenteNo() {
        assertTrue(Modalidad.REGULAR.permiteAcreditacion());
        assertTrue(Modalidad.CONDICIONAL.permiteAcreditacion());
        assertFalse(Modalidad.OYENTE.permiteAcreditacion());
    }
}
