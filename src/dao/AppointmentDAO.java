package dao;

import jsondatamanager.JsonHandler;
import models.Appointment;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentDAO {
    private static final String FILE_PATH = "data/appointments.json";

    public List<Appointment> getAllAppointments() {
        try {
            Type listType = new TypeToken<List<Appointment>>(){}.getType();
            List<Appointment> appointments = JsonHandler.readList(FILE_PATH, listType);
            return (appointments != null) ? appointments : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        return getAllAppointments().stream()
                .filter(a -> a.getIdEmployee() == doctorId)
                .collect(Collectors.toList());
    }

    public List<Appointment> getTodayAppointments(int doctorId) {
        return getAppointmentsByDoctor(doctorId).stream()
                .filter(Appointment::isToday)
                .collect(Collectors.toList());
    }

    public Appointment getNextAppointment(int doctorId) {
        List<Appointment> todayApps = getTodayAppointments(doctorId);
        String now = java.time.LocalDateTime.now().toString();

        Appointment nextApp = null;

        for (Appointment a : todayApps) {
            if (a.getAppointmentDatetime() == null) continue;

            //is it today but later?
            if (a.getAppointmentDatetime().compareTo(now) > 0) {

                //find the next
                if (nextApp == null || a.getAppointmentDatetime().compareTo(nextApp.getAppointmentDatetime()) < 0) {
                    nextApp = a;
                }
            }
        }

        return nextApp;
    }

    public int getGlobalTodayAppointmentsCount() {
        return (int) getAllAppointments().stream()
                .filter(Appointment::isToday)
                .count();
    }
}