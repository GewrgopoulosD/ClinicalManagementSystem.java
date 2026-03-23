package models;

public abstract class User {

    private int id;
    private String name;
    private String lastname;
    private String telephone;
    private String email;
    private String password;
    private Role role;

    public User(String name, String lastname, String telephone, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User" + "\n" +
                "name: " + name + "\n" +
                "lastname: " + lastname + "\n" +
                "telephone: " + telephone + "\n" +
                "email: " + email + "\n" +
                "password: " + password;
    }
}
