package com.example.vetapp.controller;

import com.example.vetapp.model.Client;
import com.example.vetapp.model.User;
import com.example.vetapp.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private TextField registerUsernameField;
    @FXML private PasswordField registerPasswordField;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private TabPane tabPane;
    @FXML private Tab loginTab;
    @FXML private Tab registerTab;
    @FXML private Button specialistLoginButton;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Ошибка", "Заполните имя пользователя и пароль");
            return;
        }

        try {
            System.out.println("Попытка входа с: " + username + ", " + password);
            User user = AuthService.authenticate(username, password);
            if (user != null && "client".equalsIgnoreCase(user.getRole())) {
                System.out.println("Успешный вход для: " + user.getUsername());
                loadMainWindow(event, user);
            } else {
                showAlert("Ошибка", "Неверное имя пользователя, пароль или роль");
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка при загрузке данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String username = registerUsernameField.getText().trim();
        String password = registerPasswordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showAlert("Ошибка", "Заполните обязательные поля");
            return;
        }

        Client newClient = new Client(username, password, fullName, phone, address);
        try {
            if (AuthService.registerClient(newClient)) {
                showAlert("Успех", "Регистрация прошла успешно");
                tabPane.getSelectionModel().select(loginTab);
                clearFields();
            } else {
                showAlert("Ошибка", "Пользователь с таким именем уже существует");
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка при сохранении данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSpecialistLogin(ActionEvent event) {
        System.out.println("Переход к окну авторизации врачей");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/specialist_login.fxml"));
            Parent specialistLoginRoot = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(specialistLoginRoot));
            stage.setTitle("Вход для врачей");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке specialist_login.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно входа для врачей");
        }
    }

    private void loadMainWindow(ActionEvent event, User user) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/client_main.fxml"));
            Parent root = loader.load();
            ClientMainController controller = loader.getController();
            controller.setUser(user);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Главная страница");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки client_main.fxml: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        registerUsernameField.clear();
        registerPasswordField.clear();
        fullNameField.clear();
        phoneField.clear();
        addressField.clear();
    }
}