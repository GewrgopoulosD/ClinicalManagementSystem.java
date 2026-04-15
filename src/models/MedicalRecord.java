package models;

public class MedicalRecord {

    private int appointmentId;
    private int idCustomer;
    private String diagnosis;
    private String treatment;
    private String internalNotes;

    public MedicalRecord() {}

    public MedicalRecord(int appointmentId, int idCustomer, String diagnosis, String treatment, String internalNotes) {
        this.appointmentId = appointmentId;
        this.idCustomer = idCustomer;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.internalNotes = internalNotes;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getInternalNotes() {
        return internalNotes;
    }

    public void setInternalNotes(String internalNotes) {
        this.internalNotes = internalNotes;
    }
}