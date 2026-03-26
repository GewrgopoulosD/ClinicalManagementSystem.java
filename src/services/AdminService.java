package services;

import dao.AdminDAO;
import dao.SpecializationDAO;
import models.Doctor;
import java.util.List;

public class AdminService {

    private final AdminDAO adminDAO = new AdminDAO();
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
}
