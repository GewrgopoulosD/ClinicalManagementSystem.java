package services;

import dao.AppointmentDAO;
import dao.UserDAO;
import models.Appointment;
import models.Patient;
import models.User;
import java.util.List;
import java.util.Map;
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

    public void createAppointment(Appointment newApp) throws Exception {
        String[] parts = newApp.getAppointmentDatetime().split(" ");
        String date = parts[0];
        String time = parts[1];

        //check if the time is available
        List<String> busySlots = appointmentDAO.getBusySlots(newApp.getIdEmployee(), date);

        if (busySlots.contains(time)) {
            throw new Exception("This slot was just booked by someone else!");
        }

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

    public void deleteAppointments(int patientId) {
        appointmentDAO.deleteAppointmentsByPatientId(patientId);
    }

    //myCalendar
    public List<Appointment> getAppointmentsByDate(int doctorId, String date) {
        //daily app
        List<Appointment> apps = appointmentDAO.getAppointmentsByDoctorAndDate(doctorId, date);

        //+ patiens names
        for (Appointment a : apps) {
            String patientName = patientService.getPatientFullNameById(a.getIdCustomer());
            a.setCustomerFullName(patientName);
        }

        return apps;
    }

    public Map<String, Long> getTop3DoctorsStats(int patientId) {
        return appointmentDAO.getTop3DoctorsForPatient(patientId);
    }
}