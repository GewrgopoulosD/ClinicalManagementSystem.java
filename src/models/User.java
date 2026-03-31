package models;

import com.google.gson.annotations.SerializedName;

public abstract class User {

    private int id;

    private String name;

    @SerializedName("lastName")
    private String lastname;

    @SerializedName("tel")
    private String telephone;

    private String email;
    private String password;

    @SerializedName("idRole")
    private int roleId;

    public User() {//empty constructor for json
    }

    public User(String name, String lastname, String telephone, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.telephone = telephone;
        this.email = email;
        this.password = password;
    }

    public Role getRole() {
        return Role.fromInt(this.roleId);
    }

    //for register to give access
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "User " + getRole() + "\n" +
                "id: " + id + "\n" +
                "name: " + name + "\n" +
                "lastname: " + lastname + "\n" +
                "telephone: " + telephone + "\n" +
                "email: " + email + "\n" +
                "password: " + password + "\n";
    }

    public String getFullname(){
        return this.getName() + " " +  this.getLastname();
    }
}
