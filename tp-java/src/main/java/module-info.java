module org.example.tpjava {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.tpjava to javafx.fxml;
    exports org.example.tpjava;
}