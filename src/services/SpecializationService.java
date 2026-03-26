package services;

import dao.SpecializationDAO;
import java.util.List;

public class SpecializationService {

    private final SpecializationDAO specializationDAO = new SpecializationDAO();


    public List<String> getAll() {
        return specializationDAO.getAllSpecializations();
    }

    public void add(String specName) {
        if (specName == null || specName.trim().isEmpty()) {
            throw new IllegalArgumentException("Το όνομα δεν μπορεί να είναι κενό.");
        }

        // Καλούμε το DAO. Αν υπάρχει ήδη, το DAO απλά δεν θα κάνει τίποτα.
        specializationDAO.addSpecialization(specName.trim());
    }


    public boolean exists(String specName) {
        return specializationDAO.exists(specName);
    }
}