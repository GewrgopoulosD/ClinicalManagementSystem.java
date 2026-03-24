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
    private static final Type USER_LIST_TYPE = new TypeToken<List<User>>(){}.getType();

    //read users from json and make it map for speed
    private Map<String, User> getAllUsersMap() {
        try {
            List<User> usersList = JsonHandler.readList(FILE_PATH, USER_LIST_TYPE);
            if (usersList == null) return new HashMap<>();

            // list to map key = email, value = user
            Map<String, User> userMap = new HashMap<>();
            for (User u : usersList) {
                userMap.put(u.getEmail().toLowerCase(), u);
            }
            return userMap;

        } catch (IOException e) {
            throw new RuntimeException("Error reading user data: " + e.getMessage(), e);
        }
    }

    //save the map to json as list
    private void saveAllUsers(Map<String, User> userMap) {
        try {
            //the json accept only list
            List<User> usersList = new ArrayList<>(userMap.values());
            JsonHandler.writeList(FILE_PATH, usersList);
        } catch (IOException e) {
            throw new RuntimeException("Error saving user data: " + e.getMessage(), e);
        }
    }

    public void save(User user) {
        if (user == null) throw new IllegalArgumentException("User object is null");

        Map<String, User> users = getAllUsersMap();

        //logic for auto increment id
        int maxId = 0;

        for (User u : users.values()) {
            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }
        user.setId(maxId + 1);


        users.put(user.getEmail().toLowerCase(), user);
        saveAllUsers(users);
    }

    public boolean emailExists(String email) {
        return getAllUsersMap().containsKey(email.toLowerCase());
    }

    public boolean telExists(String tel, Role role) {
        Map<String, User> users = getAllUsersMap();

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

        for (User u : getAllUsersMap().values()) {
            if (u instanceof Patient p) {
                if (p.getAmka().equals(amka)) return true;
            }
        }
        return false;
    }

    public User userExists(String email) {
        Map<String, User> users = getAllUsersMap();
        User user = users.get(email.toLowerCase());

        if (user != null) {
            return user;
        }

        throw new IllegalArgumentException("Invalid email address.");
    }
}