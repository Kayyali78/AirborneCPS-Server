module com.csc380.teame.airbornecpsserver {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.csc380.teame.airbornecpsserver to javafx.fxml;
    exports com.csc380.teame.airbornecpsserver;
}