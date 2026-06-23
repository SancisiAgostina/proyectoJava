package interfaces.menu_asistencias;

import java.io.IOException;
import java.util.List;
import dominio.Alumno;
import dominio.Asignatura;
import dominio.Clase;
import dominio.excepciones.DatoInvalidoException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controlador que se encarga de registrar asistencias de los alumnos a clases.
 * Aplicando los diferentes filtros respectivamente a materias, alumnos e clases
 * para que se muestren los correspondientes.
 * Muestra mensajes en caso de ya haber indicado la asistencia.
 */

public class ControladorMenuAsistencia {

	@FXML
	private ComboBox<Alumno> comboBoxEstudiantes;

	@FXML
	private ComboBox<Asignatura> comboBoxMaterias;

	@FXML
	private ComboBox<Clase> comboBoxClases;

	@FXML
	private Label labelMensaje;

	@FXML
	public void initialize() {
		try {
			// Cargar materias inicialmente
			comboBoxMaterias.getItems().addAll(app.Launcher.getUniversidad().getAsignaturas());

			// Escuchar cambios en la materia seleccionada para cargar/filtrar los alumnos
			// inscritos y las clases
			comboBoxMaterias.valueProperty().addListener((observable, oldValue, newValue) -> {
				actualizarEstudiantesYClases(newValue);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actualizarEstudiantesYClases(Asignatura materiaSeleccionada) {
		comboBoxEstudiantes.setValue(null);
		comboBoxEstudiantes.getItems().clear();

		comboBoxClases.setValue(null);
		comboBoxClases.getItems().clear();

		// Ocultar mensaje al cambiar de materia
		labelMensaje.setVisible(false);

		if (materiaSeleccionada != null) {
			// Obtener todos los alumnos inscritos a esta materia (sin importar modalidad)
			List<Alumno> alumnosInscritos = app.Launcher.getUniversidad().getInscripciones().stream()
					.filter(inscripcion -> inscripcion.getAsignatura() != null &&
							inscripcion.getAsignatura().getCodigo().equals(materiaSeleccionada.getCodigo()))
					.map(dominio.Inscripcion::getAlumno)
					.distinct()
					.sorted()
					.toList();

			comboBoxEstudiantes.getItems().addAll(alumnosInscritos);

			// Obtener las clases dictadas para la materia seleccionada
			List<Clase> clasesMateria = app.Launcher.getUniversidad().getClases().stream()
					.filter(clase -> clase.getAsignatura() != null &&
							clase.getAsignatura().getCodigo().equals(materiaSeleccionada.getCodigo()))
					.toList();

			comboBoxClases.getItems().addAll(clasesMateria);
		}
	}

	private void mostrarMensaje(String texto, boolean esError) {
		labelMensaje.setText(texto);
		if (esError) {
			labelMensaje.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
		} else {
			labelMensaje.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14px; -fx-font-weight: bold;");
		}
		labelMensaje.setVisible(true);
	}

	@FXML
	private void sobreRegistrarAsistencia(ActionEvent event) {
		Alumno alumnoSeleccionado = comboBoxEstudiantes.getValue();
		Asignatura materiaSeleccionada = comboBoxMaterias.getValue();
		Clase claseSeleccionada = comboBoxClases.getValue();

		if (alumnoSeleccionado == null || materiaSeleccionada == null || claseSeleccionada == null) {
			mostrarMensaje("Por favor, selecciona un estudiante, una materia y una clase.", true);
			return;
		}

		try {
			// Registrar asistencia en el modelo directamente con la clase elegida
			app.Launcher.getUniversidad().registrarAsistencia(
					alumnoSeleccionado.getMatricula(),
					claseSeleccionada.getId());

			mostrarMensaje("Asistencia registrada correctamente para " +
					alumnoSeleccionado.getNombre() + " en la clase del " +
					claseSeleccionada.getId(), false);

		} catch (DatoInvalidoException e) {
			mostrarMensaje(e.getMessage(), true);
		}
	}

	@FXML
	private void sobreVolver(ActionEvent event) {
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
}