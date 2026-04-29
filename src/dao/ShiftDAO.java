package dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import models.WeeklySchedule;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShiftDAO {
    private final String FILE_PATH = "data/shifts.json";

    //set the gson to write "pretty" to be eassier for us to read it
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //read all schedule
    public List<WeeklySchedule> getAllSchedules() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        //user bufferedReader for fast
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Type listType = new TypeToken<ArrayList<WeeklySchedule>>(){}.getType();
            List<WeeklySchedule> schedules = gson.fromJson(reader, listType);

            return (schedules != null) ? schedules : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read shifts data", e);
        }
    }

    public WeeklySchedule getScheduleByDoctorId(int doctorId) {
        return getAllSchedules().stream()
                .filter(s -> s.getDoctorId() == doctorId)
                .findFirst()
                .orElse(null);
    }

    public void saveSchedule(WeeklySchedule newSchedule) {
        List<WeeklySchedule> allSchedules = getAllSchedules();

        allSchedules.removeIf(s -> s.getDoctorId() == newSchedule.getDoctorId());

        allSchedules.add(newSchedule);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            gson.toJson(allSchedules, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save shifts data", e);
        }
    }
}
