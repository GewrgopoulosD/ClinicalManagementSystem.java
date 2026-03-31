package controllers;

import javafx.event.ActionEvent;
import ui.WindowManaged;
import ui.WindowManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Doctor;
import services.AdminService;
import java.util.List;

public class AdminDoctorsListController implements WindowManaged {

    @FXML private TableView<Doctor> doctorTable;
    @FXML private TableColumn<Doctor, String> nameCol, lastnameCol, emailCol, phoneCol;
    @FXML private TextField searchField;
    @FXML private CheckBox pendingOnlyCheckBox;
    @FXML private Label selectedDoctorLbl;
    @FXML private ListView<String> currentSpecsList;
    @FXML private ComboBox<String> specializationCombo;
    @FXML private Button assignBtn;
    @FXML private Button refreshBtn;
    @FXML private MenuItem deleteSpecItem;

    private WindowManager windowManager;
    private final AdminService adminService = new AdminService();
    private ObservableList<Doctor> Data = FXCollections.observableArrayList();
    private FilteredList<Doctor> filteredData;
    private Doctor selectedDoctor;

    @Override
    public void setWindowManager(WindowManager wm) {
        this.windowManager = wm;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadDoctors();
        setupFilters();

        //set table view clickable
        doctorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updateDetails(newVal);
            }
        });

        pendingOnlyCheckBox.setOnAction(event -> { //make the combobox functional
            applyFilters();
        });

        assignBtn.setOnAction(event -> {
            handleAssign();
        });

        refreshBtn.setOnAction(event -> {
            loadDoctors();
            applyFilters();
        });

        if (currentSpecsList.getContextMenu() != null) {
            this.deleteSpecItem = currentSpecsList.getContextMenu().getItems().get(0);
        }
        currentSpecsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (deleteSpecItem != null) {
                deleteSpecItem.setDisable(newVal == null);
            }
        });
    }

    private void setupTable() {
        nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        lastnameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLastname()));
        emailCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEmail()));
        phoneCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTelephone()));
    }

    @FXML
    private void loadDoctors() {
        List<Doctor> doctors = adminService.getAllDoctors();
        Data.setAll(doctors);

        specializationCombo.setItems(FXCollections.observableArrayList(adminService.getAllSpecializations()));
    }

    private void setupFilters() {
        filteredData = new FilteredList<>(Data, p -> true);//filter on data

        //for any click run the method applyFilters
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        doctorTable.setItems(filteredData);//set the new data in the initialy data
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        boolean onlyPending = pendingOnlyCheckBox.isSelected();


        filteredData.setPredicate(doctor -> {
            boolean matchesSearch = doctor.getLastname().toLowerCase().contains(searchText) ||
                    doctor.getName().toLowerCase().contains(searchText);

            boolean isPending = doctor.getSpecializations() == null || doctor.getSpecializations().isEmpty();
            boolean matchesPending = !onlyPending || isPending;

            return matchesSearch && matchesPending;
        });
    }

    @FXML
    private void handleFilterPending() {
        applyFilters();
    }

    private void updateDetails(Doctor doctor) {
        this.selectedDoctor = doctor;
        selectedDoctorLbl.setText(doctor.getName() + " " + doctor.getLastname());
        currentSpecsList.getItems().setAll(adminService.getDoctorSpecializations(doctor.getId()));
    }

    @FXML
    private void handleAssign() {
        String spec = specializationCombo.getValue();


        if (selectedDoctor == null) {//is any doctor selected
            alert.AlertView.showError("Error", "No Doctor Selected", "Please select a doctor from the table first.");
            return;
        }

        if (spec == null) {//is any specialization selected from the ComboBox
            alert.AlertView.showError("Error", "No Specialization Selected", "Please select a specialization from the dropdown list.");
            return;
        }

        if (currentSpecsList.getItems().contains(spec)) {//if the doctor has already this specialization
            alert.AlertView.showError("Duplicate Entry", "Assignment Conflict",
                    "Dr. " + selectedDoctor.getLastname() + " is already assigned to: " + spec);
            return;
        }


        boolean success = adminService.assignSpecialization(selectedDoctor.getId(), spec);//registerNewUser it

        if (success) {
            loadDoctors();
            updateDetails(selectedDoctor);//refresh the ui list
            alert.AlertView.showInfo("Success", "Specialization Assigned", "The specialization has been successfully added to the doctor's profile.");
        } else {
            alert.AlertView.showError("Database Error", "Assignment Failed", "An error occurred while saving to the database. Please try again.");
        }
    }

    @FXML
    private void handleRemoveContext(ActionEvent event) {
        String selectedSpecName = currentSpecsList.getSelectionModel().getSelectedItem();

        if (selectedSpecName == null || selectedDoctor == null) return;

        boolean confirmed = alert.AlertView.showConfirmation(
                "Confirm Deletion",
                "Remove Specialization: " + selectedSpecName,
                "Are you sure that you want to remove this?"
        );

        if (confirmed) {
            boolean success = adminService.removeSpecialization(selectedDoctor.getId(), selectedSpecName);

            if (success) {
                loadDoctors();
                updateDetails(selectedDoctor);
                alert.AlertView.showInfo("Success", "Deleted", "Specialization removed successfully.");
            } else {
                alert.AlertView.showError("Error", "Action Failed", "Could not remove specialization.");
            }
        }
    }
}
