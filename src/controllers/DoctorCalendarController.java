package controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.Doctor;
import services.AppointmentService;
import session.CurrentUser;

import java.time.LocalDate;
import java.util.List;

public class DoctorCalendarController {

    @FXML private DatePicker calendarPicker;
    @FXML private Label selectedDateLbl;
    @FXML private Label dayTotalLbl;

    @FXML private TableView<Appointment> calendarTable;
    @FXML private TableColumn<Appointment, String> timeCol;
    @FXML private TableColumn<Appointment, String> patientCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> descCol;

    @FXML private Button cancelBtn;

    private final AppointmentService appointmentService = new AppointmentService();
    private int currentDoctorId;

    public void initialize() {

        Doctor doctor = (Doctor) CurrentUser.getUser();
        currentDoctorId = doctor.getId();

        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDatetime"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("customerFullName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));

        formatTimeColumn();

        //datepicker listener
        calendarPicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                loadDayData(newDate);
            }
        });
        calendarPicker.setValue(LocalDate.now());

        cancelBtn.disableProperty().bind(
                calendarTable.getSelectionModel().selectedItemProperty().isNull().or(
                        Bindings.createBooleanBinding(() -> {
                            Appointment selected = calendarTable.getSelectionModel().getSelectedItem();
                            return selected != null && !selected.getAppointmentType().equalsIgnoreCase("Pending");
                        }, calendarTable.getSelectionModel().selectedItemProperty())
                )
        );
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

    private void loadDayData(LocalDate date) {
        String dateStr = date.toString(); //"yyyy-MM-dd"

        selectedDateLbl.setText(dateStr);

        List<Appointment> apps = appointmentService.getAppointmentsByDate(currentDoctorId, dateStr);

        calendarTable.setItems(FXCollections.observableArrayList(apps));

        dayTotalLbl.setText(String.valueOf(apps.size()));
    }

    @FXML
    private void handleGoToToday() {
        calendarPicker.setValue(LocalDate.now());
    }


    @FXML
    private void handleCancelApp() {
        Appointment selected = calendarTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Appointment");
        alert.setHeaderText("Are you sure you want to cancel this appointment?");
        alert.setContentText("Patient: " + selected.getCustomerFullName() + "\nTime: " + selected.getAppointmentDatetime());

        if (alert.showAndWait().get() == ButtonType.OK) {
            appointmentService.updateAppointmentStatus(selected.getIdAppointment(), Appointment.STATUS_CANCELLED);
            loadDayData(calendarPicker.getValue());
        }
    }
}