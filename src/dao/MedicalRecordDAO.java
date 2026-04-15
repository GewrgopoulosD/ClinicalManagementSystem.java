package dao;

import jsondatamanager.JsonHandler;
import models.MedicalRecord;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MedicalRecordDAO {
    private static final String FILE_PATH = "data/medicalRecords.json";

    public List<MedicalRecord> getAllRecords() {
        try {
            Type listType = new TypeToken<List<MedicalRecord>>(){}.getType();
            List<MedicalRecord> records = JsonHandler.readList(FILE_PATH, listType);
            return (records != null) ? records : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    //from appointmentId, to medicalRecord
    public MedicalRecord getRecordByAppointmentId(int appointmentId) {
        return getAllRecords().stream()
                .filter(r -> r.getAppointmentId() == appointmentId)
                .findFirst()
                .orElse(null);
    }

    //all record from customerId (future)
    public List<MedicalRecord> getRecordsByCustomer(int idCustomer) {
        return getAllRecords().stream()
                .filter(r -> r.getIdCustomer() == idCustomer)
                .collect(Collectors.toList());
    }

    public void saveOrUpdateRecord(MedicalRecord record) {
        try {
            List<MedicalRecord> all = getAllRecords();
            boolean found = false;

            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getAppointmentId() == record.getAppointmentId()) {
                    all.set(i, record); //if exist , replace
                    found = true;
                    break;
                }
            }

            if (!found) {
                all.add(record);
            }

            saveAll(all);
        } catch (IOException e) {
            throw new RuntimeException("Failed save or update record" + e.getMessage());
        }
    }

    private void saveAll(List<MedicalRecord> records) throws IOException {
        JsonHandler.writeList(FILE_PATH, records);
    }
}