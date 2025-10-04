package edu.deakin.sit738.service;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * WARNING: intentionally vulnerable to SQL injection for demo purposes.
 * DO NOT use this pattern in production.
 */
@Service
public class VulnerableSqlService {
    private final DataSource dataSource;

    public VulnerableSqlService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Vulnerable: builds SQL by concatenating the user input into the query string.
     * If q contains SQL, it may be executed by the DB.
     */
    public List<String> searchUsersVulnerable(String q) {
        List<String> out = new ArrayList<>();
        String sql = "SELECT username, email FROM app_user WHERE username LIKE '%" + q + "%'"; // <-- vulnerable
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                out.add(rs.getString("username") + " : " + rs.getString("email"));
            }
        } catch (SQLException e) {
            out.add("SQL error: " + e.getMessage());
        }
        return out;
    }
}
