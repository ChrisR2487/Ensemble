module com.ensemblecp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires com.flexganttfx.core;
    requires com.flexganttfx.extras;
    requires com.flexganttfx.model;
    requires com.flexganttfx.view;

    opens com.ensemblecp to javafx.fxml;
    exports com.ensemblecp;
}