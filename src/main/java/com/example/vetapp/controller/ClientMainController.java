package com.example.vetapp.controller;

import com.example.vetapp.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientMainController {
    @FXML private Pane contentPane;
    private User user;

    public Pane getContentPane() {
        return contentPane;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void initialize() {
        showClinicsList();
    }

    public void showClinicsList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/clinics_list.fxml"));
            Parent clinicsContent = loader.load();
            ClinicsListController controller = loader.getController();
            controller.setParentController(this);
            controller.initialize();
            contentPane.getChildren().setAll(clinicsContent);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке списка клиник: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/profile.fxml"));
            Parent profileContent = loader.load();
            ProfileController controller = loader.getController();
            controller.setUser(user);
            controller.setParentController(this);
            contentPane.getChildren().setAll(profileContent);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке профиля: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке экрана логина: " + e.getMessage());
            e.printStackTrace();
        }
    }
}