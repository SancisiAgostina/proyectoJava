package reportes;

import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import dominio.enums.Condicion;
import dominio.enums.Modalidad;
import dominio.excepciones.DatoInvalidoException;
import servicio.Universidad;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReporteDatosAsignatura {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Universidad universidad;

    public ReporteDatosAsignatura(Universidad universidad) {
        this.universidad = universidad;
    }

    public DatosAsignatura generar(String codigoAsignatura) throws DatoInvalidoException {
        Asignatura asignatura = buscarAsignatura(codigoAsignatura);
        List<Clase> clases = clasesDe(asignatura);

        List<DetalleAlumno> alumnos = universidad.getInscripciones().stream()
                .filter(inscripcion -> mismaAsignatura(inscripcion.getAsignatura(), asignatura))
                .sorted(Comparator.comparing(Inscripcion::getAlumno))
                .map(inscripcion -> crearDetalle(inscripcion, clases))
                .toList();

        return new DatosAsignatura(asignatura, List.copyOf(clases), alumnos);
    }

    public String generarTexto(String codigoAsignatura) throws DatoInvalidoException {
        DatosAsignatura datos = generar(codigoAsignatura);
        StringBuilder texto = new StringBuilder();
        texto.append("DETALLE DE ASIGNATURA\n")
                .append(datos.asignatura().getCodigo()).append(" - ")
                .append(datos.asignatura().getNombre()).append("\n")
                .append("Clases dictadas: ").append(datos.clases().size()).append("\n\n");

        for (DetalleAlumno detalle : datos.alumnos()) {
            texto.append(detalle.alumno()).append("\n")
                    .append("Modalidad: ").append(detalle.modalidad()).append("\n")
                    .append(String.format(Locale.ROOT,
                            "Presentes: %d/%d - %.2f%%%n",
                            detalle.cantidadPresentes(),
                            datos.clases().size(),
                            detalle.porcentajeAsistencia()))
                    .append("Condición: ").append(detalle.condicion()).append("\n");

            for (DetalleClase clase : detalle.clases()) {
                texto.append("  ")
                        .append(clase.clase().getId()).append(" - ")
                        .append(clase.clase().getFechahora().format(FORMATO_FECHA))
                        .append(": ")
                        .append(clase.presente() ? "PRESENTE" : "AUSENTE")
                        .append("\n");
            }
            texto.append("\n");
        }
        return texto.toString();
    }

    public void mostrar(String codigoAsignatura) throws DatoInvalidoException {
        System.out.print(generarTexto(codigoAsignatura));
    }

    public void exportar(String codigoAsignatura, Path ruta)
            throws IOException, DatoInvalidoException {
        ExportadorTexto.exportar(generarTexto(codigoAsignatura), ruta);
    }

    private Asignatura buscarAsignatura(String codigo) throws DatoInvalidoException {
        if (codigo == null || codigo.isBlank()) {
            throw new DatoInvalidoException("Debe indicar el código de la asignatura.");
        }
        return universidad.getAsignaturas().stream()
                .filter(asignatura -> asignatura.getCodigo().equals(codigo))
                .findFirst()
                .orElseThrow(() -> new DatoInvalidoException(
                        "No existe una asignatura con código " + codigo + "."));
    }

    private List<Clase> clasesDe(Asignatura asignatura) {
        return universidad.getClases().stream()
                .filter(clase -> mismaAsignatura(clase.getAsignatura(), asignatura))
                .sorted(Comparator.comparing(Clase::getFechahora))
                .toList();
    }

    private DetalleAlumno crearDetalle(Inscripcion inscripcion, List<Clase> clases) {
        List<DetalleClase> detalleClases = clases.stream()
                .map(clase -> new DetalleClase(
                        clase, inscripcion.getClasesAsistidas().contains(clase)))
                .toList();
        int cantidadPresentes = (int) detalleClases.stream()
                .filter(DetalleClase::presente)
                .count();
        double porcentaje = clases.isEmpty()
                ? 0
                : cantidadPresentes * 100.0 / clases.size();

        return new DetalleAlumno(
                inscripcion.getAlumno(),
                inscripcion.getModalidad(),
                cantidadPresentes,
                porcentaje,
                inscripcion.calcularCondicion(cantidadPresentes, clases.size()),
                detalleClases);
    }

    private boolean mismaAsignatura(Asignatura primera, Asignatura segunda) {
        return primera != null && segunda != null
                && primera.getCodigo().equals(segunda.getCodigo());
    }

    public record DatosAsignatura(
            Asignatura asignatura, List<Clase> clases, List<DetalleAlumno> alumnos) {
    }

    public record DetalleAlumno(
            Alumno alumno,
            Modalidad modalidad,
            int cantidadPresentes,
            double porcentajeAsistencia,
            Condicion condicion,
            List<DetalleClase> clases) {
    }

    public record DetalleClase(Clase clase, boolean presente) {
    }
}
