package com.example.vetapp.controller;

import com.example.vetapp.model.Appointment;
import com.example.vetapp.model.Clinic;
import com.example.vetapp.model.Specialist;
import com.example.vetapp.model.Schedule;
import com.example.vetapp.model.User;
import com.example.vetapp.service.FileDataService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentFormController {
    @FXML private TextField petNameField;
    @FXML private TextField petTypeField;
    @FXML private ComboBox<String> dateComboBox;
    @FXML private ComboBox<Specialist> specialistComboBox;
    @FXML private ComboBox<String> timeComboBox;
    @FXML private TextField reasonField;

    private Clinic clinic;
    private User user;
    private ClientMainController parentController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter STORAGE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
        loadSpecialists();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setParentController(ClientMainController parentController) {
        this.parentController = parentController;
    }

    public void setSpecialist(Specialist specialist) {
        if (specialist != null && specialistComboBox.getItems().contains(specialist)) {
            specialistComboBox.setValue(specialist);
            handleSpecialistSelection();
            System.out.println("Установлен специалист: " + specialist.getName());
        } else {
            System.out.println("Специалист не найден в списке: " + (specialist != null ? specialist.getName() : "null"));
        }
    }

    @FXML
    private void initialize() {
        // Настраиваем ComboBox для отображения специалистов
        specialistComboBox.setConverter(new StringConverter<Specialist>() {
            @Override
            public String toString(Specialist specialist) {
                return specialist != null ? specialist.getName() + " (" + specialist.getSpecialization() + ")" : "";
            }

            @Override
            public Specialist fromString(String string) {
                return null; // Не используется
            }
        });

        // Настраиваем ComboBox для отображения дат
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

        specialistComboBox.setOnAction(event -> handleSpecialistSelection());
        dateComboBox.setOnAction(event -> handleDateSelection());
    }

    private void loadSpecialists() {
        specialistComboBox.getItems().clear();
        try {
            List<Specialist> specialists = FileDataService.loadSpecialistsByClinicId(clinic.getId());
            specialistComboBox.setItems(FXCollections.observableArrayList(specialists));
            System.out.println("Загружено специалистов для клиники " + clinic.getId() + ": " + specialists.size());
            if (specialists.isEmpty()) {
                showAlert("Информация", "В этой клинике нет специалистов.");
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить специалистов: " + e.getMessage());
        }
    }

    @FXML
    private void handleSpecialistSelection() {
        Specialist selectedSpecialist = specialistComboBox.getValue();
        dateComboBox.getItems().clear();
        timeComboBox.getItems().clear();

        if (selectedSpecialist == null) {
            return;
        }

        try {
            List<String> availableDates = FileDataService.loadSchedules().stream()
                    .filter(s -> s.getSpecialistId().equals(selectedSpecialist.getId()))
                    .map(Schedule::getDate)
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            dateComboBox.setItems(FXCollections.observableArrayList(availableDates));
            System.out.println("Загружено дат для специалиста " + selectedSpecialist.getId() + ": " + availableDates.size());
            if (availableDates.isEmpty()) {
                showAlert("Информация", "Нет доступных дат для этого специалиста.");
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить расписание: " + e.getMessage());
        }
    }

    @FXML
    private void handleDateSelection() {
        Specialist selectedSpecialist = specialistComboBox.getValue();
        String selectedDate = dateComboBox.getValue();
        timeComboBox.getItems().clear();

        if (selectedSpecialist == null || selectedDate == null) {
            return;
        }

        try {
            List<String> availableTimes = FileDataService.loadSchedules().stream()
                    .filter(s -> s.getSpecialistId().equals(selectedSpecialist.getId()) &&
                            s.getDate().equals(selectedDate))
                    .map(Schedule::getTime)
                    .sorted()
                    .collect(Collectors.toList());
            timeComboBox.setItems(FXCollections.observableArrayList(availableTimes));
            System.out.println("Загружено времени для специалиста " + selectedSpecialist.getId() +
                    " на дату " + selectedDate + ": " + availableTimes.size());
            if (availableTimes.isEmpty()) {
                showAlert("Информация", "Нет доступного времени на выбранную дату.");
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить расписание: " + e.getMessage());
        }
    }

    @FXML
    private void handleSubmit() {
        String petName = petNameField.getText().trim();
        String petType = petTypeField.getText().trim();
        String date = dateComboBox.getValue();
        String time = timeComboBox.getValue();
        Specialist selectedSpecialist = specialistComboBox.getValue();
        String reason = reasonField.getText().trim();

        if (petName.isEmpty() || petType.isEmpty() || date == null || time == null ||
                selectedSpecialist == null || reason.isEmpty()) {
            showAlert("Ошибка", "Заполните все поля!");
            return;
        }

        try {
            Appointment appointment = new Appointment(
                    user.getUsername(),
                    clinic.getId(),
                    selectedSpecialist.getId(),
                    petName,
                    petType,
                    date,
                    time,
                    reason
            );
            FileDataService.saveAppointment(appointment);
            showAlert("Успех", "Запись успешно создана!");
            parentController.showClinicsList();
        } catch (IOException e) {
            showAlert("Ошибка", "Ошибка при сохранении записи: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        parentController.showClinicsList();
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