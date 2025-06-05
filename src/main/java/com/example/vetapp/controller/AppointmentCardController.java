package com.example.vetapp.controller;

import com.example.vetapp.model.Appointment;
import com.example.vetapp.model.Schedule;
import com.example.vetapp.service.FileDataService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Optional;

public class AppointmentCardController {
    @FXML private Label petNameLabel;
    @FXML private Label petTypeLabel;
    @FXML private Label specialistLabel;
    @FXML private Label dateTimeLabel;
    @FXML private Label reasonLabel;

    private Appointment appointment;
    private ProfileController parentController;

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        updateCard();
    }

    public void setParentController(ProfileController parentController) {
        this.parentController = parentController;
    }

    private void updateCard() {
        petNameLabel.setText("Pet name: " + appointment.getPetName());
        petTypeLabel.setText("Type: " + appointment.getPetType());
        specialistLabel.setText("Specialist: ID " + appointment.getSpecialistId());
        dateTimeLabel.setText("Date and time: " + appointment.getDate() + " " + appointment.getTime());
        reasonLabel.setText("Visit reason: " + appointment.getReason());
    }

    @FXML
    private void handleReschedule() {
        if (parentController != null) {
            parentController.showRescheduleForm(appointment);
        } else {
            System.err.println("parentController не установлен в AppointmentCardController");
        }
    }

    @FXML
    private void handleDelete() {
        if (parentController == null) {
            System.err.println("parentController не установлен в AppointmentCardController");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete " + appointment.getPetName() + " appointment for " + appointment.getDate() + " " + appointment.getTime() + "?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Удаляем запись
                FileDataService.deleteAppointment(appointment);
                // Возвращаем слот в расписание
                FileDataService.addSchedule(new Schedule(appointment.getSpecialistId(), appointment.getDate(), appointment.getTime()));
                // Обновляем профиль
                parentController.refreshAppointments();
                showAlert("Success", "Appointment successfully deleted!");
            } catch (IOException e) {
                showAlert("Error", "Error deleting appointment: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(message.contains("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}