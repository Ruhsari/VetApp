package com.example.vetapp.controller;

import com.example.vetapp.model.Appointment;
import com.example.vetapp.model.User;
import com.example.vetapp.service.FileDataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class ProfileController {
    @FXML private Label usernameLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label addressLabel;
    @FXML private Label statusLabel;
    @FXML private VBox appointmentsContainer;

    private User user;
    private ClientMainController parentController;

    public void setUser(User user) {
        this.user = user;
        if (user == null) {
            System.err.println("Пользователь не установлен в ProfileController");
            return;
        }

        if (usernameLabel != null) usernameLabel.setText("User name: " + user.getUsername());
        if (fullNameLabel != null) fullNameLabel.setText("Your full name: " + user.getFullName());
        if (phoneLabel != null) phoneLabel.setText("Phone number: " + user.getPhone());
        if (addressLabel != null) addressLabel.setText("Address: " + user.getAddress());
        if (statusLabel != null) statusLabel.setText("Status: " + user.getRole());

        refreshAppointments();
    }

    public void setParentController(ClientMainController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация ProfileController");
    }

    public void refreshAppointments() {
        if (appointmentsContainer == null) {
            System.err.println("appointmentsContainer не инициализирован");
            return;
        }

        appointmentsContainer.getChildren().clear();
        try {
            List<Appointment> appointments = FileDataService.loadAppointmentsByUsername(user.getUsername());
            if (appointments.isEmpty()) {
                Label noAppointmentsLabel = new Label("No active appoinments");
                noAppointmentsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
                appointmentsContainer.getChildren().add(noAppointmentsLabel);
                return;
            }

            for (Appointment appointment : appointments) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/appointment_card.fxml"));
                VBox card = loader.load();
                AppointmentCardController controller = loader.getController();
                controller.setAppointment(appointment);
                controller.setParentController(this);
                appointmentsContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке записей: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void showRescheduleForm(Appointment appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/reschedule_appointment.fxml"));
            Pane formPane = loader.load();
            RescheduleAppointmentController controller = loader.getController();
            controller.setAppointment(appointment);
            controller.setParentController(this);
            parentController.getContentPane().getChildren().setAll(formPane);
        } catch (IOException e) {
            System.err.println("Ошибка при открытии формы переноса: " + e.getMessage());
        }
    }

    public void showProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/profile.fxml"));
            Pane profilePane = loader.load();
            ProfileController controller = loader.getController();
            controller.setUser(user);
            controller.setParentController(parentController);
            parentController.getContentPane().getChildren().setAll(profilePane);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке профиля: " + e.getMessage());
        }
    }
}