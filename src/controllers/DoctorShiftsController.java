package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.WeeklySchedule;
import services.ShiftService;
import session.CurrentUser;
import alert.AlertView; // Using your custom Alert class

import java.util.Map;

public class DoctorShiftsController {

    @FXML private CheckBox monCheck, tueCheck, wedCheck, thuCheck, friCheck, satCheck, sunCheck;
    @FXML private ComboBox<String> monStart, tueStart, wedStart, thuStart, friStart, satStart, sunStart;
    @FXML private ComboBox<String> monEnd, tueEnd, wedEnd, thuEnd, friEnd, satEnd, sunEnd;

    @FXML private Button saveShiftsBtn;

    private final ShiftService shiftService = new ShiftService();
    private int doctorId;

    @FXML
    public void initialize() {

        ObservableList<String> timeOptions = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            String hour = String.format("%02d", h);
            timeOptions.add(hour + ":00");
            timeOptions.add(hour + ":30");
        }

        ComboBox<String>[] allCombos = new ComboBox[]{
                monStart, monEnd, tueStart, tueEnd, wedStart, wedEnd,
                thuStart, thuEnd, friStart, friEnd, satStart, satEnd, sunStart, sunEnd
        };

        for (ComboBox<String> cb : allCombos) {
            if (cb != null) cb.setItems(timeOptions);
        }

        addAutoClearListener(monCheck, monStart, monEnd);
        addAutoClearListener(tueCheck, tueStart, tueEnd);
        addAutoClearListener(wedCheck, wedStart, wedEnd);
        addAutoClearListener(thuCheck, thuStart, thuEnd);
        addAutoClearListener(friCheck, friStart, friEnd);
        addAutoClearListener(satCheck, satStart, satEnd);
        addAutoClearListener(sunCheck, sunStart, sunEnd);

        //set doctorId from the current session
        if (CurrentUser.isLoggedIn()) {
            this.doctorId = CurrentUser.getUser().getId();
        }

        //load schedule from json
        WeeklySchedule schedule = shiftService.getDoctorSchedule(doctorId);

        //fill grid
        fillUI(schedule);

        saveShiftsBtn.setOnAction(event -> {
            handleSave();
        });
    }

    @FXML
    private void handleSave() {
        if (!AlertView.showConfirmation("Confirm Save", "Updating Schedule", "Are you sure?")) return;

        WeeklySchedule newSchedule = new WeeklySchedule(doctorId);
        collectData(newSchedule);

        for (Map.Entry<String, WeeklySchedule.Shift> entry : newSchedule.getDays().entrySet()) {
            String dayName = entry.getKey();
            WeeklySchedule.Shift shift = entry.getValue();

            //active without hours
            if (shift.isActive()) {
                if (shift.getStart() == null || shift.getEnd() == null) {
                    AlertView.showError("Validation Error", "Incomplete Data",
                            "Please select times for " + dayName);
                    return;
                }
            }

            //hours without active
            else {
                if (shift.getStart() != null || shift.getEnd() != null) {
                    AlertView.showError("Validation Error", "Inconsistent Selection",
                            "You have selected hours for " + dayName + " but the day is not marked as Active. " +
                                    "Please check the 'Working' box or clear the hours.");
                    return;
                }
            }
        }

        if (shiftService.saveSchedule(newSchedule)) {
            AlertView.showInfo("Success", "Operation Complete", "Schedule updated.");
        }else {
            AlertView.showWarning("Invalid Range", "Time Error", "The start time must be before the end time.");
        }
    }

    //helper methods not to repeat code
    private void fillUI(WeeklySchedule schedule) {
        setDayUI("Monday", schedule, monCheck, monStart, monEnd);
        setDayUI("Tuesday", schedule, tueCheck, tueStart, tueEnd);
        setDayUI("Wednesday", schedule, wedCheck, wedStart, wedEnd);
        setDayUI("Thursday", schedule, thuCheck, thuStart, thuEnd);
        setDayUI("Friday", schedule, friCheck, friStart, friEnd);
        setDayUI("Saturday", schedule, satCheck, satStart, satEnd);
        setDayUI("Sunday", schedule, sunCheck, sunStart, sunEnd);
    }

    private void setDayUI(String day, WeeklySchedule sc, CheckBox cb, ComboBox<String> s, ComboBox<String> e) {
        WeeklySchedule.Shift shift = sc.getDays().get(day);
        if (shift != null) {
            cb.setSelected(shift.isActive());
            s.setValue(shift.getStart());
            e.setValue(shift.getEnd());
        }
    }

    private void collectData(WeeklySchedule schedule) {
        getDayFromUI("Monday", schedule, monCheck, monStart, monEnd);
        getDayFromUI("Tuesday", schedule, tueCheck, tueStart, tueEnd);
        getDayFromUI("Wednesday", schedule, wedCheck, wedStart, wedEnd);
        getDayFromUI("Thursday", schedule, thuCheck, thuStart, thuEnd);
        getDayFromUI("Friday", schedule, friCheck, friStart, friEnd);
        getDayFromUI("Saturday", schedule, satCheck, satStart, satEnd);
        getDayFromUI("Sunday", schedule, sunCheck, sunStart, sunEnd);
    }


    private void getDayFromUI(String day, WeeklySchedule sc, CheckBox cb, ComboBox<String> s, ComboBox<String> e) {
        //make the shift
        WeeklySchedule.Shift newShift = new WeeklySchedule.Shift(
                cb.isSelected(),
                s.getValue(),
                e.getValue()
        );

        sc.getDays().put(day, newShift);
    }

    private void addAutoClearListener(CheckBox cb, ComboBox<String> s, ComboBox<String> e) {
        cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //if active got false
                s.setValue(null);
                e.setValue(null);
            }
        });
    }
}