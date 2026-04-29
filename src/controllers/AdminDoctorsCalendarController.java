package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import models.Appointment;
import models.Doctor;
import services.AdminService;
import services.AppointmentService;

import java.time.LocalDate;
import java.util.List;

public class AdminDoctorsCalendarController {

    @FXML private ComboBox<Doctor> doctorSelector;
    @FXML private DatePicker calendarPicker;
    @FXML private Label selectedDateLbl;
    @FXML private Label dayTotalLbl;

    @FXML private TableView<Appointment> calendarTable;
    @FXML private TableColumn<Appointment, String> timeCol;
    @FXML private TableColumn<Appointment, String> patientCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> descCol;

    private final AppointmentService appointmentService = new AppointmentService();
    private final AdminService adminService = new AdminService();

    public void initialize() {
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDatetime"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("customerFullName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));

        formatTimeColumn();

        setupDoctorSelector();

        calendarPicker.valueProperty().addListener((obs, oldDate, newDate) -> refreshData());
        doctorSelector.valueProperty().addListener((obs, oldDoc, newDoc) -> refreshData());

        calendarPicker.setValue(LocalDate.now());

    }

    private void formatTimeColumn() {
        timeCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    //show only time "HH:mm" instead of "yyyy-MM-dd HH:mm"
                    String[] parts = item.split(" ");
                    setText(parts.length > 1 ? parts[1] : item);
                }
            }
        });
    }

    private void setupDoctorSelector() {
        List<Doctor> allDoctors = adminService.getAllDoctors();
        doctorSelector.setItems(FXCollections.observableArrayList(allDoctors));

        //instead of id show doctor's name
        doctorSelector.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor doctor) {
                return (doctor == null) ? "" : doctor.getName() + " " + doctor.getLastname();
            }

            @Override
            public Doctor fromString(String string) {
                return null;
            }
        });
    }

    private void refreshData() {
        Doctor selectedDoctor = doctorSelector.getValue();
        LocalDate selectedDate = calendarPicker.getValue();

        if (selectedDoctor != null && selectedDate != null) {
            String dateStr = selectedDate.toString();
            selectedDateLbl.setText(dateStr);

            List<Appointment> apps = appointmentService.getAppointmentsByDate(selectedDoctor.getId(), dateStr);
            calendarTable.setItems(FXCollections.observableArrayList(apps));
            dayTotalLbl.setText(String.valueOf(apps.size()));
        } else {
            calendarTable.getItems().clear();
            dayTotalLbl.setText("0");
        }
    }

    @FXML
    private void handleGoToToday() {
        calendarPicker.setValue(LocalDate.now());
    }
}