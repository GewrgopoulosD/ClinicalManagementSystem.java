package services;

import dao.AppointmentDAO;
import dao.UserDAO;
import models.Appointment;
import models.Patient;
import models.User;

import java.util.List;

public class PatientService {

    private final UserDAO userDAO = new UserDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public boolean updateOwnProfile(Patient p, String oldEmail) {
        if (p.getRoleId() != 3) {
            return false;
        }
        userDAO.updateSingleUser(p, oldEmail);
        return true;
    }

    public List<Appointment> getMyAppointments(int patientId) {
        return appointmentDAO.getAppointmentsByCustomer(patientId);
    }

    public List<User> getDoctorsBySpecialty(String specialtyName) {
        if (specialtyName == null || specialtyName.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        return userDAO.findDoctorsBySpecialty(specialtyName);
    }

    public String getDoctorFullNameById(int doctorId) {
        User doc = userDAO.findUserById(doctorId);

        if (doc != null) {
            return "Dr. " + doc.getName() + " " + doc.getLastname();
        }

        return "Unknown Doctor";
    }

}
