package com.example.vetapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/vetapp/view/auth.fxml"));
        primaryStage.setTitle("VetApp");
        primaryStage.setScene(new Scene(root, 1536, 864));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}