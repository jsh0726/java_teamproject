module java_teamproject {
    requires javafx.controls;
    opens application to javafx.graphics, javafx.fxml;
    exports application;
}
