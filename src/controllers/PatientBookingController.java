package controllers;

import alert.AlertView;
import dao.AppointmentDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import models.Appointment;
import models.User;
import models.WeeklySchedule;
import services.AppointmentService;
import services.PatientService;
import services.SpecializationService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private final services.ShiftService shiftService = new services.ShiftService();

    @FXML
    public void initialize() {
        setupDoctorComboDisplay();

        specialtyCombo.setItems(FXCollections.observableArrayList(specializationService.getAll()));

        doctorCombo.valueProperty().addListener((o, old, newVal) -> updateAvailableSlots());

        appointmentDatePicker.valueProperty().addListener((o, old, newVal) -> updateAvailableSlots());

        appointmentDatePicker.setDayCellFactory(picker -> new DateCell() {//dont let user choose past dates
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        specialtyCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDoctors(newVal);
            }
        });

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
        doctorCombo.getItems().clear();
        doctorCombo.setPromptText("Select Doctor");

        //get all doctor with specialty
        List<User> allDoctors = patientService.getDoctorsBySpecialty(specialty);

        //filter the list
        List<User> activeDoctors = allDoctors.stream()
                .filter(this::isDoctorAvailable)
                .toList();

        if (activeDoctors.isEmpty()) {
            doctorCombo.setPromptText("No doctors available for this specialty");
        } else {
            doctorCombo.setItems(FXCollections.observableArrayList(activeDoctors));
            doctorCombo.getSelectionModel().clearSelection();
        }
    }

    //check for doctor schedule
    private boolean isDoctorAvailable(User doctor) {
        WeeklySchedule schedule = shiftService.getDoctorSchedule(doctor.getId());

        if (schedule == null || schedule.getDays().isEmpty()) {
            return false;
        }

        //true if at least a day is active
        return schedule.getDays().values().stream()
                .anyMatch(shift -> shift != null && shift.isActive());
    }

    private void updateAvailableSlots() {
        User doctor = doctorCombo.getValue();
        LocalDate date = appointmentDatePicker.getValue();

        //clear combobox
        timeSlotsCombo.getItems().clear();
        timeSlotsCombo.setPromptText("Select Time");

        if (doctor == null || date == null) {
            timeSlotsCombo.setDisable(true);
            return;
        }

        if (doctor != null && date != null) {
            //find the schedule of doc for the day
            WeeklySchedule schedule = shiftService.getDoctorSchedule(doctor.getId());
            String formattedDay = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);

            WeeklySchedule.Shift shift = schedule.getDays().get(formattedDay);

            //if doc doesnt work this day
            if (shift == null || !shift.isActive() || shift.getStart() == null || shift.getEnd() == null) {
                timeSlotsCombo.setPromptText("Doctor not working today");
                timeSlotsCombo.setDisable(true);
                return;
            }

            List<String> allSlots = generateSlots(shift.getStart(), shift.getEnd());

            //filter for time
            if (date.equals(LocalDate.now())) {
                String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                allSlots.removeIf(slot -> slot.compareTo(currentTime) <= 0);
            }

            //delete the reserved slots
            List<String> busySlots = appointmentService.getBusySlots(doctor.getId(), date.toString());
            allSlots.removeAll(busySlots);

            if (allSlots.isEmpty()) {
                timeSlotsCombo.setPromptText("No slots available");
                timeSlotsCombo.setDisable(true);
            } else {
                timeSlotsCombo.setItems(FXCollections.observableArrayList(allSlots));
                timeSlotsCombo.setPromptText("Select Time");
                timeSlotsCombo.setDisable(false);
            }
        }
    }

    private List<String> generateSlots(String startStr, String endStr) {
        List<String> slots = new java.util.ArrayList<>();
        try {
            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);

            while (start.isBefore(end)) {
                slots.add(start.toString());
                start = start.plusHours(1); //every appointment keep an hour
            }
        } catch (Exception e) {
            alert.AlertView.showError(
                    "Schedule Error",
                    "Invalid doctor availability data",
                    "We couldn't load the time slots. Please try another doctor or date."
            );

            //for debug
            System.err.println("--- DEBUG ERROR: generateSlots ---");
            System.err.println("Input Start: '" + startStr + "' | Input End: '" + endStr + "'");
            System.err.println("Error: " + e.getMessage());
            System.err.println("----------------------------------");
        }
        return slots;
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