package models;

public class Admin extends User {

    private int idClinic;

    public Admin(String name, String lastname, String telephone, String email, String password, int idClinic, int role) {
        super(name, lastname, telephone, email, password);
        this.idClinic = idClinic;
        this.setRole(Role.ADMIN);
    }

    public Admin(int id, String name, String lastname, String telephone, String email, String password, int idClinic) {
        super(name, lastname, telephone, email, password);
        this.setId(id);
        this.idClinic = idClinic;
        this.setRole(Role.ADMIN);
    }

    public int getIdClinic() {
        return idClinic;
    }

    public void setIdClinic(int idClinic) {
        this.idClinic = idClinic;
    }
}