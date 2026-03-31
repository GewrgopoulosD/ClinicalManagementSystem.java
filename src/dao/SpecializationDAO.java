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

    public List<String> getAllSpecializations() {
        try {
            Type listType = new TypeToken<List<Specialization>>(){}.getType();
            List<Specialization> fullSpecs = JsonHandler.readList(FILE_PATH, listType);

            if (fullSpecs == null) return new ArrayList<>();

            return fullSpecs.stream()
                    .map(Specialization::getName)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean exists(String specName) {
        return getAllSpecializations().stream()
                .anyMatch(s -> s.equalsIgnoreCase(specName.trim()));
    }

    public void addSpecialization(String newSpec) {

        if (!exists(newSpec)) {
            List<String> specs = getAllSpecializations();
            specs.add(newSpec.trim());
            saveAll(specs);
        }
    }

    private void saveAll(List<String> specNames) {
        try {
            List<Specialization> listToSave = specNames.stream()
                    .map(name -> new Specialization(name))
                    .collect(Collectors.toList());

            JsonHandler.writeList(FILE_PATH, listToSave);
        } catch (IOException e) {
            throw new RuntimeException("Error saving specializations: " + e.getMessage());
        }
    }
}