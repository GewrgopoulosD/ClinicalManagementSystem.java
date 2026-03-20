package models;

public class Specialization {

    private String name;

    public Specialization(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
