package com.example.vetapp.controller;

import com.example.vetapp.model.Clinic;
import com.example.vetapp.model.Specialist;
import com.example.vetapp.model.User;
import com.example.vetapp.service.FileDataService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicsListController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<String> locationComboBox;
    @FXML private VBox clinicsContainer;
    @FXML private Label userNameLabel;
    @FXML private Label userStatusLabel;

    private List<Clinic> clinics;
    private List<Clinic> allClinics;
    private List<Specialist> allSpecialists;
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
        if (userNameLabel == null) System.err.println("userNameLabel is null");
        if (userStatusLabel == null) System.err.println("userStatusLabel is null");

        if (filterComboBox != null) {
            filterComboBox.setItems(FXCollections.observableArrayList("All", "by rating", "by distance"));
        }
        if (locationComboBox != null) {
            locationComboBox.setItems(FXCollections.observableArrayList("All", "Sverdlovsky", "Leninsky", "Chuysky", "Pervomaysky", "Oktyabrsky"));
        }

        try {
            allClinics = FileDataService.loadClinics();
            allSpecialists = FileDataService.loadSpecialists();
            System.out.println("Клиники загружены: " + allClinics.size() + " элементов");
            System.out.println("Специалисты загружены: " + allSpecialists.size() + " элементов");
            clinics = allClinics;
            updateClinicsList();
        } catch (IOException e) {
            System.err.println("Ошибка загрузки данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearch() {
        System.out.println("Обработка поиска...");
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        String filter = filterComboBox.getValue() != null ? filterComboBox.getValue() : "All";
        String location = locationComboBox.getValue() != null ? locationComboBox.getValue() : "All";
        System.out.println("Поиск: " + searchText + ", Фильтр: " + filter + ", Район: " + location);

        clinics = allClinics.stream()
                .filter(clinic -> location.equals("All") || clinic.getDistrict().equalsIgnoreCase(location))
                .filter(clinic -> {
                    if (searchText.isEmpty()) {
                        return true;
                    }
                    boolean matchesName = clinic.getName().toLowerCase().contains(searchText);
                    boolean matchesService = clinic.getServices().stream()
                            .anyMatch(service -> service.toLowerCase().contains(searchText));
                    boolean matchesSpecialist = allSpecialists.stream()
                            .filter(spec -> spec.getClinicId().equals(String.valueOf(clinic.getId())))
                            .anyMatch(spec -> spec.getName().toLowerCase().contains(searchText));
                    return matchesName || matchesService || matchesSpecialist;
                })
                .sorted((clinic1, clinic2) -> {
                    if (filter.equals("by rating")) {
                        return Double.compare(clinic2.getRating(), clinic1.getRating());
                    } else if (filter.equals("by distance")) {
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