package com.example.vetapp.controller;

import com.example.vetapp.model.Clinic;
import com.example.vetapp.model.Specialist;
import com.example.vetapp.model.User;
import com.example.vetapp.service.FileDataService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;

public class ClinicDetailsController {
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label districtLabel;
    @FXML private Label ratingLabel;
    @FXML private HBox servicesContainer;
    @FXML private VBox specialistsContainer;

    private Clinic clinic;
    private User user;
    private ClientMainController parentController;

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
        updateDetails();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setParentController(ClientMainController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void initialize() {
        System.out.println("Инициализация ClinicDetailsController");
    }

    private void updateDetails() {
        if (clinic == null) {
            showAlert("Ошибка", "Клиника не задана");
            return;
        }
        nameLabel.setText(clinic.getName());
        addressLabel.setText("Address: " + clinic.getAddress());
        districtLabel.setText("District: " + clinic.getDistrict());
        ratingLabel.setText("Rating: " + String.format("%.1f", clinic.getRating()));

        // Populate services as styled labels
        servicesContainer.getChildren().clear();
        if (clinic.getServices() != null && !clinic.getServices().isEmpty()) {
            for (String service : clinic.getServices()) {
                Label serviceLabel = new Label(service);
                serviceLabel.setStyle("-fx-background-color: #FDD96B; -fx-background-radius: 10; -fx-padding: 5 10 5 10; -fx-text-fill: #0a1c40;");
                serviceLabel.setFont(new javafx.scene.text.Font("Poppins Medium", 14));
                servicesContainer.getChildren().add(serviceLabel);
            }
        } else {
            Label noServicesLabel = new Label("Услуги не указаны");
            noServicesLabel.setStyle("-fx-text-fill: #0a1c40;");
            noServicesLabel.setFont(new javafx.scene.text.Font("Poppins Medium", 14));
            servicesContainer.getChildren().add(noServicesLabel);
        }

        loadSpecialists();
    }

    private void loadSpecialists() {
        specialistsContainer.getChildren().clear();
        try {
            List<Specialist> specialists = FileDataService.loadSpecialistsByClinicId(clinic.getId());
            System.out.println("Загружено специалистов для клиники " + clinic.getId() + ": " + specialists.size());
            if (specialists.isEmpty()) {
                showAlert("Информация", "В этой клинике нет специалистов.");
                return;
            }
            for (Specialist specialist : specialists) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/specialist_card.fxml"));
                    Pane cardPane = loader.load();
                    SpecialistCardController controller = loader.getController();
                    controller.setSpecialist(specialist);
                    controller.setOnClick(() -> openAppointmentForm(specialist));
                    specialistsContainer.getChildren().add(cardPane);
                    System.out.println("Добавлена карточка специалиста: " + specialist.getName());
                } catch (IOException e) {
                    showAlert("Ошибка", "Не удалось загрузить карточку специалиста: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось загрузить специалистов: " + e.getMessage());
        }
    }

    private void openAppointmentForm(Specialist specialist) {
        try {
            System.out.println("Открытие формы записи для специалиста: " + specialist.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/appointment_form.fxml"));
            Pane formPane = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setClinic(clinic);
            controller.setUser(user);
            controller.setParentController(parentController);
            controller.setSpecialist(specialist);
            parentController.getContentPane().getChildren().setAll(formPane);
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось открыть форму записи: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookAppointment() {
        try {
            System.out.println("Открытие формы записи для клиники: " + clinic.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/appointment_form.fxml"));
            Pane formPane = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setClinic(clinic);
            controller.setUser(user);
            controller.setParentController(parentController);
            parentController.getContentPane().getChildren().setAll(formPane);
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось открыть форму записи: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        if (parentController != null) {
            parentController.showClinicsList();
        } else {
            showAlert("Ошибка", "Контроллер не инициализирован");
        }
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