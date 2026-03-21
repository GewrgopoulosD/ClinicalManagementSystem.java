package models;

public class Patient extends User {

    private String amka;

    public Patient(String name, String lastname, String telephone, String email, String password, String amka) {
        super(name, lastname, telephone, email, password);
        this.amka = amka;
    }

    public String getAmka() {
        return amka;
    }

    public void setAmka(String amka) {
        this.amka = amka;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "class: " + this.getClass().getSimpleName() + "\n" +
                "amka: " + amka;
    }
}
