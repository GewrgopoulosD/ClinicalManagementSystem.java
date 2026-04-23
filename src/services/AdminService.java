package services;

import dao.AdminDAO;
import dao.AppointmentDAO;
import dao.SpecializationDAO;
import dao.UserDAO;
import models.Appointment;
import models.Doctor;
import models.Patient;
import models.User;

import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final UserDAO  userDAO = new UserDAO();
    private final SpecializationDAO specializationDAO = new SpecializationDAO();


    public List<Doctor> getPendingDoctors() {
        return adminDAO.getDoctorsWithoutSpecialization();
    }

    public int getTotalDoctorsCount() {
        return adminDAO.getAllDoctors().size();
    }

    public int getTotalPatientsCount() {
        return adminDAO.getTotalPatientsCount();
    }

    public int getTodayAppointmentsCount() {
        return adminDAO.getTodayAppointmentsCount();
    }

    public List<Doctor> getAllDoctors() {
        return adminDAO.getAllDoctors();
    }

    public List<String> getAllSpecializations() {
        return specializationDAO.getAllSpecializations();
    }

    public List<String> getDoctorSpecializations(int doctorId) {
        return adminDAO.getDoctorSpecializations(doctorId);
    }

    public boolean assignSpecialization(int doctorId, String specName) {
        return adminDAO.assignSpecialization(doctorId, specName);
    }

    public boolean removeSpecialization(int doctorId, String specName) {
        return adminDAO.removeSpecialization(doctorId, specName);
    }

    public List<Patient> getAllPatients() {
        return adminDAO.getAllPatients();
    }

    public boolean updatePatient(Patient patient, String oldEmail) {
        try {
            userDAO.updateSingleUser(patient, oldEmail);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Appointment> getPatientHistory(int patientId) {
        AppointmentDAO appointmentDAO = new AppointmentDAO();
        List<Appointment> history = appointmentDAO.getAppointmentsByCustomer(patientId);

        for (Appointment app : history) {
            User doc = userDAO.findUserById(app.getIdEmployee());

            if (doc != null) {
                app.setDoctorFullName(doc.getName() + " " + doc.getLastname());
            } else {
                app.setDoctorFullName("Unknown Doctor");
            }
        }

        return history;
    }

    public boolean deletePatient(String email) {
        try {
            userDAO.deleteUser(email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
