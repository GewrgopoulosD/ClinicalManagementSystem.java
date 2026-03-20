package DAO;

import alert.AlertView;
import models.Doctor;
import models.Patient;
import models.Role;
import models.User;
import configDB.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public void save(User user) {

        if (user instanceof Patient patient) {
            savePatient(patient);
        } else if (user instanceof Doctor doctor) {
            saveDoctor(doctor);
        }
    }

    private void savePatient(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("Patient is null");
        }

        String sql = "INSERT INTO customer (name, lastname, tel, email, password, amka) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patient.getName());
            stmt.setString(2, patient.getLastname());
            stmt.setString(3, patient.getTelephone());
            stmt.setString(4, patient.getEmail());
            stmt.setString(5, patient.getPassword());
            stmt.setString(6, patient.getAmka());

            stmt.executeUpdate();

        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot save user: " + e.getMessage(), e);
        }
    }

    private void saveDoctor(Doctor doctor) {
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor is null");
        }

        String sql = "INSERT INTO employee (name, lastname, tel, email, password, idClinic, idRole) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getLastname());
            stmt.setString(3, doctor.getTelephone());
            stmt.setString(4, doctor.getEmail());
            stmt.setString(5, doctor.getPassword());
            stmt.setInt(6, doctor.getIdClinic());
            stmt.setInt(7, doctor.getIdRole());


            stmt.executeUpdate();

        } catch (Exception e) {
           throw new IllegalArgumentException("Cannot save user: " + e.getMessage(), e);
        }
    }


    public boolean emailExists(String email, Role role) {
        String sql;
        if (role == Role.PATIENT) {
            sql = "SELECT COUNT(*) FROM customer WHERE email = ?";
        } else {
            sql = "SELECT COUNT(*) FROM employee WHERE email = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot save user: " + e.getMessage(), e);
        }

        return false;
    }
    public boolean telExists(String tel, Role role) {
        String sql;
        if (role == Role.PATIENT) {
            sql = "SELECT COUNT(*) FROM customer WHERE tel = ?";
        } else {
            sql = "SELECT COUNT(*) FROM employee WHERE tel = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tel);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            throw new RuntimeException("Cannot save user: " + e.getMessage(), e);
        }

        return false;
    }

    public boolean amkaExists(String amka, Role role) {
        if (role != Role.PATIENT) {
            return false; // doctors havent amka
        }
        String sql = "SELECT COUNT(*) FROM customer WHERE amka = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, amka);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }

            } catch (Exception e) {
                throw new RuntimeException("Cannot save user: " + e.getMessage(), e);
            }

            return false;
        }
}
