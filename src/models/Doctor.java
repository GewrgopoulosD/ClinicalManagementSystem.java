package models;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends User {

    private int idClinic;
    private int idRole;
    private String verificationCode; //valid code that doctor took from admin
    private List<Specialization> specializations;//admin will put specializations for doctor

    public Doctor(String name, String lastname, String telephone, String email, String password, String verificationCode) {
        super(name, lastname, telephone, email, password);
        idClinic = 1;
        idRole = 1;
        this.verificationCode = verificationCode;
        this.specializations = new ArrayList<>();
    }

    public Doctor(String name, String lastname, String telephone, String email, String password) {
        super(name, lastname, telephone, email, password);
    }//second constructor for token

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    public int getIdClinic() {
        return idClinic;
    }

    public void setIdClinic(int idClinic) {
        this.idClinic = idClinic;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }



    @Override
    public String toString() {
        return super.toString() + "\n" +
                "class: " + this.getClass().getSimpleName() + "\n" +
                this.specializations;
    }
}
