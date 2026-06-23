package interfaces.menu_reportes;

import java.io.IOException;
import java.util.List;
import interfaces.reportes.ControladorReportes;
import reportes.ReporteDatosAsignatura;
import reportes.ReporteDatosAsignatura.DetalleAlumno;
import dominio.Asignatura;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador que se encarga de generar el reporte de asignaturas.
 * Filtra las asignaturas por codigo y nombre.
 * Envia al ControladorReportes para que se muestre en la tabla genérica
 * y permite su descarga como .txt a partir del ExportadorTexto.java.
 */

public class ControladorSubMenuAsignaturas {

    @FXML
    private ComboBox<Asignatura> comboBoxAsignaturas;

    @FXML
    private Label labelMensaje;

    @FXML
    public void initialize() {
        try {
            // Cargar las asignaturas en el combobox
            List<Asignatura> asignaturas = app.Launcher.getUniversidad().getAsignaturas();
            comboBoxAsignaturas.getItems().addAll(asignaturas);
            labelMensaje.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sobreBotonReporteAsignatura(ActionEvent event) {
        Asignatura asignaturaSeleccionada = comboBoxAsignaturas.getValue();

        if (asignaturaSeleccionada == null) {
            labelMensaje.setText("Por favor, selecciona una asignatura.");
            labelMensaje.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
            labelMensaje.setVisible(true);
            return;
        }

        try {
            // Cargar el FXML de la tabla genérica
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/interfaces/reportes/reportes.fxml"));
            Parent root = fxmlLoader.load();

            // Configurar el controlador de reportes
            ControladorReportes controladorTabla = fxmlLoader.getController();

            // Generar el reporte
            ReporteDatosAsignatura reporte = new ReporteDatosAsignatura(app.Launcher.getUniversidad());
            ReporteDatosAsignatura.DatosAsignatura datos = reporte.generar(asignaturaSeleccionada.getCodigo());

            // Convertir a DTO compatible con PropertyValueFactory
            List<FilaReporteAsignatura> filas = datos.alumnos().stream()
                    .map(detalle -> new FilaReporteAsignatura(detalle, datos.clases().size()))
                    .toList();

            String tituloReporte = "Reporte de Asignatura: " + datos.asignatura().getNombre() + " ("
                    + datos.asignatura().getCodigo() + ")";

            controladorTabla.configurarPantalla(
                    tituloReporte,
                    new String[] { "legajo", "nombreCompleto", "modalidad", "asistencia", "porcentajeAsistencia",
                            "condicion" },
                    new String[] { "Legajo", "Nombre Completo", "Modalidad", "Asistencias", "% Asistencia",
                            "Condición" },
                    filas,
                    ruta -> reporte.exportar(asignaturaSeleccionada.getCodigo(), ruta));

            // Cambiar de pantalla
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

    public static class FilaReporteAsignatura {
        private final String legajo;
        private final String nombreCompleto;
        private final String modalidad;
        private final String asistencia;
        private final String porcentajeAsistencia;
        private final String condicion;

        public FilaReporteAsignatura(DetalleAlumno detalle, int totalClases) {
            this.legajo = String.valueOf(detalle.alumno().getMatricula());
            this.nombreCompleto = detalle.alumno().getApellido() + ", " + detalle.alumno().getNombre();
            this.modalidad = detalle.modalidad().toString();
            this.asistencia = detalle.cantidadPresentes() + "/" + totalClases;
            this.porcentajeAsistencia = String.format("%.2f%%", detalle.porcentajeAsistencia());
            this.condicion = detalle.condicion().toString();
        }

        public String getLegajo() {
            return legajo;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public String getModalidad() {
            return modalidad;
        }

        public String getAsistencia() {
            return asistencia;
        }

        public String getPorcentajeAsistencia() {
            return porcentajeAsistencia;
        }

        public String getCondicion() {
            return condicion;
        }
    }
}