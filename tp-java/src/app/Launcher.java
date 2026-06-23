package app;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase que permite iniciar la aplicación de JavaFX y manejar la persistencia
 * de datos en la carga del .json y su posterior guardado al cerrar la ventana.
 */

public class Launcher {

    private static servicio.Universidad universidad;
    private static boolean huboErroresAlCargar = false;
    private static String rutaCargada = "datos.json";

    // Método que permite obtener la universidad, si no existe, se crea una nueva
    // universidad.
    public static servicio.Universidad getUniversidad() {
        if (universidad == null) {
            System.out.println("Buscando y cargando archivo de datos JSON...");
            persistencia.Serializador serializador = new persistencia.Serializador();

            // Determinar cuál archivo existe
            String[] rutasPosibles = { "datos.json", "datos.JSON", "../datos.json", "../datos.JSON" };
            String rutaEncontrada = null;
            for (String r : rutasPosibles) {
                if (java.nio.file.Files.exists(java.nio.file.Path.of(r))) {
                    rutaEncontrada = r;
                    break;
                }
            }

            if (rutaEncontrada == null) {
                System.out.println("No se encontró ningún archivo de datos. Iniciando universidad vacía.");
                universidad = new servicio.Universidad();
                rutaCargada = "datos.json"; // Ruta por defecto para guardar al salir
            } else {
                try {
                    rutaCargada = rutaEncontrada;
                    universidad = serializador.cargar(rutaEncontrada);
                    System.out.println("Datos cargados exitosamente desde " + rutaEncontrada);

                    persistencia.InformeErrores informe = serializador.getUltimoInforme();
                    if (informe != null && informe.hayErrores()) {
                        System.out.println(
                                "Se encontraron errores de validación en algunos registros de " + rutaEncontrada);
                        // No activamos huboErroresAlCargar para permitir que se guarde la versión
                        // limpia y ordenada al salir.

                        // Mostrar Warning Dialog en JavaFX thread
                        Platform.runLater(() -> {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.WARNING);
                            alert.setTitle("Advertencia de Carga de Datos");
                            alert.setHeaderText("Se encontraron registros inválidos en " + rutaCargada);
                            alert.setContentText(
                                    "Los registros correctos fueron cargados con éxito. Se guardará una versión limpia y ordenada de los datos al cerrar.");

                            javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(
                                    informe.toString());
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
                    System.out.println("Error crítico al cargar " + rutaEncontrada + ": " + ex.getMessage());
                    huboErroresAlCargar = true;
                    universidad = new servicio.Universidad();

                    // Mostrar Error Dialog para error crítico de formato/Lectura
                    Platform.runLater(() -> {
                        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                javafx.scene.control.Alert.AlertType.ERROR);
                        alert.setTitle("Error Crítico de Carga");
                        alert.setHeaderText("No se pudo procesar el archivo " + rutaCargada);
                        alert.setContentText(
                                "El archivo contiene un error crítico de sintaxis o no se pudo leer. Se inició una universidad vacía. El guardado automático está desactivado.");

                        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(ex.getMessage());
                        textArea.setEditable(false);
                        textArea.setWrapText(true);
                        textArea.setPrefWidth(500);
                        textArea.setPrefHeight(250);

                        alert.getDialogPane().setExpandableContent(textArea);
                        alert.getDialogPane().setExpanded(true);
                        alert.showAndWait();
                    });
                }
            }
        }
        return universidad;
    }

    public static void guardarDatos() {
        if (huboErroresAlCargar) {
            System.out.println("Guardado cancelado: se detectaron errores al cargar el archivo de datos original.");
            return;
        }
        try {
            new persistencia.Serializador().guardar(getUniversidad(), rutaCargada);
            System.out.println("Datos guardados exitosamente en " + rutaCargada);
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