package models;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {

    private int idClinic;
    private String verificationCode; //valid code that doctor took from admin
    private List<Specialization> specializations;//admin will put specializations for doctor


    //signup
    public Doctor(String name, String lastname, String telephone, String email, String password) {
        super(name, lastname, telephone, email, password);
        this.idClinic = 1;
        this.specializations = new ArrayList<>();
        this.setRoleId(Role.DOCTOR.getId());
    }

    //login
    public Doctor(int id, String name, String lastname, String telephone, String email, String password, int idClinic) {
        super(name, lastname, telephone, email, password);
        this.setId(id);
        this.idClinic = idClinic;
        this.specializations = new ArrayList<>();
        this.setRoleId(Role.DOCTOR.getId());
    }

    //gson
    public Doctor() {
        super();
        this.specializations = new ArrayList<>();
        this.setRoleId(Role.DOCTOR.getId());
    }


    public int getIdClinic() {
        return idClinic;
    }

    public void setIdClinic(int idClinic) {
        this.idClinic = idClinic;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public List<Specialization> getSpecializations() {
        if (specializations == null) {
            specializations = new ArrayList<>();
        }
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    @Override
    public String toString() {
        return super.toString()  + "Specializations: " + this.specializations;
    }
}
