package models;

import java.time.LocalDate;

public class Appointment {

    //status for appointmentType
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private int idAppointment;
    private int idCustomer;
    private int idClinic;
    private int idEmployee;
    private String appointmentDatetime;
    private String appointmentType;
    private String appointmentDescription;
    private String doctorFullName;

    public Appointment(int idAppointment, int idCustomer, int idClinic, int idEmployee, String appointmentDatetime, String appointmentType, String appointmentDescription) {
        this.idAppointment = idAppointment;
        this.idCustomer = idCustomer;
        this.idClinic = idClinic;
        this.idEmployee = idEmployee;
        this.appointmentDatetime = appointmentDatetime;
        this.appointmentType = appointmentType;
        this.appointmentDescription = appointmentDescription;
    }

    public Appointment() {
    }

    public boolean isToday() {
        if (appointmentDatetime == null) return false;
        String today = LocalDate.now().toString();
        return appointmentDatetime.startsWith(today);
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

    public String getAppointmentDatetime() {
        return appointmentDatetime;
    }

    public void setAppointmentDatetime(String appointmentDatetime) {
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

    public String getDoctorFullName() {
        return doctorFullName;
    }

    public void setDoctorFullName(String doctorFullName) {
        this.doctorFullName = doctorFullName;
    }
}