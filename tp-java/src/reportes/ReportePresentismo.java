package reportes;

import dominio.Asignatura;
import dominio.Clase;
import dominio.Inscripcion;
import servicio.Universidad;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Clase que se encarga de generar el reporte de presentismo.
 * Ordena las asignaturas por porcentaje de asistencia y muestra el ranking.
 * Los datos se los pasa al ControladorReportes para que se muestren en la tabla
 * genérica.
 */

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

        public void exportar(java.nio.file.Path ruta) throws java.io.IOException {
                ExportadorTexto.exportar(generarTexto(), ruta);
        }

        private ItemPresentismo crearItem(Asignatura asignatura) {
                List<Clase> clases = universidad.getClases().stream()
                                .filter(clase -> mismaAsignatura(clase.getAsignatura(), asignatura))
                                .toList();

                List<Inscripcion> inscripciones = universidad.getInscripciones().stream()
                                .filter(inscripcion -> mismaAsignatura(inscripcion.getAsignatura(), asignatura))
                                .toList();

                List<ItemPresentismoClase> detalleClases = clases.stream().map(clase -> {
                        int asistenciasEnClase = (int) inscripciones.stream()
                                        .filter(insc -> insc.getClasesAsistidas().contains(clase))
                                        .count();
                        int inscriptos = inscripciones.size();
                        double pct = inscriptos == 0 ? 0 : asistenciasEnClase * 100.0 / inscriptos;
                        return new ItemPresentismoClase(clase, inscriptos, asistenciasEnClase, pct);
                })
                                .sorted(Comparator.comparing(c -> c.clase().getFechahora()))
                                .toList();

                int asistenciasRegistradas = detalleClases.stream()
                                .mapToInt(ItemPresentismoClase::asistenciasRegistradas).sum();
                int asistenciasPosibles = clases.size() * inscripciones.size();
                double porcentaje = asistenciasPosibles == 0
                                ? 0
                                : asistenciasRegistradas * 100.0 / asistenciasPosibles;

                return new ItemPresentismo(
                                asignatura, clases.size(), inscripciones.size(),
                                asistenciasRegistradas, asistenciasPosibles, porcentaje, detalleClases);
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
                        double porcentaje,
                        List<ItemPresentismoClase> detalleClases) {
        }

        public record ItemPresentismoClase(
                        Clase clase,
                        int cantidadInscriptos,
                        int asistenciasRegistradas,
                        double porcentaje) {
        }

        public List<ItemPresentismoClase> generarDetallePorClase(Asignatura asignatura) {
                return crearItem(asignatura).detalleClases();
        }
}
