package dao;

import jsondatamanager.JsonHandler;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SpecializationDAO {

    private static final String FILE_PATH = "data/specializations.json";

    public List<String> getAllSpecializations() {
        try {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> specs = JsonHandler.readList(FILE_PATH, listType);

            return (specs != null) ? specs : new ArrayList<>();
        } catch (IOException e) {
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

    private void saveAll(List<String> specs) {
        try {
            JsonHandler.writeList(FILE_PATH, specs);
        } catch (IOException e) {
            throw new RuntimeException("Error saving specializations: " + e.getMessage());
        }
    }
}