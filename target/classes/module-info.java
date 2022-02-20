module com.ensemblecp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.ensemblecp to javafx.fxml;
    exports com.ensemblecp;
}