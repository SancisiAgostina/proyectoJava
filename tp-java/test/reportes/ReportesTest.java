package reportes;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.Obligatoria;
import dominio.Optativa;
import dominio.enums.Condicion;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import servicio.Universidad;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportesTest {

    private Universidad universidad;
    private Asignatura obligatoria;
    private Asignatura optativa;

    @BeforeEach
    void prepararDatos() throws DatoInvalidoException {
        universidad = new Universidad();
        obligatoria = new Obligatoria("OB1", "Obligatoria", 1, true);
        optativa = new Optativa("OP1", "Optativa", 3, true);

        Alumno ana = alumno(1001, "Alvarez", "Ana");
        Alumno beto = alumno(1002, "Benitez", "Beto");
        Alumno carla = alumno(1003, "Costa", "Carla");

        universidad.agregarAlumno(ana);
        universidad.agregarAlumno(beto);
        universidad.agregarAlumno(carla);
        universidad.agregarAsignatura(obligatoria);
        universidad.agregarAsignatura(optativa);

        universidad.agregarClase(clase("OB-C1", 1, obligatoria));
        universidad.agregarClase(clase("OB-C2", 2, obligatoria));
        universidad.agregarClase(clase("OP-C1", 3, optativa));
        universidad.agregarClase(clase("OP-C2", 4, optativa));

        universidad.agregarInscripcion(new Inscripcion(ana, obligatoria, Modalidad.REGULAR));
        universidad.agregarInscripcion(new Inscripcion(beto, obligatoria, Modalidad.REGULAR));
        universidad.agregarInscripcion(new Inscripcion(carla, optativa, Modalidad.REGULAR));

        universidad.registrarAsistencia(1001, "OB-C1");
        universidad.registrarAsistencia(1001, "OB-C2");
        universidad.registrarAsistencia(1002, "OB-C1");
    }

    @Test
    void rankingOrdenaAsignaturasPorPresentismoDescendente() {
        List<ReportePresentismo.ItemPresentismo> ranking =
                new ReportePresentismo(universidad).generar();

        assertEquals("OB1", ranking.get(0).asignatura().getCodigo());
        assertEquals(75, ranking.get(0).porcentaje());
        assertEquals(3, ranking.get(0).asistenciasRegistradas());
        assertEquals(4, ranking.get(0).asistenciasPosibles());
        assertEquals("OP1", ranking.get(1).asignatura().getCodigo());
        assertEquals(0, ranking.get(1).porcentaje());
    }

    @Test
    void detalleAsignaturaIncluyeAsistenciasPorClaseYCondicion()
            throws DatoInvalidoException {
        ReporteDatosAsignatura.DatosAsignatura datos =
                new ReporteDatosAsignatura(universidad).generar("OB1");

        assertEquals(2, datos.clases().size());
        assertEquals(2, datos.alumnos().size());

        ReporteDatosAsignatura.DetalleAlumno ana = datos.alumnos().get(0);
        assertEquals("Alvarez", ana.alumno().getApellido());
        assertEquals(2, ana.cantidadPresentes());
        assertEquals(100, ana.porcentajeAsistencia());
        assertEquals(Condicion.PUEDE_PROMOCIONAR, ana.condicion());
        assertTrue(ana.clases().stream().allMatch(ReporteDatosAsignatura.DetalleClase::presente));

        ReporteDatosAsignatura.DetalleAlumno beto = datos.alumnos().get(1);
        assertEquals(1, beto.cantidadPresentes());
        assertEquals(50, beto.porcentajeAsistencia());
        assertEquals(Condicion.LIBRE, beto.condicion());
        assertTrue(beto.clases().get(0).presente());
        assertFalse(beto.clases().get(1).presente());
    }

    @Test
    void detalleAsignaturaRechazaCodigoInexistente() {
        assertThrows(DatoInvalidoException.class,
                () -> new ReporteDatosAsignatura(universidad).generar("NO_EXISTE"));
    }

    @Test
    void alumnosLibresPuedeListarTodosOFiltrarPorAnio() throws DatoInvalidoException {
        ReporteAlumnosLibres reporte = new ReporteAlumnosLibres(universidad);

        assertEquals(2, reporte.generarTodos().size());
        assertEquals("OB1", reporte.generarPorAnio(1).getFirst().asignatura().getCodigo());
        assertEquals("OP1", reporte.generarPorAnio(2).getFirst().asignatura().getCodigo());
        assertTrue(reporte.generarPorAnio(3).isEmpty());
    }

    @Test
    void alumnosLibresRechazaAnioFueraDeRango() {
        assertThrows(DatoInvalidoException.class,
                () -> new ReporteAlumnosLibres(universidad).generarPorAnio(6));
    }

    @Test
    void exportaLosTresReportesComoTexto(@TempDir Path directorio)
            throws IOException, DatoInvalidoException {
        Path ranking = directorio.resolve("ranking.txt");
        Path detalle = directorio.resolve("detalle.txt");
        Path libres = directorio.resolve("libres/anio-1.txt");

        new ReportePresentismo(universidad).exportar(ranking);
        new ReporteDatosAsignatura(universidad).exportar("OB1", detalle);
        new ReporteAlumnosLibres(universidad).exportarPorAnio(1, libres);

        assertTrue(Files.readString(ranking).contains("75.00%"));
        assertTrue(Files.readString(detalle).contains("PRESENTE"));
        assertTrue(Files.readString(detalle).contains("AUSENTE"));
        assertTrue(Files.readString(libres).contains("Benitez-Beto"));
    }

    private Alumno alumno(int matricula, String apellido, String nombre) {
        return new Alumno(matricula, apellido, nombre, LocalDate.of(2000, 1, 1));
    }

    private Clase clase(String id, int dia, Asignatura asignatura) {
        return new Clase(id, LocalDateTime.of(2026, 3, dia, 8, 0), asignatura);
    }
}
