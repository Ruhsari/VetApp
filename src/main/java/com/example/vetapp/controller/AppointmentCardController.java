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
        petNameLabel.setText("Питомец: " + appointment.getPetName());
        petTypeLabel.setText("Тип: " + appointment.getPetType());
        specialistLabel.setText("Специалист: ID " + appointment.getSpecialistId());
        dateTimeLabel.setText("Дата и время: " + appointment.getDate() + " " + appointment.getTime());
        reasonLabel.setText("Причина: " + appointment.getReason());
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
        confirmation.setTitle("Подтверждение удаления");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Вы уверены, что хотите удалить запись для " + appointment.getPetName() + " на " + appointment.getDate() + " " + appointment.getTime() + "?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Удаляем запись
                FileDataService.deleteAppointment(appointment);
                // Возвращаем слот в расписание
                FileDataService.addSchedule(new Schedule(appointment.getSpecialistId(), appointment.getDate(), appointment.getTime()));
                // Обновляем профиль
                parentController.refreshAppointments();
                showAlert("Успех", "Запись успешно удалена!");
            } catch (IOException e) {
                showAlert("Ошибка", "Ошибка при удалении записи: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(message.contains("Ошибка") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}