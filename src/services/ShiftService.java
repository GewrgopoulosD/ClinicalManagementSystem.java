package services;

import dao.ShiftDAO;
import models.WeeklySchedule;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class ShiftService {
    private final ShiftDAO shiftDAO = new ShiftDAO();

    //if took null return a new schedule just for the controller to not throw null pointer exception
    public WeeklySchedule getDoctorSchedule(int doctorId) {
        WeeklySchedule schedule = shiftDAO.getScheduleByDoctorId(doctorId);
        if (schedule == null) {
            return new WeeklySchedule(doctorId);
        }
        return schedule;
    }

    public boolean saveSchedule(WeeklySchedule schedule) {
        //took the days from map of schedule
        for (String day : schedule.getDays().keySet()) {

            WeeklySchedule.Shift shift = schedule.getDays().get(day);

            if (shift.isActive()) {

                //check if the start is before end
                if (!isValidTimeRange(shift.getStart(), shift.getEnd())) {
                    return false;
                }
            }
        }


        shiftDAO.saveSchedule(schedule);
        return true;
    }

    //validate the time
    private boolean isValidTimeRange(String startStr, String endStr) {
        try {
            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);

            return start.isBefore(end);
        } catch (DateTimeParseException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }
}