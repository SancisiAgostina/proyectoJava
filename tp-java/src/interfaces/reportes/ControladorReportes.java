package interfaces.reportes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.StackPane;
import java.util.List;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Controlador que se encarga de mostrar los reportes en una tabla genérica.
 * Recibe el titulo, las columnas, los nombres de las cabeceras, los datos y el
 * exportador.
 * Luego permite exportar el reporte a un archivo de texto.
 */

public class ControladorReportes {

    @FXML
    private Label labelTituloReporte;
    @FXML
    private StackPane contenedorTabla;

    private Exporter exportador;

    public interface Exporter {
        void exportar(java.nio.file.Path ruta) throws Exception;
    }

    public void configurarPantalla(String titulo, String[] columnas, String[] nombresCabecera, List<?> datos,
            Exporter exportador) {
        this.exportador = exportador;

        // Seteamos el título dinámico
        labelTituloReporte.setText(titulo);

        // Limpiamos el contenedor
        contenedorTabla.getChildren().clear();

        // Creamos la tabla plana dinámicamente
        TableView<Object> tablaReporte = new TableView<>();
        tablaReporte.setPlaceholder(new Label("No hay datos cargados para este reporte."));

        // Creamos las columnas dinámicamente
        for (int i = 0; i < columnas.length; i++) {
            String propiedad = columnas[i];
            String cabecera = (i < nombresCabecera.length) ? nombresCabecera[i] : propiedad;

            TableColumn<Object, Object> nuevaColumna = new TableColumn<>(cabecera);
            nuevaColumna.setCellValueFactory(new PropertyValueFactory<>(propiedad));
            nuevaColumna.prefWidthProperty().bind(tablaReporte.widthProperty().divide(columnas.length).subtract(1));

            tablaReporte.getColumns().add(nuevaColumna);
        }

        // Cargamos los datos
        ObservableList<Object> listaObservable = FXCollections.observableArrayList(datos);
        tablaReporte.setItems(listaObservable);

        // Agregamos la tabla al contenedor
        contenedorTabla.getChildren().add(tablaReporte);
    }

    /**
     * Sobrecarga para configurar un reporte jerárquico (TreeTableView)
     */
    @SuppressWarnings("unchecked")
    public void configurarPantalla(String titulo, String[] columnas, String[] nombresCabecera, TreeItem<?> nodoRaiz,
            Exporter exportador) {
        this.exportador = exportador;

        // Seteamos el título dinámico
        labelTituloReporte.setText(titulo);

        // Limpiamos el contenedor
        contenedorTabla.getChildren().clear();

        // Creamos el TreeTableView dinámicamente
        TreeTableView<Object> treeTablaReporte = new TreeTableView<>();
        treeTablaReporte.setPlaceholder(new Label("No hay datos cargados para este reporte."));

        // Creamos las columnas dinámicamente para TreeTableColumn
        for (int i = 0; i < columnas.length; i++) {
            String propiedad = columnas[i];
            String cabecera = (i < nombresCabecera.length) ? nombresCabecera[i] : propiedad;

            TreeTableColumn<Object, Object> nuevaColumna = new TreeTableColumn<>(cabecera);
            nuevaColumna.setCellValueFactory(new TreeItemPropertyValueFactory<>(propiedad));
            nuevaColumna.prefWidthProperty().bind(treeTablaReporte.widthProperty().divide(columnas.length).subtract(1));

            treeTablaReporte.getColumns().add(nuevaColumna);
        }

        // Cargamos el nodo raíz y configuramos para que muestre la raíz o no
        treeTablaReporte.setRoot((TreeItem<Object>) nodoRaiz);
        treeTablaReporte.setShowRoot(false); // Opcional: ocultar el nodo raíz si sirve solo de contenedor

        // Agregamos la tabla al contenedor
        contenedorTabla.getChildren().add(treeTablaReporte);
    }

    @FXML
    private void sobreBotonGenerarReporte(ActionEvent event) {
        if (exportador == null) {
            return;
        }

        // Abrir un FileChooser para elegir dónde guardar el archivo de texto
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar Reporte de Texto");
        fileChooser.getExtensionFilters()
                .add(new javafx.stage.FileChooser.ExtensionFilter("Archivos de texto (*.txt)", "*.txt"));

        // Sugerir un nombre de archivo predeterminado basado en el título del reporte
        String nombreSugerido = labelTituloReporte.getText()
                .replaceAll("[^a-zA-Z0-9.-]", "_")
                .toLowerCase() + ".txt";
        fileChooser.setInitialFileName(nombreSugerido);

        // Mostrar el diálogo de guardado
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        java.io.File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // Ejecutar el exportador específico del reporte
                exportador.exportar(file.toPath());

                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Reporte Exportado");
                alert.setHeaderText(null);
                alert.setContentText("El reporte ha sido exportado exitosamente en:\n" + file.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error al Exportar");
                alert.setHeaderText("No se pudo generar el archivo de texto");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
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
}