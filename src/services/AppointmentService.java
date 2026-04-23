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
    private final PatientService patientService = new PatientService();

    public List<Appointment> getTodayAppointments(int doctorId) {
        return appointmentDAO.getTodayAppointments(doctorId);
    }

    public List<Appointment> getAllAppointments(int doctorId) {
        return appointmentDAO.getAppointmentsByDoctor(doctorId);
    }

    public Appointment getNextAppointment(int customerId) {
        return appointmentDAO.getPatientNextUpcoming(customerId);
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

    //for patient, to watch his appointment
    public List<Appointment> getAppointmentsForPatient(int patientId) {
        List<Appointment> appointments = appointmentDAO.getAppointmentsByCustomer(patientId);

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

    //doctor appointment dashboard
    //all appointmets (pending, completed, cancelled etc etc) for tableview
    public List<Appointment> getTodayFullSchedule(int doctorId) {
        List<Appointment> apps = appointmentDAO.getTodayAppointmentsFull(doctorId);

        for (Appointment a : apps) {
            String patientName = patientService.getPatientFullNameById(a.getIdCustomer());
            a.setCustomerFullName(patientName);
        }

        return apps;
    }

    //count appointmets (pendings, completted)
    public long getTodayTotalCount(int doctorId) {
        return appointmentDAO.countTodayTotal(doctorId);
    }

    //completed app
    public long getTodayCompletedCount(int doctorId) {
        return appointmentDAO.countTodayCompleted(doctorId);
    }

    //next app today
    public Appointment getNextAppointmentToday(int doctorId) {
        Appointment next = appointmentDAO.nextAppointmentToday(doctorId);
        if (next != null) {
            next.setCustomerFullName(patientService.getPatientFullNameById(next.getIdCustomer()));
        }
        return next;
    }

    //status changer
    public void updateAppointmentStatus(int appointmentId, String newStatus) {
        appointmentDAO.updateStatus(appointmentId, newStatus);
    }

}