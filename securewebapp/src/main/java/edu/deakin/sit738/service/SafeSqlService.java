package edu.deakin.sit738.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Safe version — uses PreparedStatement with parameter binding.
 */
@Service
public class SafeSqlService {
    private final DataSource dataSource;

    public SafeSqlService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Safe: uses parameter binding so user input cannot change SQL structure.
     */
    public List<String> searchUsersSafe(String q) {
        List<String> out = new ArrayList<>();
        String sql = "SELECT username, email FROM app_user WHERE username LIKE ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + q + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(rs.getString("username") + " : " + rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            out.add("SQL error: " + e.getMessage());
        }
        return out;
    }
}
