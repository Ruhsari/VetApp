package com.example.vetapp.controller;

import com.example.vetapp.model.Clinic;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        if (servicesLabel != null) servicesLabel.setText("Services: " + clinic.getServices());
    }
}