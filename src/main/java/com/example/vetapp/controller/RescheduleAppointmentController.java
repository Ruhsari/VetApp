package com.example.vetapp.controller;

import com.example.vetapp.model.Appointment;
import com.example.vetapp.model.Schedule;
import com.example.vetapp.model.Specialist;
import com.example.vetapp.service.FileDataService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RescheduleAppointmentController {
    @FXML private Label petNameLabel;
    @FXML private Label petTypeLabel;
    @FXML private Label specialistLabel;
    @FXML private Label reasonLabel;
    @FXML private ComboBox<String> dateComboBox;
    @FXML private ComboBox<String> timeComboBox;

    private Appointment appointment;
    private ProfileController parentController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        updateDetails();
        loadAvailableDates();
    }

    public void setParentController(ProfileController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void initialize() {
        dateComboBox.setConverter(new StringConverter<String>() {
            @Override
            public String toString(String date) {
                if (date == null) return "";
                try {
                    LocalDate localDate = LocalDate.parse(date, STORAGE_FORMATTER);
                    return localDate.format(DATE_FORMATTER);
                } catch (Exception e) {
                    return date;
                }
            }

            @Override
            public String fromString(String string) {
                if (string == null) return null;
                try {
                    LocalDate localDate = LocalDate.parse(string, DATE_FORMATTER);
                    return localDate.format(STORAGE_FORMATTER);
                } catch (Exception e) {
                    return string;
                }
            }
        });

        dateComboBox.setOnAction(event -> handleDateSelection());
    }

    private void updateDetails() {
        petNameLabel.setText("Pet name: " + appointment.getPetName());
        petTypeLabel.setText("Type: " + appointment.getPetType());
        try {
            List<Specialist> specialists = FileDataService.loadSpecialistsByClinicId(appointment.getClinicId());
            Specialist specialist = specialists.stream()
                    .filter(s -> s.getId().equals(appointment.getSpecialistId()))
                    .findFirst()
                    .orElse(null);
            specialistLabel.setText("Specialist: " + (specialist != null ? specialist.getName() + " (" + specialist.getSpecialization() + ")" : "ID " + appointment.getSpecialistId()));
        } catch (IOException e) {
            specialistLabel.setText("Specialist: ID " + appointment.getSpecialistId());
            System.err.println("Ошибка при загрузке специалиста: " + e.getMessage());
        }
        reasonLabel.setText("Visit reason: " + appointment.getReason());
    }

    private void loadAvailableDates() {
        dateComboBox.getItems().clear();
        timeComboBox.getItems().clear();
        try {
            List<String> availableDates = FileDataService.loadSchedules().stream()
                    .filter(s -> s.getSpecialistId().equals(appointment.getSpecialistId()))
                    .map(Schedule::getDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            dateComboBox.setItems(FXCollections.observableArrayList(availableDates));
            System.out.println("Загружено дат для специалиста " + appointment.getSpecialistId() + ": " + availableDates.size());
            if (availableDates.isEmpty()) {
                showAlert("Information", "There are no available dates for this specialist..");
            }
        } catch (IOException e) {
            showAlert("Error", "Unable to load schedule: " + e.getMessage());
        }
    }

    @FXML
    private void handleDateSelection() {
        String selectedDate = dateComboBox.getValue();
        timeComboBox.getItems().clear();
        if (selectedDate == null) {
            return;
        }
        try {
            List<String> availableTimes = FileDataService.loadSchedules().stream()
                    .filter(s -> s.getSpecialistId().equals(appointment.getSpecialistId()) &&
                            s.getDate().equals(selectedDate))
                    .map(Schedule::getTime)
                    .sorted()
                    .collect(Collectors.toList());
            timeComboBox.setItems(FXCollections.observableArrayList(availableTimes));
            System.out.println("Загружено времени для специалиста " + appointment.getSpecialistId() +
                    " на дату " + selectedDate + ": " + availableTimes.size());
            if (availableTimes.isEmpty()) {
                showAlert("Information", "There is no time available for the selected date.");
            }
        } catch (IOException e) {
            showAlert("Error", "Unable to load schedule: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        String newDate = dateComboBox.getValue();
        String newTime = timeComboBox.getValue();
        if (newDate == null || newTime == null) {
            showAlert("Error", "Выберите дату и время!");
            return;
        }
        try {
            // Удаляем старую запись
            FileDataService.deleteAppointment(appointment);
            // Возвращаем старый слот в расписание
            FileDataService.addSchedule(new Schedule(appointment.getSpecialistId(), appointment.getDate(), appointment.getTime()));
            // Создаём новую запись
            Appointment newAppointment = new Appointment(
                    appointment.getUsername(),
                    appointment.getClinicId(),
                    appointment.getSpecialistId(),
                    appointment.getPetName(),
                    appointment.getPetType(),
                    newDate,
                    newTime,
                    appointment.getReason()
            );
            FileDataService.saveAppointment(newAppointment);
            showAlert("Success", "Appointment successfully rescheduled!");
            parentController.showProfile();
        } catch (IOException e) {
            showAlert("Error", "Error when transferring an appointment: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        parentController.showProfile();
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = message.contains("Ошибка") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}