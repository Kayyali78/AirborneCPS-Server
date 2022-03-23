module com.csc380.teame.airbornecpsserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.gmapsfx;
    requires javafx.swing;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.web;


    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.csc380.teame.airbornecpsserver to javafx.fxml;
    exports com.csc380.teame.airbornecpsserver;
}