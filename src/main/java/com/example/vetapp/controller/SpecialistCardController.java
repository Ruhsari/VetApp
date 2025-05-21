package com.example.vetapp.controller;

import com.example.vetapp.model.Specialist;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class SpecialistCardController {
    @FXML private Label nameLabel;
    @FXML private Label specializationLabel;

    private Specialist specialist;
    private Runnable onClick;

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
        nameLabel.setText(specialist.getName());
        specializationLabel.setText(specialist.getSpecialization());
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @FXML
    private void handleClick(MouseEvent event) {
        if (onClick != null) {
            onClick.run();
        }
    }
}