package reportes;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import servicio.Universidad;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import static dominio.enums.Condicion.LIBRE;

public class ReporteAlumnosLibres {

    private final Universidad universidad;

    public ReporteAlumnosLibres(Universidad universidad) {
        this.universidad = universidad;
    }

    public List<AlumnoLibre> generarTodos() {
        return generar(inscripcion -> true);
    }

    public List<AlumnoLibre> generarPorAnio(int anio) throws DatoInvalidoException {
        if (anio < 1 || anio > 5) {
            throw new DatoInvalidoException("El año de carrera debe estar entre 1 y 5.");
        }
        return generar(inscripcion -> anioDe(inscripcion.getAsignatura()) == anio);
    }

    public String generarTextoTodos() {
        return generarTexto("ALUMNOS LIBRES - TODAS LAS ASIGNATURAS", generarTodos());
    }

    public String generarTextoPorAnio(int anio) throws DatoInvalidoException {
        return generarTexto("ALUMNOS LIBRES - AÑO " + anio, generarPorAnio(anio));
    }

    public void mostrarTodos() {
        System.out.print(generarTextoTodos());
    }

    public void mostrarPorAnio(int anio) throws DatoInvalidoException {
        System.out.print(generarTextoPorAnio(anio));
    }

    public void exportarTodos(Path ruta) throws IOException {
        ExportadorTexto.exportar(generarTextoTodos(), ruta);
    }

    public void exportarPorAnio(int anio, Path ruta)
            throws IOException, DatoInvalidoException {
        ExportadorTexto.exportar(generarTextoPorAnio(anio), ruta);
    }

    private List<AlumnoLibre> generar(Predicate<Inscripcion> filtro) {
        return universidad.getInscripciones().stream()
                .filter(inscripcion -> inscripcion.getAsignatura() != null)
                .filter(filtro)
                .map(this::crearDetalle)
                .filter(detalle -> detalle != null)
                .sorted(Comparator
                        .comparing((AlumnoLibre detalle) -> detalle.asignatura().getCodigo())
                        .thenComparing(AlumnoLibre::alumno))
                .toList();
    }

    private AlumnoLibre crearDetalle(Inscripcion inscripcion) {
        List<Clase> clases = universidad.getClases().stream()
                .filter(clase -> clase.getAsignatura() != null)
                .filter(clase -> clase.getAsignatura().getCodigo()
                        .equals(inscripcion.getAsignatura().getCodigo()))
                .toList();
        int cantidadPresentes = (int) inscripcion.getClasesAsistidas().stream()
                .filter(clases::contains)
                .count();

        if (inscripcion.calcularCondicion(cantidadPresentes, clases.size()) != LIBRE) {
            return null;
        }

        return new AlumnoLibre(
                inscripcion.getAlumno(),
                inscripcion.getAsignatura(),
                inscripcion.getModalidad(),
                cantidadPresentes,
                clases.size(),
                clases.isEmpty() ? 0 : cantidadPresentes * 100.0 / clases.size());
    }

    private int anioDe(Asignatura asignatura) {
        return (asignatura.getCuatrimestre() + 1) / 2;
    }

    private String generarTexto(String titulo, List<AlumnoLibre> alumnos) {
        StringBuilder texto = new StringBuilder(titulo).append("\n");
        for (AlumnoLibre detalle : alumnos) {
            texto.append(String.format(Locale.ROOT,
                    "%s - %s - %s: %d/%d asistencias (%.2f%%), modalidad %s%n",
                    detalle.asignatura().getCodigo(),
                    detalle.asignatura().getNombre(),
                    detalle.alumno(),
                    detalle.cantidadPresentes(),
                    detalle.cantidadClases(),
                    detalle.porcentajeAsistencia(),
                    detalle.modalidad()));
        }
        return texto.toString();
    }

    public record AlumnoLibre(
            Alumno alumno,
            Asignatura asignatura,
            Modalidad modalidad,
            int cantidadPresentes,
            int cantidadClases,
            double porcentajeAsistencia) {
    }
}
