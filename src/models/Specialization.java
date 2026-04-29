package models;

public class Specialization {

    private int idSpecialization;
    private String name;

    public Specialization() {}

    public Specialization(int id, String name) {
        this.idSpecialization = id;
        this.name = name;
    }

    public int getIdSpecialization() {
        return idSpecialization;
    }

    public void setIdSpecialization(int idSpecialization) {
        this.idSpecialization = idSpecialization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
