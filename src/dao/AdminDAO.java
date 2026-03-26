package dao;

import models.Doctor;
import models.Patient;
import models.Specialization; // Μην ξεχάσεις το import
import models.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminDAO {

    private final UserDAO userDAO = new UserDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public List<Doctor> getAllDoctors() {
        return userDAO.fetchAllUsersAsList().stream()
                .filter(u -> u instanceof Doctor)
                .map(u -> (Doctor) u)
                .collect(Collectors.toList());
    }

    public int getTotalPatientsCount() {
        return (int) userDAO.fetchAllUsersAsList().stream()
                .filter(u -> u instanceof Patient)
                .count();
    }

    public int getTodayAppointmentsCount() {
        return appointmentDAO.getGlobalTodayAppointmentsCount();
    }

    public List<Doctor> getDoctorsWithoutSpecialization() {
        return getAllDoctors().stream()
                .filter(d -> d.getSpecializations() == null || d.getSpecializations().isEmpty())
                .collect(Collectors.toList());
    }

    public boolean assignSpecialization(int doctorId, String specName) {
        //from updatable method
        Map<String, User> userMap = userDAO.fetchAllUsersAsMap();
        boolean updated = false;

        //search the doc
        for (User u : userMap.values()) {
            if (u instanceof Doctor d && d.getId() == doctorId) {

                Specialization newSpec = new Specialization(specName);

                boolean alreadyHasIt = d.getSpecializations().stream()
                        .anyMatch(s -> s.getName().equalsIgnoreCase(specName));

                if (!alreadyHasIt) {
                    d.getSpecializations().add(newSpec);
                    updated = true;
                }
                break;
            }
        }

        if (updated) {
            userDAO.updateAllUsersData(userMap);
        }

        return updated;
    }

    public List<String> getDoctorSpecializations(int doctorId) {
        Doctor doctor = getAllDoctors().stream()
                .filter(d -> d.getId() == doctorId)
                .findFirst()
                .orElse(null);

        if (doctor == null || doctor.getSpecializations() == null) {
            return new ArrayList<>();
        }

        return doctor.getSpecializations().stream()
                .map(Specialization::getName)
                .collect(Collectors.toList());
    }
}