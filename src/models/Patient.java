package models;

public class Patient extends User {

    private String amka;

    //signup
    public Patient(String name, String lastname, String telephone, String email, String password, String amka) {
        super(name, lastname, telephone, email, password);
        this.amka = amka;
        this.setRoleId(Role.PATIENT.getId());
    }

    //login
    public Patient(int id, String name, String lastname, String telephone, String email, String password, String amka) {
        super(name, lastname, telephone, email, password);
        this.setId(id);
        this.amka = amka;
        this.setRoleId(Role.PATIENT.getId());
    }

    //gson
    public Patient() {
        super();
        this.setRoleId(Role.PATIENT.getId());
    }

    public String getAmka() {
        return amka;
    }

    public void setAmka(String amka) {
        this.amka = amka;
    }

    @Override
    public String toString() {
        return super.toString() + "amka: " + amka;
    }
}
