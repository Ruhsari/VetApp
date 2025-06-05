package com.example.vetapp.controller;

import com.example.vetapp.model.Clinic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;

public class ClinicCardController {
    @FXML private Label nameLabel;
    @FXML private Label addressLabel;
    @FXML private Label ratingLabel;
    @FXML private Label servicesLabel;

    private Clinic clinic;
    private ClientMainController parentController;

    public void setClinic(Clinic clinic) {
        this.clinic = clinic;
        updateDisplay();
    }

    public void setParentController(ClientMainController parentController) {
        this.parentController = parentController;
    }

    private void updateDisplay() {
        if (nameLabel != null) nameLabel.setText(clinic.getName());
        if (addressLabel != null) addressLabel.setText("Address: " + clinic.getAddress());
        if (ratingLabel != null) ratingLabel.setText("Rating: " + clinic.getRating());
        if (servicesLabel != null) servicesLabel.setText("Services: " + String.join(", ", clinic.getServices()));
    }

    @FXML
    private void handleMakeAppointment() {
        try {
            System.out.println("Открытие формы записи для клиники: " + clinic.getName());
            if (parentController == null) {
                System.err.println("parentController не инициализирован");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/appointment_form.fxml"));
            Parent formContent = loader.load();
            AppointmentFormController controller = loader.getController();
            controller.setClinic(clinic);
            controller.setUser(parentController.getUser());
            controller.setParentController(parentController);
            parentController.getContentPane().getChildren().setAll(formContent);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке appointment_form.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}