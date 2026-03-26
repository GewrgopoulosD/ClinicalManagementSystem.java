package services;

import dao.UserDAO;
import models.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String passwordInput) {

        User user = userDAO.userExists(email);//check if user exists

        if (user == null) {
            throw new IllegalArgumentException("Email not found.");
        }

        if (!BCrypt.checkpw(passwordInput, user.getPassword())) {
            throw new IllegalArgumentException("Invalid password. Please try again.");
        }

        return user;
    }
}