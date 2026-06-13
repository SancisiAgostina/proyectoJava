package interfaces.menu_reportes;

import java.io.IOException;
import java.util.List;
import reportes.ReporteDatosAsignatura.DetalleAlumno;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import interfaces.reportes.ControladorReportes;

public class ControladorMenuReportes {

	@FXML
	private void sobreBotonReportePresentismo(ActionEvent event) {
		try {
			// Generar el reporte de presentismo para todas las asignaturas
			reportes.ReportePresentismo reporte = new reportes.ReportePresentismo(app.Launcher.getUniversidad());
			List<reportes.ReportePresentismo.ItemPresentismo> items = reporte.generar();

			// Convertir a filas DTO para TableView
			List<FilaReportePresentismo> filas = java.util.stream.IntStream.range(0, items.size())
					.mapToObj(i -> new FilaReportePresentismo(i + 1, items.get(i)))
					.toList();

			// Cargar la vista de reportes
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/interfaces/reportes/reportes.fxml"));
			Parent root = fxmlLoader.load();

			// Configurar la tabla de reportes
			ControladorReportes controladorTabla = fxmlLoader.getController();
			controladorTabla.configurarPantalla(
				"Ranking de Presentismo - Todas las Asignaturas",
				new String[]{"puesto", "codigo", "nombre", "clases", "alumnos", "porcentaje"},
				new String[]{"Puesto", "Código", "Asignatura", "Clases", "Alumnos", "% Presentismo"},
				filas,
				ruta -> reporte.exportar(ruta)
			);

			// Cambiar de pantalla
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void sobreBotonReporteAsignaturas(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/interfaces/menu_reportes/submenu-asignaturas.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void sobreBotonReporteAlumnosLibres(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/interfaces/menu_reportes/submenu-alumnos-libres.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void sobreBotonVolver(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/interfaces/menu_principal/menu-principal.fxml"));
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

	public static class FilaReportePresentismo {
		private final int puesto;
		private final String codigo;
		private final String nombre;
		private final int clases;
		private final int alumnos;
		private final String porcentaje;

		public FilaReportePresentismo(int puesto, reportes.ReportePresentismo.ItemPresentismo item) {
			this.puesto = puesto;
			this.codigo = item.asignatura().getCodigo();
			this.nombre = item.asignatura().getNombre();
			this.clases = item.cantidadClases();
			this.alumnos = item.cantidadInscriptos();
			this.porcentaje = String.format("%.2f%%", item.porcentaje());
		}

		public int getPuesto() { return puesto; }
		public String getCodigo() { return codigo; }
		public String getNombre() { return nombre; }
		public int getClases() { return clases; }
		public int getAlumnos() { return alumnos; }
		public String getPorcentaje() { return porcentaje; }
	}
}