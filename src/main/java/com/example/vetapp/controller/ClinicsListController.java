package com.example.vetapp.controller;

import com.example.vetapp.model.Clinic;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.vetapp.service.FileDataService;

public class ClinicsListController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<String> locationComboBox;
    @FXML private VBox clinicsContainer;

    private List<Clinic> clinics;
    private List<Clinic> allClinics;
    private ClientMainController parentController;

    public void setParentController(ClientMainController parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void initialize() {
        System.out.println("Инициализация ClinicsListController");
        if (filterComboBox == null) System.err.println("filterComboBox is null");
        if (locationComboBox == null) System.err.println("locationComboBox is null");
        if (searchField == null) System.err.println("searchField is null");
        if (clinicsContainer == null) System.err.println("clinicsContainer is null");

        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList("Все", "По рейтингу", "По расстоянию"));
        }
        if (locationComboBox != null) {
            locationComboBox.setItems(FXCollections.observableArrayList("Все", "Свердловский", "Ленинский", "Чуйский", "Первомайский"));
        }

        try {
            allClinics = FileDataService.loadClinics();
            System.out.println("Клиники загружены: " + allClinics.size() + " элементов");
            clinics = allClinics;
            updateClinicsList();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки клиник: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        System.out.println("Обработка поиска...");
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String filter = filterComboBox.getValue() != null ? filterComboBox.getValue() : "Все";
        String location = locationComboBox.getValue() != null ? locationComboBox.getValue() : "Все";
        System.out.println("Поиск: " + searchText + ", Фильтр: " + filter + ", Район: " + location);

        clinics = allClinics.stream()
                .filter(clinic -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            clinic.getName().toLowerCase().contains(searchText) ||
                            String.join(" ", clinic.getServices()).toLowerCase().contains(searchText);
                    boolean matchesLocation = location.equals("Все") ||
                            clinic.getDistrict().equalsIgnoreCase(location);
                    return matchesSearch && matchesLocation;
                })
                .sorted((clinic1, clinic2) -> {
                    if (filter.equals("Все")) {
                        return 0;
                    } else if (filter.equals("По рейтингу")) {
                        return Double.compare(clinic2.getRating(), clinic1.getRating());
                    } else if (filter.equals("По расстоянию")) {
                        return 0;
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        System.out.println("После фильтрации: " + clinics.size() + " клиник");
        updateClinicsList();
    }

    private void updateClinicsList() {
        System.out.println("Обновление списка клиник...");
        clinicsContainer.getChildren().clear();
        if (clinics == null || clinics.isEmpty()) {
            System.err.println("Список клиник пуст или не загружен");
            return;
        }

        System.out.println("Добавление карточек для " + clinics.size() + " клиник");
        for (Clinic clinic : clinics) {
            VBox card = createClinicCard(clinic);
            if (card != null) {
                clinicsContainer.getChildren().add(card);
            } else {
                System.err.println("Не удалось создать карточку для клиники: " + clinic.getName());
            }
        }
    }

    private VBox createClinicCard(Clinic clinic) {
        try {
            System.out.println("Создание карточки для клиники: " + clinic.getName());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/clinic_card.fxml"));
            VBox card = loader.load();
            ClinicCardController controller = loader.getController();
            controller.setClinic(clinic);
            controller.setParentController(parentController);
            card.setOnMouseClicked(event -> {
                System.out.println("Клик по карточке клиники: " + clinic.getName());
                showClinicDetails(clinic);
            });
            return card;
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке clinic_card.fxml: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void showClinicDetails(Clinic clinic) {
        try {
            System.out.println("Переход к деталям клиники: " + clinic.getName());
            if (parentController == null) {
                System.err.println("parentController не инициализирован");
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/vetapp/view/clinic_details.fxml"));
            Parent detailsContent = loader.load();
            ClinicDetailsController controller = loader.getController();
            controller.setClinic(clinic);
            controller.setUser(parentController.getUser());
            controller.setParentController(parentController);
            parentController.getContentPane().getChildren().setAll(detailsContent);
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке clinic_details.fxml: " + e.getMessage());
            e.printStackTrace();
        }
    }
}