package com.example.vetapp.controller;

import com.example.vetapp.model.Specialist;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class SpecialistCardController {
    @FXML private Label nameLabel;
    @FXML private Label specializationLabel;
    @FXML private Button bookButton;

    private Specialist specialist;
    private Runnable onClick;

    public void setSpecialist(Specialist specialist) {
        this.specialist = specialist;
        updateCard();
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    private void updateCard() {
        if (specialist == null) {
            System.err.println("Ошибка: specialist is null!");
            nameLabel.setText("Не указано");
            specializationLabel.setText("Не указано");
            bookButton.setDisable(true);
            return;
        }
        nameLabel.setText(specialist.getName() != null ? specialist.getName() : "Не указано");
        specializationLabel.setText(specialist.getSpecialization() != null ? specialist.getSpecialization() : "Не указано");
        bookButton.setDisable(false);
    }

    @FXML
    private void handleClick(MouseEvent event) {
        if (onClick != null) {
            onClick.run();
        } else {
            System.err.println("Действие для клика по карточке не установлено");
        }
    }

    @FXML
    private void handleBook() {
        if (onClick != null) {
            onClick.run();
            System.out.println("Нажата кнопка Book для специалиста: " + (specialist != null ? specialist.getName() : "null"));
        } else {
            System.err.println("Действие для кнопки Book не установлено");
        }
    }
}