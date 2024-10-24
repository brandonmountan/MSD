module com.example.synthesizer4a {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.synthesizer4a to javafx.fxml;
    exports com.example.synthesizer4a;
}