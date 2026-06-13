package app;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher {

    private static servicio.Universidad universidad;

    public static servicio.Universidad getUniversidad() {
        if (universidad == null) {
            System.out.println("No se encontró universidad.dat o está corrupto. Cargando desde datos.JSON...");
            universidad = new servicio.Universidad();
            persistencia.CargadorJSON cargador = new persistencia.CargadorJSON();
            try {
                try {
                    cargador.cargar("datos.JSON", universidad);
                } catch (IOException ex) {
                    cargador.cargar("../datos.JSON", universidad);
                }

                // Si hubo errores al deserializar los datos de datos.JSON, mostrar un Warning
                // Dialog
                if (cargador.getInforme().hayErrores()) {
                    Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.WARNING);
                        alert.setTitle("Advertencia de Carga de Datos");
                        alert.setHeaderText("Se encontraron errores de validación al deserializar datos.JSON");
                        alert.setContentText("Los siguientes registros no pudieron ser cargados:");

                        // Formatear los errores en un listado legible y presentarlo en un TextArea con
                        // scroll
                        String detalleErrores = String.join("\n", cargador.getInforme().getErrores());
                        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(detalleErrores);
                        textArea.setEditable(false);
                        textArea.setWrapText(true);
                        textArea.setPrefWidth(500);
                        textArea.setPrefHeight(250);

                        alert.getDialogPane().setExpandableContent(textArea);
                        alert.getDialogPane().setExpanded(true);
                        alert.showAndWait();
                    });
                }
            } catch (Exception ex) {
                System.out.println("Aviso: No se pudo cargar datos.JSON automáticamente: " + ex.getMessage());
            }
        }
        return universidad;
    }

    public static void guardarDatos() {
        try {
            new persistencia.Serializador().guardar(getUniversidad(), "universidad.dat");
            System.out.println("Datos guardados exitosamente en universidad.dat");
        } catch (IOException e) {
            System.err.println("Error al serializar y guardar los datos: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                // Forzar la carga de la base de datos (y mostrar advertencias del JSON si las
                // hay) inmediatamente al iniciar
                getUniversidad();

                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(
                        Launcher.class.getResource("/interfaces/menu_principal/menu-principal.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 720, 480);
                stage.setTitle("Gestor de Asistencias");
                stage.setScene(scene);
                stage.setMaximized(true);

                // Configurar guardado automático de datos cuando se cierra la ventana con la X
                stage.setOnCloseRequest(event -> {
                    guardarDatos();
                });

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Platform.exit();
            }
        });
    }

}