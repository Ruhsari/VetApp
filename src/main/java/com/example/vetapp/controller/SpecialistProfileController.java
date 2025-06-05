package com.example.vetapp.controller;

import com.example.vetapp.model.Appointment;
import com.example.vetapp.model.Clinic;
import com.example.vetapp.model.Specialist;
import com.example.vetapp.service.FileDataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SpecialistProfileController {
    @FXML private Label fullNameLabel;
    @FXML private Label specializationLabel;
    @FXML private Label clinicLabel;
    @FXML private AnchorPane appointmentsContainer;
    @FXML private VBox appointmentsVBox;

    private Specialist specialist;

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
        System.out.println("Установка врача: " + (specialist != null ? specialist.getName() : "null"));
        updateSpecialistDisplay();
        loadAppointments();
    }

    @FXML
    public void initialize() {
        System.out.println("Инициализация SpecialistProfileController");
        if (appointmentsContainer == null) {
            System.err.println("Ошибка: appointmentsContainer не инициализирован");
        }
        if (appointmentsVBox == null) {
            System.err.println("Ошибка: appointmentsVBox не инициализирован");
        }
    }

    private void updateSpecialistDisplay() {
        if (specialist == null) {
            System.err.println("specialist is null");
            return;
        }
        fullNameLabel.setText("Full name: " + (specialist.getName() != null ? specialist.getName() : "Не указано"));
        specializationLabel.setText("Specialization: " + (specialist.getSpecialization() != null ? specialist.getSpecialization() : "Не указано"));
        try {
            List<Clinic> clinics = FileDataService.loadClinics();
            Clinic clinic = clinics.stream()
                    .filter(c -> c.getId().equals(specialist.getClinicId()))
                    .findFirst()
                    .orElse(null);
            clinicLabel.setText("Clinic: " + (clinic != null ? clinic.getName() : "Не указано"));
        } catch (IOException e) {
            
            clinicLabel.setText("Клиника: Не указано");
        }
    }

    private void loadAppointments() {
        System.out.println("Загрузка записей для врача: " + (specialist != null ? specialist.getName() : "null"));
        if (appointmentsVBox == null) {
            System.err.println("appointmentsVBox не инициализирован!");
            return;
        }
        appointmentsVBox.getChildren().clear();
        try {
            List<Appointment> appointments = FileDataService.loadAppointments()
                    .stream()
                    .filter(app -> specialist != null && specialist.getId() != null && specialist.getId().equals(app.getSpecialistId()))
                    .collect(Collectors.toList());
            System.out.println("Найдено записей: " + appointments.size());
            for (Appointment appointment : appointments) {
                VBox card = createAppointmentCard(appointment);
                if (card != null) {
                    appointmentsVBox.getChildren().add(card);
                    System.out.println("Добавлена карточка записи: " + appointment.getPetName());
                } else {
                    System.err.println("Не удалось создать карточку для записи: " + appointment.getPetName());
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки appointments.txt: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createAppointmentCard(Appointment appointment) {
        try {
            System.out.println("Создание карточки для записи: " + appointment.getPetName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/appointment_card.fxml"));
            VBox card = loader.load();
            AppointmentCardController controller = loader.getController();
            controller.setAppointment(appointment);
            // Не устанавливаем parentController, так как врач не редактирует записи
            return card;
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке appointment_card.fxml: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleLogout() {
        System.out.println("Выход врача: " + (specialist != null ? specialist.getName() : "null"));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/auth.fxml"));
            Parent authRoot = loader.load();
            Stage stage = (Stage) fullNameLabel.getScene().getWindow();
            stage.setScene(new Scene(authRoot));
            stage.setTitle("Авторизация");
            stage.show();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке auth.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}