package services;

import dao.UserDAO;
import models.*;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private UserDAO userDAO = new UserDAO();

    public User login(String email, String passwordInput) {
        User user = userDAO.userExists(email); // call the userExists

        if (!BCrypt.checkpw(passwordInput, user.getPassword())) {
            throw new RuntimeException("Invalid password");//check the password
        }

        if (user instanceof Doctor) {
            user.setRole(Role.DOCTOR);
        } else if (user instanceof Patient) {
            user.setRole(Role.PATIENT);
        } else if (user instanceof Admin) {
            user.setRole(Role.ADMIN);
        }

        return user;
    }


}
