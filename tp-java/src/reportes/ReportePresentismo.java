package reportes;

import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import servicio.Universidad;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReportePresentismo {

    private final Universidad universidad;

    public ReportePresentismo(Universidad universidad) {
        this.universidad = universidad;
    }

    public List<ItemPresentismo> generar() {
        return universidad.getAsignaturas().stream()
                .map(this::crearItem)
                .sorted(Comparator.comparingDouble(ItemPresentismo::porcentaje)
                        .reversed()
                        .thenComparing(item -> item.asignatura().getCodigo()))
                .toList();
    }

    public String generarTexto() {
        StringBuilder texto = new StringBuilder("RANKING DE PRESENTISMO\n");
        int posicion = 1;

        for (ItemPresentismo item : generar()) {
            texto.append(String.format(Locale.ROOT,
                    "%d. %s - %s: %.2f%% (%d asistencias de %d posibles)%n",
                    posicion++,
                    item.asignatura().getCodigo(),
                    item.asignatura().getNombre(),
                    item.porcentaje(),
                    item.asistenciasRegistradas(),
                    item.asistenciasPosibles()));
        }
        return texto.toString();
    }

    public void mostrar() {
        System.out.print(generarTexto());
    }

    public void exportar(Path ruta) throws IOException {
        ExportadorTexto.exportar(generarTexto(), ruta);
    }

    private ItemPresentismo crearItem(Asignatura asignatura) {
        List<Clase> clases = universidad.getClases().stream()
                .filter(clase -> mismaAsignatura(clase.getAsignatura(), asignatura))
                .toList();

        List<Inscripcion> inscripciones = universidad.getInscripciones().stream()
                .filter(inscripcion -> mismaAsignatura(inscripcion.getAsignatura(), asignatura))
                .toList();

        int asistenciasRegistradas = inscripciones.stream()
                .mapToInt(inscripcion -> (int) inscripcion.getClasesAsistidas().stream()
                        .filter(clases::contains)
                        .count())
                .sum();
        int asistenciasPosibles = clases.size() * inscripciones.size();
        double porcentaje = asistenciasPosibles == 0
                ? 0
                : asistenciasRegistradas * 100.0 / asistenciasPosibles;

        return new ItemPresentismo(
                asignatura, clases.size(), inscripciones.size(),
                asistenciasRegistradas, asistenciasPosibles, porcentaje);
    }

    private boolean mismaAsignatura(Asignatura primera, Asignatura segunda) {
        return primera != null && segunda != null
                && primera.getCodigo().equals(segunda.getCodigo());
    }

    public record ItemPresentismo(
            Asignatura asignatura,
            int cantidadClases,
            int cantidadInscriptos,
            int asistenciasRegistradas,
            int asistenciasPosibles,
            double porcentaje) {
    }
}
