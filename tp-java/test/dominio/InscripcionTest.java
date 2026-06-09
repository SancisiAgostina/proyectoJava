package dominio;

import dominio.enums.Condicion;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InscripcionTest {

    private Alumno alumno;

    @BeforeEach
    void prepararAlumno() throws DatoInvalidoException {
        alumno = new Alumno(1, "Perez", "Ana", LocalDate.of(2000, 1, 1));
    }

    @Test
    void obligatoriaRegularHabilitaCon60PeroPromocionaCon80() throws DatoInvalidoException {
        Asignatura asignatura = new Obligatoria("OB1", "Obligatoria", 1, true);

        assertEquals(Condicion.PUEDE_HABILITAR,
                inscripcionConAsistencias(asignatura, Modalidad.REGULAR, 3)
                        .calcularCondicion(5));
        assertEquals(Condicion.PUEDE_PROMOCIONAR,
                inscripcionConAsistencias(asignatura, Modalidad.REGULAR, 4)
                        .calcularCondicion(5));
    }

    @Test
    void obligatoriaCondicionalSuma20PuntosACadaUmbral() throws DatoInvalidoException {
        Asignatura asignatura = new Obligatoria("OB1", "Obligatoria", 1, true);

        assertEquals(Condicion.PUEDE_HABILITAR,
                inscripcionConAsistencias(asignatura, Modalidad.CONDICIONAL, 4)
                        .calcularCondicion(5));
        assertEquals(Condicion.PUEDE_PROMOCIONAR,
                inscripcionConAsistencias(asignatura, Modalidad.CONDICIONAL, 5)
                        .calcularCondicion(5));
    }

    @Test
    void optativaCondicionalHabilitaCon70YPromocionaCon80() throws DatoInvalidoException {
        Asignatura asignatura = new Optativa("OP1", "Optativa", 1, true);

        assertEquals(Condicion.PUEDE_HABILITAR,
                inscripcionConAsistencias(asignatura, Modalidad.CONDICIONAL, 7)
                        .calcularCondicion(10));
        assertEquals(Condicion.PUEDE_PROMOCIONAR,
                inscripcionConAsistencias(asignatura, Modalidad.CONDICIONAL, 8)
                        .calcularCondicion(10));
    }

    @Test
    void pasantiaNuncaPromociona() throws DatoInvalidoException {
        Asignatura asignatura = new Pasantia("PA1", "Pasantia", 9, false);

        assertEquals(Condicion.PUEDE_HABILITAR,
                inscripcionConAsistencias(asignatura, Modalidad.REGULAR, 4)
                        .calcularCondicion(4));
    }

    @Test
    void oyenteSiempreQuedaLibre() throws DatoInvalidoException {
        Asignatura asignatura = new Obligatoria("OB1", "Obligatoria", 1, true);

        assertEquals(Condicion.LIBRE,
                inscripcionConAsistencias(asignatura, Modalidad.OYENTE, 5)
                        .calcularCondicion(5));
    }

    private Inscripcion inscripcionConAsistencias(
            Asignatura asignatura, Modalidad modalidad, int asistencias)
            throws DatoInvalidoException {
        Inscripcion inscripcion = new Inscripcion(alumno, asignatura, modalidad);
        for (int i = 0; i < asistencias; i++) {
            inscripcion.registrarAsistencia(new Clase(
                    "C" + i, LocalDateTime.of(2026, 3, i + 1, 8, 0), asignatura));
        }
        return inscripcion;
    }
}
