module ni.edu.uam.PracticaHilos {
    requires javafx.controls;
    requires javafx.fxml;


    opens ni.edu.uam.PracticaHilos to javafx.fxml;
    exports ni.edu.uam.PracticaHilos;
}