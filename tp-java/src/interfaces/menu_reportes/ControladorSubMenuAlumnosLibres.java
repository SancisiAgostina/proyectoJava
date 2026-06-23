package interfaces.menu_reportes;

import java.io.IOException;
import java.util.List;
import interfaces.reportes.ControladorReportes;
import reportes.ReporteAlumnosLibres;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador que se encarga de generar el reporte de alumnos libres.
 * Aplica filtros por año de carrera y permite ver todos los alumnos libres.
 * Y lo envia al ControladorReportes para que se muestre en la tabla genérica
 * y permite su descarga como .txt a partir del ExportadorTexto.java.
 */

public class ControladorSubMenuAlumnosLibres {

    // Inyectamos el ComboBox definido en el FXML
    @FXML
    private ComboBox<String> comboBoxAnios;

    @FXML
    private Label labelMensaje;

    @FXML
    public void initialize() {
        try {
            // Los años de carrera deben ser de 1 a 5 para ReporteAlumnosLibres
            List<String> aniosDisponibles = List.of("1", "2", "3", "4", "5");

            // 2. Cargás los años en el ComboBox
            comboBoxAnios.getItems().addAll(aniosDisponibles);

            // 3. Opcional: Agregás la opción "Todos" al principio o al final
            comboBoxAnios.getItems().add("Todos");

            // Ocultar mensaje inicial
            labelMensaje.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sobreBotonReporteAlumnosLibres(ActionEvent event) {
        // Obtener el valor seleccionado o escrito por el usuario
        String anioSeleccionado = comboBoxAnios.getValue();

        // Validación básica por si no seleccionó nada
        if (anioSeleccionado == null || anioSeleccionado.trim().isEmpty()) {
            labelMensaje.setText("Por favor, selecciona o escribe un año.");
            labelMensaje.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
            labelMensaje.setVisible(true);
            return;
        }

        try {
            // Cargamos el FXML de la tabla genérica
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/interfaces/reportes/reportes.fxml"));
            Parent root = fxmlLoader.load();

            // 2. OBTENEMOS EL CONTROLADOR de la tabla genérica
            ControladorReportes controladorTabla = fxmlLoader.getController();

            // AGREGAR EL METODO QUE OBTENGA LOS ALUMNOS FILTRADOS POR AÑO
            ReporteAlumnosLibres reporte = new ReporteAlumnosLibres(app.Launcher.getUniversidad());
            List<ReporteAlumnosLibres.AlumnoLibre> listaAlumnosLibres;
            String tituloReporte;

            if (anioSeleccionado.equalsIgnoreCase("Todos")) {
                listaAlumnosLibres = reporte.generarTodos();
                tituloReporte = "Reporte Histórico de Todos los Alumnos Libres";
            } else {
                int anio = Integer.parseInt(anioSeleccionado.trim());
                listaAlumnosLibres = reporte.generarPorAnio(anio);
                tituloReporte = "Reporte de Alumnos Libres - Año de Carrera: " + anio;
            }

            List<FilaAlumnoLibre> filas = listaAlumnosLibres.stream()
                    .map(FilaAlumnoLibre::new)
                    .toList();

            // 4. INYECTAMOS la configuración a la pantalla única
            controladorTabla.configurarPantalla(
                    tituloReporte,
                    new String[] { "legajo", "nombreCompleto", "asignatura", "modalidad", "asistencia", "porcentaje" },
                    new String[] { "Legajo", "Alumno", "Asignatura", "Modalidad", "Asistencias", "% Asistencia" },
                    filas,
                    ruta -> {
                        if (anioSeleccionado.equalsIgnoreCase("Todos")) {
                            reporte.exportarTodos(ruta);
                        } else {
                            int anio = Integer.parseInt(anioSeleccionado.trim());
                            reporte.exportarPorAnio(anio, ruta);
                        }
                    });

            // Cambiamos de pantalla usando setRoot tal como lo tenías
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sobreBotonVolver(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/interfaces/menu_reportes/menu-reportes.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // DTO interno para formatear y exponer las propiedades de AlumnoLibre a la
    // tabla
    public static class FilaAlumnoLibre {
        private final String legajo;
        private final String nombreCompleto;
        private final String asignatura;
        private final String modalidad;
        private final String asistencia;
        private final String porcentaje;

        public FilaAlumnoLibre(ReporteAlumnosLibres.AlumnoLibre al) {
            this.legajo = String.valueOf(al.alumno().getMatricula());
            this.nombreCompleto = al.alumno().getApellido() + ", " + al.alumno().getNombre();
            this.asignatura = al.asignatura().getNombre();
            this.modalidad = al.modalidad().toString();
            this.asistencia = al.cantidadPresentes() + "/" + al.cantidadClases();
            this.porcentaje = String.format("%.2f%%", al.porcentajeAsistencia());
        }

        public String getLegajo() {
            return legajo;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public String getAsignatura() {
            return asignatura;
        }

        public String getModalidad() {
            return modalidad;
        }

        public String getAsistencia() {
            return asistencia;
        }

        public String getPorcentaje() {
            return porcentaje;
        }
    }
}