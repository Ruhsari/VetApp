//module com.example.vetapp {
//    requires javafx.controls;
//    requires javafx.fxml;
//
//
//    opens com.example.vetapp to javafx.fxml;
//    exports com.example.vetapp;
//    exports com.example.vetapp.controller;
//    opens com.example.vetapp.controller to javafx.fxml;
//    exports com.example.vetapp.model;
//    opens com.example.vetapp.model to javafx.fxml;
//    exports com.example.vetapp.service;
//    opens com.example.vetapp.service to javafx.fxml;
//}


module com.example.vetapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.vetapp to javafx.fxml;
    opens com.example.vetapp.controller to javafx.fxml;
    exports com.example.vetapp;
    exports com.example.vetapp.service;
    opens com.example.vetapp.service to javafx.fxml;
}