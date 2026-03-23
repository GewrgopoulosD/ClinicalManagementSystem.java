package DAO;

import configDB.DatabaseConnection;
import models.Appointment;
import models.Patient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    public List<Appointment> getTodayAppointments(int doctorId) {

        List<Appointment> appointments = new ArrayList<>();

        String sql = """
                SELECT *
                FROM appointment
                WHERE idEmployee = ?
                AND DATE(appointmentDatetime) = CURDATE()
                ORDER BY appointmentDatetime
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Appointment appointment = new Appointment(
                        rs.getInt("idAppointment"),
                        rs.getInt("idCustomer"),
                        rs.getInt("idClinic"),
                        rs.getInt("idEmployee"),
                        rs.getTimestamp("appointmentDatetime").toLocalDateTime(),
                        rs.getString("appointmentType"),
                        rs.getString("appointmentDescription")
                );

                appointments.add(appointment);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot fetch appointments", e);
        }

        return appointments;

    }public List<Appointment> getAllAppointments(int doctorId) {

        List<Appointment> appointments = new ArrayList<>();

        String sql = """
                SELECT *
                FROM appointment
                WHERE idEmployee = ?
                ORDER BY appointmentDatetime
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Appointment appointment = new Appointment(
                        rs.getInt("idAppointment"),
                        rs.getInt("idCustomer"),
                        rs.getInt("idClinic"),
                        rs.getInt("idEmployee"),
                        rs.getTimestamp("appointmentDatetime").toLocalDateTime(),
                        rs.getString("appointmentType"),
                        rs.getString("appointmentDescription")
                );

                appointments.add(appointment);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot fetch appointments", e);
        }

        return appointments;
    }
    public int getTotalAppointments(int doctorId) {

        String sql = """
            SELECT COUNT(*)
            FROM appointment
            WHERE idEmployee = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot count appointments", e);
        }

        return 0;
    }

    public Appointment getNextAppointment(int doctorId) {

        String sql = """
                SELECT *
                FROM appointment
                WHERE idEmployee = ?
                AND appointmentDatetime > NOW()
                ORDER BY appointmentDatetime
                LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                return new Appointment(
                        rs.getInt("idAppointment"),
                        rs.getInt("idCustomer"),
                        rs.getInt("idClinic"),
                        rs.getInt("idEmployee"),
                        rs.getTimestamp("appointmentDatetime").toLocalDateTime(),
                        rs.getString("appointmentType"),
                        rs.getString("appointmentDescription")
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot fetch next appointment", e);
        }

        return null;
    }

    public List<Patient> getAllPatients() {

        List<Patient> patients = new ArrayList<>();

        String sql = """
            SELECT *
            FROM customer
            ORDER BY lastname
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Patient patient = new Patient(
                        rs.getString("name"),
                        rs.getString("lastname"),
                        rs.getString("tel"),
                        rs.getString("email"),
                        "",
                        rs.getString("amka")
                );

                patients.add(patient);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot fetch patients", e);
        }

        return patients;
    }

    public List<Patient> getDoctorPatients(int doctorId) {

        List<Patient> patients = new ArrayList<>();

        String sql = """
                SELECT DISTINCT c.*
                FROM customer c
                JOIN appointment a ON c.idCustomer = a.idCustomer
                WHERE a.idEmployee = ?
                ORDER BY c.lastname
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Patient patient = new Patient(
                        rs.getString("name"),
                        rs.getString("lastname"),
                        rs.getString("tel"),
                        rs.getString("email"),
                        "",
                        rs.getString("amka")
                );

                patients.add(patient);
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot fetch patients", e);
        }

        return patients;
    }

}
