package dao;

import jsondatamanager.JsonHandler;
import com.google.gson.reflect.TypeToken;
import models.Specialization;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpecializationDAO {

    private static final String FILE_PATH = "data/specializations.json";

    public List<Specialization> getAllSpecializationsObjects() {
        try {
            Type listType = new TypeToken<List<Specialization>>(){}.getType();
            List<Specialization> specs = JsonHandler.readList(FILE_PATH, listType);
            return (specs != null) ? specs : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<String> getAllSpecializations() {
        return getAllSpecializationsObjects().stream()
                .map(Specialization::getName)
                .collect(Collectors.toList());
    }

    public boolean exists(String specName) {
        return getAllSpecializations().stream()
                .anyMatch(s -> s.equalsIgnoreCase(specName.trim()));
    }

    public void addSpecialization(String newSpecName) {
        List<Specialization> allSpecs = getAllSpecializationsObjects();
        String trimmedName = newSpecName.trim();

        if (!exists(trimmedName)) {
            int nextId = allSpecs.stream()
                    .mapToInt(Specialization::getIdSpecialization)
                    .max()
                    .orElse(0) + 1;

            allSpecs.add(new Specialization(nextId, trimmedName));
            saveAll(allSpecs);
        }
    }

    private void saveAll(List<Specialization> listToSave) {
        try {
            JsonHandler.writeList(FILE_PATH, listToSave);
        } catch (IOException e) {
            throw new RuntimeException("Error saving specializations: " + e.getMessage());
        }
    }
}