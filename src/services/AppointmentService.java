package services;

import DAO.AppointmentDAO;
import models.Appointment;
import models.Patient;

import java.util.List;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    public List<Appointment> getTodayAppointments(int doctorId) {
        return appointmentDAO.getTodayAppointments(doctorId);
    }

    public List<Appointment> getAllAppointments(int doctorId) {
        return appointmentDAO.getAllAppointments(doctorId);
    }

    public Appointment getNextAppointment(int doctorId) {
        return appointmentDAO.getNextAppointment(doctorId);
    }

    public int getTotalAppointments(int doctorId) {
        return appointmentDAO.getTotalAppointments(doctorId);
    }

    public List<Patient> getAllPatients() {
        return appointmentDAO.getAllPatients();
    }

    public List<Patient> getDoctorPatients(int doctorId) {
        return appointmentDAO.getDoctorPatients(doctorId);
    }

}