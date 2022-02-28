module com.ensemblecp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.ensemblecp to javafx.fxml;
    exports com.ensemblecp;
}