package services;

import dao.AdminDAO;
import dao.AppointmentDAO;
import dao.SpecializationDAO;
import dao.UserDAO;
import models.*;

import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO = new AdminDAO();
    private final UserDAO  userDAO = new UserDAO();
    private final  AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final MedicalRecordService medicalRecordService = new MedicalRecordService();
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

    public List<Specialization> getAllSpecializations() {
        return specializationDAO.getAllSpecializationsObjects();
    }

    public List<Specialization> getDoctorSpecializations(int doctorId) {
        return adminDAO.getDoctorSpecializationsObjects(doctorId);
    }

    public boolean assignSpecialization(int doctorId, Specialization spec) {
        if (spec == null) return false;
        return adminDAO.assignSpecialization(doctorId, spec);
    }

    public boolean removeSpecialization(int doctorId, int specId) {
        return adminDAO.removeSpecialization(doctorId, specId);
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

    public boolean deletePatient(String email) throws Exception {
            User user = userDAO.getUserByEmail(email);
                if (user == null) {
                    return false;
                }
            int patientsId = user.getId();


            medicalRecordService.deleteMedicalRecords(patientsId); //medical records delete
            appointmentDAO.deleteAppointmentsByPatientId(patientsId); //app delete

            userDAO.deleteUser(email);

            return true;
    }

    //statistics
    public java.util.Map<String, Long> getTop5DoctorsStats() {
        return adminDAO.getTop5DoctorsByCompletedAppointments();
    }

    public java.util.Map<String, Long> getTop5PatientsStats() {
        return adminDAO.getTop5PatientsByCompletedAppointments();
    }

}
