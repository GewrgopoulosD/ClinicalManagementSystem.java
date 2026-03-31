package controllers;

import alert.AlertView;
import dao.AppointmentDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import models.Appointment;
import models.User;
import services.AppointmentService;
import services.PatientService;
import services.SpecializationService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class PatientBookingController {

    @FXML private ComboBox<String> specialtyCombo;
    @FXML private ComboBox<User> doctorCombo;
    @FXML private DatePicker appointmentDatePicker;
    @FXML private ComboBox<String> timeSlotsCombo;
    @FXML private TextArea commentsArea;

    private final PatientService patientService = new PatientService();
    private final SpecializationService specializationService = new SpecializationService();
    private final AppointmentService appointmentService = new AppointmentService();

    @FXML
    public void initialize() {
        setupDoctorComboDisplay();

        specialtyCombo.setItems(FXCollections.observableArrayList(specializationService.getAll()));

        specialtyCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDoctors(newVal);
            }
        });

        doctorCombo.valueProperty().addListener((o, old, newVal) -> updateAvailableSlots());
        appointmentDatePicker.valueProperty().addListener((o, old, newVal) -> updateAvailableSlots());
    }

    private void setupDoctorComboDisplay() {
        doctorCombo.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return (user == null) ? "Select Doctor" : user.getFullname();
            }
            @Override
            public User fromString(String string) { return null; }
        });
    }

    private void loadDoctors(String specialty) {
        List<User> doctors = patientService.getDoctorsBySpecialty(specialty);
        doctorCombo.setItems(FXCollections.observableArrayList(doctors));
    }

    private void updateAvailableSlots() {
        User doctor = doctorCombo.getValue();
        LocalDate date = appointmentDatePicker.getValue();

        if (doctor != null && date != null) {
            List<String> allSlots = new java.util.ArrayList<>(Arrays.asList(
                    "08:00", "09:00", "10:00", "11:00", "12:00",
                    "13:00", "14:00", "17:00", "18:00", "19:00", "20:00"
            ));

            List<String> busySlots = appointmentService.getBusySlots(doctor.getId(), date.toString());

            allSlots.removeAll(busySlots);

            timeSlotsCombo.setItems(FXCollections.observableArrayList(allSlots));
            timeSlotsCombo.setDisable(false);

            if(allSlots.isEmpty()) {
                timeSlotsCombo.setPromptText("No slots available");
            }
        } else {
            timeSlotsCombo.setDisable(true);
            timeSlotsCombo.getItems().clear();
        }
    }

    @FXML
    private void handleBooking() {
        User doctor = doctorCombo.getValue();
        LocalDate date = appointmentDatePicker.getValue();
        String time = timeSlotsCombo.getValue();
        String comments = commentsArea.getText();

        if (doctor == null || date == null || time == null) {
            alert.AlertView.showWarning("Missing Data", "Please fill these fields!",
                    "Doctor, Date and Time to book an appointment.");
            return;
        }

        boolean confirmed = alert.AlertView.showConfirmation("Confirm Booking",
                "Are you sure?",
                "You are about to book an appointment with " + doctor.getFullname() + ".");

        if (!confirmed) return;

        try {
            Appointment newApp = new Appointment();
            if (session.CurrentUser.getUser() != null) {
                newApp.setIdCustomer(session.CurrentUser.getUser().getId());
            } else {
                alert.AlertView.showError("Session Error", "No user found", "Please login again.");
                return;
            }
            newApp.setIdClinic(1);
            newApp.setIdEmployee(doctor.getId());
            newApp.setAppointmentDatetime(date.toString() + " " + time);
            newApp.setAppointmentType(Appointment.STATUS_PENDING);
            newApp.setAppointmentDescription(comments);

            appointmentService.createAppointment(newApp);

            alert.AlertView.showInfo("Success!", "Booking Confirmed",
                    "Your appointment has been saved successfully.");

            clearFields();

        } catch (Exception e) {
            alert.AlertView.showError("Database Error", "Something went wrong", e.getMessage());
        }
    }

    private void clearFields() {
        doctorCombo.getSelectionModel().clearSelection();
        appointmentDatePicker.setValue(null);
        timeSlotsCombo.getItems().clear();
        timeSlotsCombo.setDisable(true);
        commentsArea.clear();
    }
}