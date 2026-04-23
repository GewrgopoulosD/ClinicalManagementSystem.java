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
    private static final java.time.format.DateTimeFormatter FORMATTER =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); //formated date time for comparator

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

    //method for patient's next upcoming appointment
    public Appointment getPatientNextUpcoming(int customerId) {
            String now = java.time.LocalDateTime.now().format(FORMATTER);

            return getAllAppointments().stream()
                    .filter(a -> a.getIdCustomer() == customerId)
                    .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING))
                    .filter(a -> a.getAppointmentDatetime() != null)
                    .filter(a -> a.getAppointmentDatetime().compareTo(now) > 0)
                    .min(Comparator.comparing(Appointment::getAppointmentDatetime))
                    .orElse(null);
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
        String now = java.time.LocalDateTime.now().format(FORMATTER);
        return (int) getAllAppointments().stream()
                .filter(a -> a.getIdCustomer() == customerId)
                .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING))
                .filter(a -> a.getAppointmentDatetime() != null && a.getAppointmentDatetime().compareTo(now) >= 0)
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
            throw new RuntimeException("Could not saveSchedule the new appointment. Database error.");
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

    //method for doctor to watch how many appointments is waiting for today
    public long countTodayTotal(int doctorId) {
        String today = java.time.LocalDate.now().toString(); // "yyyy-MM-dd"
        return getAllAppointments().stream()
                .filter(a -> a.getIdEmployee() == doctorId)
                .filter(a -> a.getAppointmentDatetime() != null && a.getAppointmentDatetime().startsWith(today))
                .filter(a -> !a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_CANCELLED))
                .count();
    }

    //for doctor how many appointmets has done today
    public long countTodayCompleted(int doctorId) {
        String today = java.time.LocalDate.now().toString();
        return getAllAppointments().stream()
                .filter(a -> a.getIdEmployee() == doctorId)
                .filter(a -> a.getAppointmentDatetime() != null && a.getAppointmentDatetime().startsWith(today))
                .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_COMPLETED))
                .count();
    }

    //for doctor to watch what and what time is the nexr app
    public Appointment nextAppointmentToday(int doctorId) {
        String today = java.time.LocalDate.now().toString();
        String now = java.time.LocalDateTime.now().format(FORMATTER);

        return getAllAppointments().stream()
                .filter(a -> a.getIdEmployee() == doctorId)
                .filter(a -> a.getAppointmentType().equalsIgnoreCase(Appointment.STATUS_PENDING))
                .filter(a -> a.getAppointmentDatetime() != null)
                .filter(a -> a.getAppointmentDatetime().startsWith(today))
                .filter(a -> a.getAppointmentDatetime().compareTo(now) > 0) //only future (but today)
                .min(Comparator.comparing(Appointment::getAppointmentDatetime))//the first
                .orElse(null);
    }

    //for doctor, he will update the status of appointment (xompleted - cancelled)
    public void updateStatus(int appointmentId, String newStatus) {
        try {
            List<Appointment> all = getAllAppointments();
            if (all == null || all.isEmpty()) {
                throw new RuntimeException("Database is empty. Cannot update status.");
            }

            boolean found = false;
            for (Appointment a : all) {
                if (a.getIdAppointment() == appointmentId) {
                    a.setAppointmentType(newStatus);
                    found = true;
                    break;
                }
            }

            if (found) {
                saveAll(all);
            } else {
                //not found id
                throw new RuntimeException("Appointment with ID " + appointmentId + " not found.");
            }
        } catch (IOException e) {
            //json error
            throw new RuntimeException("Database Error: Could not update status. " + e.getMessage());
        }
    }

    //for doctor, all appointments (not only pending)
    public List<Appointment> getTodayAppointmentsFull(int doctorId) {
        String today = java.time.LocalDate.now().toString();
        return getAppointmentsByDoctor(doctorId).stream()
                .filter(a -> a.getAppointmentDatetime() != null && a.getAppointmentDatetime().startsWith(today))
                .sorted(Comparator.comparing(Appointment::getAppointmentDatetime))
                .collect(Collectors.toList());
    }
}