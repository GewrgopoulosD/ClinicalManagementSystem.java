package dao;

import configDB.DatabaseConnection;
import models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM employee WHERE idRole = 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Doctor d = new Doctor(
                        rs.getInt("idEmployee"),
                        rs.getString("name"),
                        rs.getString("lastName"),
                        rs.getString("tel"),
                        rs.getString("email"),
                        "",
                        rs.getInt("idClinic")
                );
                doctors.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public int getTotalPatientsCount() {
        String sql = "SELECT COUNT(*) FROM customer";
        int count = 0;
        try (Connection conn = configDB.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getTodayAppointmentsCount() {

        String sql = "SELECT COUNT(*) FROM appointment WHERE DATE(appointmentDatetime) = CURDATE()";
        int count = 0;
        try (Connection conn = configDB.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<String> getAllSpecializations() {
        List<String> specs = new ArrayList<>();
        String sql = "SELECT specialization FROM specialization";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                specs.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return specs;
    }

    public List<Doctor> getDoctorsWithoutSpecialization() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT e.* FROM employee e " +
                "LEFT JOIN doctorspecialization ds ON e.idEmployee = ds.idEmployee " +
                "WHERE e.idRole = 1 AND ds.idSpecialization IS NULL";

        try (Connection conn = configDB.DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Doctor d = new Doctor(
                        rs.getInt("idEmployee"),
                        rs.getString("name"),
                        rs.getString("lastName"),
                        rs.getString("tel"),
                        rs.getString("email"),
                        "", // password (κενό για λόγους ασφαλείας στο UI)
                        rs.getInt("idClinic")
                );
                doctors.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    public List<String> getDoctorSpecializations(int doctorId) {
        List<String> specs = new ArrayList<>();
        String sql = "SELECT s.specialization FROM specialization s " +
                "JOIN doctorspecialization ds ON s.idSpecialization = ds.idSpecialization " +
                "WHERE ds.idEmployee = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                specs.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return specs;
    }

    public boolean assignSpecialization(int doctorId, String specName) {
        String findIdSql = "SELECT idSpecialization FROM specialization WHERE specialization = ?";//find id from the specialization
        String insertSql = "INSERT INTO doctorspecialization (idEmployee, idSpecialization) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            int specId = -1;//for errors

            try (PreparedStatement pst1 = conn.prepareStatement(findIdSql)) {
                pst1.setString(1, specName);
                ResultSet rs = pst1.executeQuery();
                if (rs.next()) specId = rs.getInt(1);
            }

            if (specId != -1) {
                try (PreparedStatement pst2 = conn.prepareStatement(insertSql)) {
                    pst2.setInt(1, doctorId);
                    pst2.setInt(2, specId);
                    return pst2.executeUpdate() > 0;//if the new row got in the table
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
