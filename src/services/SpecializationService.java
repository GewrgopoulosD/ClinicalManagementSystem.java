package services;

import dao.SpecializationDAO;
import models.Specialization;

import java.util.List;
import java.util.stream.Collectors;

public class SpecializationService {

    private final SpecializationDAO specializationDAO = new SpecializationDAO();


    public List<Specialization> getAllObjects() {
        return specializationDAO.getAllSpecializationsObjects();
    }


    public List<String> getAll() {
        return specializationDAO.getAllSpecializationsObjects().stream()
                .map(Specialization::getName)
                .collect(Collectors.toList());
    }

    public void add(String specName) {
        if (specName == null || specName.trim().isEmpty()) {
            throw new IllegalArgumentException("Specialization name cannot be empty.");
        }

        specializationDAO.addSpecialization(specName.trim());
    }

    public boolean exists(String specName) {
        if (specName == null) return false;
        return specializationDAO.exists(specName.trim());
    }
}