package models;

public enum Role {

    DOCTOR(1),
    ADMIN(2),
    PATIENT(3);

    private final int id;

    Role(int id) {
        this.id = id;
    }


    public int getId() {
        return id;
    }

    //number to role Role
    public static Role fromInt(int id) {
        for (Role r : Role.values()) {
            if (r.id == id) return r;
        }
        throw new IllegalArgumentException("Unknown Role ID: " + id);
    }
}
