package services;

import dao.UserDAO;
import dao.VerificationCodeDAO;
import models.Doctor;
import models.Patient;
import models.User;
import org.mindrot.jbcrypt.BCrypt;


public class SignUpService {

    private final UserDAO userDAO;

    public SignUpService() {
        this.userDAO = new UserDAO();
    }

    public void registerUser(User user, String verificationCode) {

        if (userDAO.emailExists(user.getEmail())) {//check if email is already registered
            throw new IllegalArgumentException("This email address is already in use.");
        }

        if (userDAO.telExists(user.getTelephone(), user.getRole())) {//check for duplicate telephone per role
            throw new IllegalArgumentException("This telephone number is already registered.");
        }

        if (user instanceof Doctor) { //check the special verification code provided for medical staff
            if (!VerificationCodeDAO.isValidCode(verificationCode)) {
                throw new IllegalArgumentException("Invalid professional verification code.");
            }
        }
        else if (user instanceof Patient patient) {//check if the amka is unique
            if (userDAO.amkaExists(patient.getAmka(), patient.getRole())) {
                throw new IllegalArgumentException("This AMKA is already registered in our system.");
            }
        }

        String plainPassword = user.getPassword();
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        user.setPassword(hashedPassword);

        userDAO.registerNewUser(user);
    }
}