package dao;

import jsondatamanager.JsonHandler;
import models.Appointment;
import com.google.gson.reflect.TypeToken;
import models.User;

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
                //only active (pending) appointments for today
                .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING))
                .collect(Collectors.toList());
    }

    public Appointment getNextAppointment(int doctorId) {
        List<Appointment> todayApps = getTodayAppointments(doctorId);
        String now = java.time.LocalDateTime.now().toString();

        Appointment nextApp = null;

        for (Appointment a : todayApps) {
            if (a.getAppointmentDatetime() == null) continue;

            //is it today but later? (and still pending)
            if (a.getAppointmentDatetime().compareTo(now) > 0) {
                //find the closest next appointment
                if (nextApp == null || a.getAppointmentDatetime().compareTo(nextApp.getAppointmentDatetime()) < 0) {
                    nextApp = a;
                }
            }
        }

        return nextApp;
    }

    //method for patient's next upcoming appointment
    public Appointment getNextPendingForCustomer(int customerId) {
        List<Appointment> all = getAllAppointments();
        String now = java.time.LocalDateTime.now().toString();
        Appointment nextApp = null;

        for (Appointment a : all) {
            if (a.getIdCustomer() == customerId &&
                    a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING)) {

                if (a.getAppointmentDatetime() == null) continue;

                //future appointment
                if (a.getAppointmentDatetime().compareTo(now) > 0) {
                    //find the earliest one
                    if (nextApp == null || a.getAppointmentDatetime().compareTo(nextApp.getAppointmentDatetime()) < 0) {
                        nextApp = a;
                    }
                }
            }
        }
        return nextApp;
    }

    public int getGlobalTodayAppointmentsCount() {
        return (int) getAllAppointments().stream()
                .filter(Appointment::isToday)
                //except cancelled and completed from current daily count
                .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING))
                .count();
    }

    public List<Appointment> getAppointmentsByCustomer(int customerId) {
        return getAllAppointments().stream()
                .filter(a -> a.getIdCustomer() == customerId)
                .sorted((a1, a2) -> a2.getAppointmentDatetime().compareTo(a1.getAppointmentDatetime()))
                .collect(Collectors.toList());
    }

    public int getActiveCountByCustomer(int customerId) {
        return (int) getAllAppointments().stream()
                .filter(a -> a.getIdCustomer() == customerId)
                //only count "pending" as active and upcoming visits
                .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING))
                .count();
    }

    private void saveAll(List<Appointment> appointments) throws IOException {
        try {
            JsonHandler.writeList(FILE_PATH, appointments);
        } catch (IOException e) {
            throw new IOException("Failed to write to database file.");
        }
    }

    public void addAppointment(Appointment newApp) {
        try {
            List<Appointment> all = getAllAppointments();

            //auto-increment logic
            int maxId = 0;
            for (Appointment a : all) {
                if (a.getIdAppointment() > maxId) {
                    maxId = a.getIdAppointment();
                }
            }
            newApp.setIdAppointment(maxId + 1);

            all.add(newApp);
            saveAll(all);
        } catch (IOException e) {
            throw new RuntimeException("Could not save the new appointment. Database error.");
        }
    }

    public void updateAppointment(Appointment updatedApp) {
        try {
            List<Appointment> all = getAllAppointments();
            boolean found = false;

            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getIdAppointment() == updatedApp.getIdAppointment()) {
                    all.set(i, updatedApp);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new RuntimeException("Appointment with ID " + updatedApp.getIdAppointment() + " not found.");
            }

            saveAll(all);
        } catch (IOException e) {
            throw new RuntimeException("Technical error: Could not update appointment status.");
        }
    }

    public List<String> getBusySlots(int doctorId, String date) {
        return getAllAppointments().stream()
                .filter(a -> a.getIdEmployee() == doctorId)
                //ignore Cancelled appointments when calculating busy slots
                .filter(a -> !a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_CANCELLED))
                .filter(a -> a.getAppointmentDatetime().startsWith(date))
                .map(a -> {
                    String dt = a.getAppointmentDatetime();
                    return dt.contains(" ") ? dt.split(" ")[1] : dt;
                })
                .collect(Collectors.toList());
    }
}