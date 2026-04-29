package services;

import dao.AppointmentDAO;
import dao.MedicalRecordDAO;
import dao.UserDAO;
import models.Appointment;
import models.MedicalRecord;
import models.User;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MedicalRecordService {
    private final MedicalRecordDAO medicalRecordDAO;
    private final AppointmentDAO appointmentDAO;
    private final UserDAO userDAO;

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.appointmentDAO = new AppointmentDAO();
        this.userDAO = new UserDAO();
    }


    //all patien's apoointments
    public List<Appointment> getPatientAppointmentHistory(int idCustomer) {
        List<Appointment> history = appointmentDAO.getAppointmentsByCustomer(idCustomer);
        takerDoctorNames(history);
        return history;
    }

    //record by appointmentId
    public MedicalRecord getDetailsForAppointment(int appointmentId) {
        return medicalRecordDAO.getRecordByAppointmentId(appointmentId);
    }

    public void saveConsultation(MedicalRecord record) {
        medicalRecordDAO.saveOrUpdateRecord(record);
    }

    private void takerDoctorNames(List<Appointment> appointments) {
        if (appointments == null || appointments.isEmpty()) return;
        //take all user in map to have speed
        Map<String, User> usersMap = userDAO.fetchAllUsersAsMap();

        //make a map with key the id
        Map<Integer, User> userById = usersMap.values().stream()
                .filter(u -> u.getRoleId() == 1)
                .collect(Collectors.toMap(User::getId, u -> u, (existing, replacement) -> existing));

        for (Appointment app : appointments) {
            User doc = userById.get(app.getIdEmployee());
            if (doc != null) {
                app.setDoctorFullName(doc.getName() + " " + doc.getLastname());
            } else {
                app.setDoctorFullName("Unknown Doctor");
            }
        }
    }

    public void deleteMedicalRecords(int patientId) {
        List<Appointment> patientApps = appointmentDAO.getAppointmentsByCustomer(patientId);

        List<Integer> appIds = patientApps.stream()
                .map(Appointment::getIdAppointment)
                .collect(Collectors.toList());

        // 3. Αν ο ασθενής έχει ραντεβού, στέλνουμε τα IDs στο DAO για καθαρισμό
        if (!appIds.isEmpty()) {
            medicalRecordDAO.deleteRecordsByAppointmentIds(appIds);
        }
    }
}