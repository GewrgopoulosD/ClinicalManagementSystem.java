package controllers;

import javafx.event.ActionEvent;
import models.Specialization;
import services.SpecializationService;
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

    @FXML private ListView<Specialization> currentSpecsList;
    @FXML private ComboBox<Specialization> specializationCombo;

    @FXML private Button assignBtn;
    @FXML private Button refreshBtn;
    @FXML private MenuItem deleteSpecItem;
    @FXML private Button AddNewSpec;

    private WindowManager windowManager;
    private final AdminService adminService = new AdminService();
    private ObservableList<Doctor> Data = FXCollections.observableArrayList();
    private FilteredList<Doctor> filteredData;
    private Doctor selectedDoctor;
    private SpecializationService  specializationService = new SpecializationService();

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

        //combo name
        specializationCombo.setCellFactory(lv -> new ListCell<Specialization>() {
            @Override protected void updateItem(Specialization item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        specializationCombo.setButtonCell(new ListCell<Specialization>() {
            @Override protected void updateItem(Specialization item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        //listview name
        currentSpecsList.setCellFactory(lv -> new ListCell<Specialization>() {
            @Override protected void updateItem(Specialization item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        AddNewSpec.setOnAction(event -> handleAddNewSpecialization());
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

        Specialization spec = specializationCombo.getValue();


        if (selectedDoctor == null) {//is any doctor selected
            alert.AlertView.showError("Error", "No Doctor Selected", "Please select a doctor from the table first.");
            return;
        }

        if (spec == null) {//is any specialization selected from the ComboBox
            alert.AlertView.showError("Error", "No Specialization Selected", "Please select a specialization from the dropdown list.");
            return;
        }

        //if doc has already the spec
        boolean alreadyHasIt = currentSpecsList.getItems().stream()
                .anyMatch(s -> s.getIdSpecialization() == spec.getIdSpecialization());

        if (alreadyHasIt) {
            alert.AlertView.showError("Duplicate Entry", "Assignment Conflict",
                    "Dr. " + selectedDoctor.getLastname() + " is already assigned to: " + spec.getName());
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

        Specialization selectedSpec = currentSpecsList.getSelectionModel().getSelectedItem();

        if (selectedSpec == null || selectedDoctor == null) return;
        boolean confirmed = alert.AlertView.showConfirmation(
                "Confirm Deletion",
                "Remove Specialization: " + selectedSpec.getName(),
                "Are you sure that you want to remove this?"
        );

        if (confirmed) {
            boolean success = adminService.removeSpecialization(selectedDoctor.getId(), selectedSpec.getIdSpecialization());

            if (success) {
                loadDoctors();
                updateDetails(selectedDoctor);
                alert.AlertView.showInfo("Success", "Deleted", "Specialization removed successfully.");
            } else {
                alert.AlertView.showError("Error", "Action Failed", "Could not remove specialization.");
            }
        }
    }

    private void handleAddNewSpecialization() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Specialization");
        dialog.setHeaderText("Create a new medical specialization");
        dialog.setContentText("Please enter the name:");

        dialog.showAndWait().ifPresent(name -> {
            if (name.trim().isEmpty()) {
                alert.AlertView.showError("Error", "Empty Name", "Specialization name cannot be empty.");
                return;
            }

            try {
                if (specializationService.exists(name)) {
                    alert.AlertView.showError("Error", "Duplicate", "This specialization already exists.");
                    return;
                }

                specializationService.add(name);
                loadDoctors();
                alert.AlertView.showInfo("Success", "Specialization Created", "The new specialization has been added to the system.");
            } catch (Exception e) {
                alert.AlertView.showError("Error", "Save Failed", "Could not save the new specialization.");
            }
        });
    }
}
