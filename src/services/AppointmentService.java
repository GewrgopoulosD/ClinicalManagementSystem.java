package services;

import dao.AppointmentDAO;
import dao.UserDAO;
import models.Appointment;
import models.Patient;
import models.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AppointmentService {

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final UserDAO userDAO = new UserDAO();

    public List<Appointment> getTodayAppointments(int doctorId) {
        return appointmentDAO.getTodayAppointments(doctorId);
    }

    public List<Appointment> getAllAppointments(int doctorId) {
        return appointmentDAO.getAppointmentsByDoctor(doctorId);
    }

    public Appointment getNextAppointment(int doctorId) {
        return appointmentDAO.getNextAppointment(doctorId);
    }

    public int getTotalAppointmentsCount(int doctorId) {
        return appointmentDAO.getAppointmentsByDoctor(doctorId).size();
    }


}