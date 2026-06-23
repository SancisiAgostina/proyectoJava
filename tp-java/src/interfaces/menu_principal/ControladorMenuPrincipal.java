package interfaces.menu_principal;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Controlador que se encarga de manejar el menu principal.
 * Maneja la navegacion entre los diferentes menues.
 */

public class ControladorMenuPrincipal {
	@FXML
	private void sobreBotonAsistencias(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/interfaces/menu_asistencias/menu-asistencia.fxml"));
			Parent root = fxmlLoader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.getScene().setRoot(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void sobreBotonReportes(ActionEvent event) {
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

	@FXML
	private void sobreBotonSalir(ActionEvent event) {
		app.Launcher.guardarDatos();
		System.exit(0);
	}
}