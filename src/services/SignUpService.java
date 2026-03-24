package services;

import dao.VerificationCodeDAO;
import models.Doctor;
import models.User;

//Handles business logic for user registration and coordinates data persistence through daos
public class SignUpService {

    public void registerUser(User user, String verificationCode) {

        if (user instanceof Doctor) {

            boolean isValid = VerificationCodeDAO.isValidCode(verificationCode);//call dao

            if (!isValid) {
                throw new IllegalArgumentException("Invalid verification code for Doctor registration.");
            }
        }
    }
}