package models;

import java.time.LocalDateTime;

public class Appointment {

    private int idAppointment;
    private int idCustomer;
    private int idClinic;
    private int idEmployee;
    private LocalDateTime appointmentDatetime;
    private String appointmentType;
    private String appointmentDescription;

    public Appointment(int idAppointment, int idCustomer, int idClinic, int idEmployee, LocalDateTime appointmentDatetime, String appointmentType, String appointmentDescription) {
        this.idAppointment = idAppointment;
        this.idCustomer = idCustomer;
        this.idClinic = idClinic;
        this.idEmployee = idEmployee;
        this.appointmentDatetime = appointmentDatetime;
        this.appointmentType = appointmentType;
        this.appointmentDescription = appointmentDescription;
    }

    public int getIdAppointment() {
        return idAppointment;
    }

    public void setIdAppointment(int idAppointment) {
        this.idAppointment = idAppointment;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public int getIdClinic() {
        return idClinic;
    }

    public void setIdClinic(int idClinic) {
        this.idClinic = idClinic;
    }

    public int getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(int idEmployee) {
        this.idEmployee = idEmployee;
    }

    public LocalDateTime getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(LocalDateTime appointmentDatetime) {
        this.appointmentDatetime = appointmentDatetime;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    public void setAppointmentDescription(String appointmentDescription) {
        this.appointmentDescription = appointmentDescription;
    }
}