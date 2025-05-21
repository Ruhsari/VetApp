package com.example.vetapp.controller;

import com.example.vetapp.model.Specialist;
import com.example.vetapp.service.FileDataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

public class SpecialistLoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        System.out.println("Инициализация SpecialistLoginController");
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        System.out.println("Попытка входа врача: " + username);

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните имя пользователя и пароль");
            errorLabel.setVisible(true);
            return;
        }

        try {
            String fullName = FileDataService.authenticateSpecialistLogin(username, password);
            if (fullName != null) {
                Specialist specialist = FileDataService.loadSpecialists()
                        .stream()
                        .filter(s -> s.getName().equals(fullName))
                        .findFirst()
                        .orElse(null);
                if (specialist != null) {
                    System.out.println("Успешная авторизация врача: " + specialist.getName());
                    openSpecialistProfile(specialist);
                } else {
                    System.out.println("Врач не найден в specialists.txt: " + fullName);
                    errorLabel.setText("Врач не найден");
                    errorLabel.setVisible(true);
                }
            } else {
                System.out.println("Неверный логин или пароль для врача: " + username);
                errorLabel.setText("Неверный логин или пароль");
                errorLabel.setVisible(true);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при авторизации врача: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Ошибка загрузки данных");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    private void handleBack() {
        System.out.println("Возврат к окну авторизации");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/auth.fxml"));
            Parent authRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(authRoot));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке auth.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openSpecialistProfile(Specialist specialist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/specialist_profile.fxml"));
            Parent profileRoot = loader.load();
            SpecialistProfileController controller = loader.getController();
            controller.setSpecialist(specialist);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(profileRoot));
            stage.setTitle("Профиль врача");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке specialist_profile.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}