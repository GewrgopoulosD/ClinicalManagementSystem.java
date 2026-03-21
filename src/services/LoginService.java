package services;

import DAO.UserDAO;
import models.User;
import org.mindrot.jbcrypt.BCrypt;

public class LoginService {

    private UserDAO userDAO = new UserDAO();

    public User login(String email, String passwordInput) {
        User user = userDAO.userExists(email); // call the userExists

        if (!BCrypt.checkpw(passwordInput, user.getPassword())) {
            throw new RuntimeException("Invalid password");//check the password
        }

        return user;
    }


}
