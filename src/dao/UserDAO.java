package dao;

import jsondatamanager.JsonHandler;
import models.*;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDAO {

    private static final String FILE_PATH = "data/users.json";

    //read users from json and make it map for speed
    public Map<String, User> fetchAllUsersAsMap() {
        try {
            Type rawType = new TypeToken<List<Map<String, Object>>>(){}.getType();
            List<Map<String, Object>> rawData = JsonHandler.readList(FILE_PATH, rawType);

            Map<String, User> userMap = new HashMap<>();
            if (rawData == null) return userMap;

            for (Map<String, Object> data : rawData) {
                int id = ((Double) data.get("id")).intValue();
                int roleId = ((Double) data.get("idRole")).intValue();

                String name = (String) data.get("name");
                String lastName = (String) data.get("lastName");
                String tel = (String) data.get("tel");
                String email = (String) data.get("email");
                String password = (String) data.get("password");

                User u;

                if (roleId == 1) { // DOCTOR
                    int idClinic = (data.get("idClinic") != null) ? ((Double) data.get("idClinic")).intValue() : 1;
                    Doctor d = new Doctor(id, name, lastName, tel, email, password, idClinic);

                    if (data.get("specializations") != null) {
                        List<Map<String, Object>> specDataList = (List<Map<String, Object>>) data.get("specializations");
                        List<Specialization> specObjects = new ArrayList<>();

                        for (Map<String, Object> specData : specDataList) {
                            int sId = ((Double) specData.get("idSpecialization")).intValue();
                            String sName = (String) specData.get("name");

                            specObjects.add(new Specialization(sId, sName));
                        }
                        d.setSpecializations(specObjects);
                    }
                    u = d;
                }
                else if (roleId == 2) { // ADMIN
                    int idClinic = (data.get("idClinic") != null) ? ((Double) data.get("idClinic")).intValue() : 1;
                    u = new Admin(id, name, lastName, tel, email, password, idClinic);
                }
                else { // PATIENT
                    String amka = (String) data.get("amka");
                    u = new Patient(id, name, lastName, tel, email, password, amka);
                }

                userMap.put(email.toLowerCase(), u);
            }
            return userMap;

        } catch (IOException e) {
            throw new RuntimeException("Error reading user data: " + e.getMessage(), e);
        }
    }

    //for table view or sth else
    public List<User> fetchAllUsersAsList() {
        return new ArrayList<>(fetchAllUsersAsMap().values());
    }

    //update users
    public void updateAllUsersData(Map<String, User> userMap) {
        try {
            //the json accept only list
            List<User> usersList = new ArrayList<>(userMap.values());
            JsonHandler.writeList(FILE_PATH, usersList);
        } catch (IOException e) {
            throw new RuntimeException("Error saving user data: " + e.getMessage(), e);
        }
    }

    //new user
    public void registerNewUser(User user) {
        if (user == null) throw new IllegalArgumentException("User object is null");

        Map<String, User> users = fetchAllUsersAsMap();

        //logic for auto increment id
        int maxId = 0;

        for (User u : users.values()) {
            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }
        user.setId(maxId + 1);


        users.put(user.getEmail().toLowerCase(), user);
        updateAllUsersData(users);
    }

    public boolean emailExists(String email) {
        return fetchAllUsersAsMap().containsKey(email.toLowerCase());
    }

    public boolean telExists(String tel, Role role) {
        Map<String, User> users = fetchAllUsersAsMap();

        //check only values
        for (User u : users.values()) {
            if (u.getTelephone().equals(tel) && u.getRole() == role) {
                return true;
            }
        }

        return false;
    }

    public boolean amkaExists(String amka, Role role) {
        if (role != Role.PATIENT) return false;

        for (User u : fetchAllUsersAsMap().values()) {
            if (u instanceof Patient p) {
                if (p.getAmka().equals(amka)) return true;
            }
        }
        return false;
    }

    public User userExists(String email) {
        Map<String, User> users = fetchAllUsersAsMap();
        User user = users.get(email.toLowerCase());

        if (user != null) {
            return user;
        }

        throw new IllegalArgumentException("Invalid email address.");
    }

    public void updateSingleUser(User updatedUser, String oldEmail) {
        if (updatedUser == null || oldEmail == null) return;

        Map<String, User> users = fetchAllUsersAsMap();

        if (!updatedUser.getEmail().equalsIgnoreCase(oldEmail)) {
            users.remove(oldEmail.toLowerCase());
        }

        users.put(updatedUser.getEmail().toLowerCase(), updatedUser);

        updateAllUsersData(users);
    }

    public User getUserByEmail(String email) {
        return fetchAllUsersAsMap().get(email.toLowerCase());
    }

    public void deleteUser(String email) {
        Map<String, User> users = fetchAllUsersAsMap();
        if (users.remove(email.toLowerCase()) != null) {
            updateAllUsersData(users);
        }
    }

    public List<User> findDoctorsBySpecialty(String SpecialtyName) {
        List<User> allUsers = fetchAllUsersAsList();

        return allUsers.stream()
                .filter(u -> u.getRoleId() == 1)
                .filter(u -> u instanceof Doctor)
                .filter(u -> {
                    Doctor d = (Doctor) u;
                    return d.getSpecializations().stream()
                            .anyMatch(spec -> spec.getName().equalsIgnoreCase(SpecialtyName));
                })
                .collect(Collectors.toList());
    }

    public User findUserById(int id) {
        List<User> users = fetchAllUsersAsList();

        return users.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }
}