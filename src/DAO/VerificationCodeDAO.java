package DAO;

import alert.AlertView;
import configDB.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VerificationCodeDAO {

    public static boolean isValidCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("verification code is null");
        }

        String sql = "SELECT COUNT(*) FROM verificationcodes WHERE verificationcode = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // αν υπάρχει έστω ένα
            }

        } catch (SQLException e) {
            throw new RuntimeException("Cannot check verification code: " + e.getMessage(), e);
        }

        return false;
    }
}
