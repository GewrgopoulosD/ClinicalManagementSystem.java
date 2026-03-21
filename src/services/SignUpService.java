package services;
import DAO.UserDAO;
import models.Role;
import models.User;
import org.mindrot.jbcrypt.BCrypt;


public class SignUpService {
    private UserDAO userDAO;

    public SignUpService() {
        this.userDAO = new UserDAO();
    }

    public void registerUser(User user) {
        try {
            //check if the email exist in database
            if (userDAO.emailExists(user.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            //check if the tel allready exist
            if ((user instanceof models.Patient && userDAO.telExists(user.getTelephone(), Role.PATIENT))
                    || (user instanceof models.Doctor && userDAO.telExists(user.getTelephone(), Role.DOCTOR))) {
                throw new RuntimeException("Telephone already exists");
            }

            //check if the amka exist
            if (user instanceof models.Patient patient) {
                if (userDAO.amkaExists(patient.getAmka(), Role.PATIENT)) {
                    throw new RuntimeException("AMKA already exists");
                }
            }

            // Hashing του password
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(hashedPassword);

            userDAO.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Cannot register user: " + e.getMessage(), e);
        }
    }
}
