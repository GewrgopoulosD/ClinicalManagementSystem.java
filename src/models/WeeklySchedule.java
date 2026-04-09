package models;

import java.util.HashMap;
import java.util.Map;

public class WeeklySchedule {//a model which describe the schedule of the doctors

    private int doctorId;
    private Map<String, Shift> days = new HashMap<>(); //map with key the days and value the Shift

    public WeeklySchedule() {}

    public WeeklySchedule(int doctorId) {
        this.doctorId = doctorId;
    }

    public static class Shift {
        private boolean active;
        private String start;
        private String end;

        public Shift() {}

        public Shift(boolean active, String start, String end) {
            this.active = active;
            this.start = start;
            this.end = end;
        }

        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getStart() { return start; }
        public void setStart(String start) { this.start = start; }
        public String getEnd() { return end; }
        public void setEnd(String end) { this.end = end; }
    }

    public int getDoctorId() { return doctorId; }
    public Map<String, Shift> getDays() { return days; }
}