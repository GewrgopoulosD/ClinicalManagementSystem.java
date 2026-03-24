package models;

public class Admin extends User {

    private int idClinic;

    public Admin(int id, String name, String lastname, String telephone, String email, String password, int idClinic) {
        super(name, lastname, telephone, email, password);
        this.setId(id);
        this.idClinic = idClinic;
        this.setRoleId(Role.ADMIN.getId());
    }

    public Admin() {
        super();
    }


    public int getIdClinic() {
        return idClinic;
    }

}