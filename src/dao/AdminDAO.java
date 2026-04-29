package dao;

import models.*;

import java.util.*;
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

    public List<Patient> getAllPatients() {
        return userDAO.fetchAllUsersAsList().stream()
                .filter(u -> u instanceof Patient)
                .map(u -> (Patient) u)
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

    public boolean assignSpecialization(int doctorId, Specialization specialization) {
        //from updatable method
        Map<String, User> userMap = userDAO.fetchAllUsersAsMap();
        boolean updated = false;

        //search the doc
        for (User u : userMap.values()) {
            if (u instanceof Doctor d && d.getId() == doctorId) {

                boolean alreadyHasIt = d.getSpecializations().stream()
                        .anyMatch(s -> s.getIdSpecialization() == specialization.getIdSpecialization());

                if (!alreadyHasIt) {
                    d.getSpecializations().add(specialization);
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

    public boolean removeSpecialization(int doctorId, int specId) {
        Map<String, User> userMap = userDAO.fetchAllUsersAsMap();
        boolean updated = false;

        for (User u : userMap.values()) {
            if (u instanceof Doctor d && d.getId() == doctorId) {
                updated = d.getSpecializations().removeIf(s ->s.getIdSpecialization() == specId);
                break;
            }
        }

        if (updated) {
            userDAO.updateAllUsersData(userMap);
        }
        return updated;
    }

    public List<Specialization> getDoctorSpecializationsObjects(int doctorId) {
        Doctor doctor = getAllDoctors().stream()
                .filter(d -> d.getId() == doctorId)
                .findFirst()
                .orElse(null);

        if (doctor == null || doctor.getSpecializations() == null) {
            return new ArrayList<>();
        }

        return doctor.getSpecializations();
    }


    //statistics
    public Map<String, Long> getTop5DoctorsByCompletedAppointments() {
        List<Appointment> allApps = appointmentDAO.getAllAppointments();
        Map<String, Long> counts = new HashMap<>();

        for (Appointment app : allApps) {
            //only completed apps
            if ("Completed".equalsIgnoreCase(app.getAppointmentType())) {

                String docName = app.getDoctorFullName();

                //find name by ids
                if (docName == null || docName.trim().isEmpty()) {
                    User doctor = userDAO.findUserById(app.getIdEmployee());
                    if (doctor != null) {
                        docName = doctor.getName() + " " + doctor.getLastname();
                    } else {
                        docName = "Unknown Doctor (ID: " + app.getIdEmployee() + ")";
                    }
                }
                counts.put(docName, counts.getOrDefault(docName, 0L) + 1);
            }
        }

        return sortAndLimit(counts);
    }

    public Map<String, Long> getTop5PatientsByCompletedAppointments() {
        List<Appointment> allApps = appointmentDAO.getAllAppointments();
        Map<String, Long> counts = new HashMap<>();

        for (Appointment app : allApps) {
            if ("Completed".equalsIgnoreCase(app.getAppointmentType())) {

                String patientName = app.getCustomerFullName();

                if (patientName == null || patientName.trim().isEmpty()) {
                    User patient = userDAO.findUserById(app.getIdCustomer());
                    if (patient != null) {
                        patientName = patient.getName() + " " + patient.getLastname();
                    } else {
                        patientName = "Unknown Patient (ID: " + app.getIdCustomer() + ")";
                    }
                }
                counts.put(patientName, counts.getOrDefault(patientName, 0L) + 1);
            }
        }

        return sortAndLimit(counts);
    }

    //sorting, limit 5 and order by desc
    private Map<String, Long> sortAndLimit(Map<String, Long> unsortedMap) {
        return unsortedMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}