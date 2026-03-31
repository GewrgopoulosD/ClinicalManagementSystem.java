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

    public void createAppointment(Appointment newApp) {
        appointmentDAO.addAppointment(newApp);
    }

    public void updateAppointment(Appointment updatedApp) {
        appointmentDAO.updateAppointment(updatedApp);
    }

    public List<String> getBusySlots(int doctorId, String date) {
        return appointmentDAO.getBusySlots(doctorId, date);
    }

    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> appointments = appointmentDAO.getAppointmentsByCustomer(patientId);

        PatientService patientService = new PatientService();

        for (Appointment app : appointments) {
            //fullname by id
            String fullName = patientService.getDoctorFullNameById(app.getIdEmployee());

            app.setDoctorFullName(fullName);
        }

        return appointments;
    }

    public int getActiveAppointmentsCount(int patientId) {
        return appointmentDAO.getActiveCountByCustomer(patientId);
    }


}