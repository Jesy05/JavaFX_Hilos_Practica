module ni.edu.uam.nicadatasolutions {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.nicadatasolutions to javafx.fxml;
    exports ni.edu.uam.nicadatasolutions;
}