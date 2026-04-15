package services;

import dao.AppointmentDAO;
import dao.MedicalRecordDAO;
import models.Appointment;
import models.MedicalRecord;

import java.util.List;

public class MedicalRecordService {
    private final MedicalRecordDAO medicalRecordDAO;
    private final AppointmentDAO appointmentDAO;

    public MedicalRecordService() {
        this.medicalRecordDAO = new MedicalRecordDAO();
        this.appointmentDAO = new AppointmentDAO();
    }


    //all patien's apoointments
    public List<Appointment> getPatientAppointmentHistory(int idCustomer) {
        return appointmentDAO.getAppointmentsByCustomer(idCustomer);
    }

    //record by appointmentId
    public MedicalRecord getDetailsForAppointment(int appointmentId) {
        return medicalRecordDAO.getRecordByAppointmentId(appointmentId);
    }

    public void saveConsultation(MedicalRecord record) {
        medicalRecordDAO.saveOrUpdateRecord(record);
    }
}