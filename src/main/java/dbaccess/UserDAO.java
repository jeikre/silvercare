package dbaccess;

import models.Role;
import models.User;

import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class UserDAO {

    private static String sha256(String pwd) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(pwd.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b));
        return hex.toString();
    }

    public User authenticate(String email, String password) throws SQLException {

        String sql =
            "SELECT user_id, role, name, email, password, phone, address, created_at " +
            "FROM users " +
            "WHERE email = ? " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String roleStr = rs.getString("role");
                String dbPassword = rs.getString("password"); // stored in DB

                boolean ok;

                // ✅ ADMIN: plaintext compare
                if ("ADMIN".equalsIgnoreCase(roleStr)) {
                    ok = password.equals(dbPassword);
                }
                // ✅ CLIENT: hash compare
                else {
                    String hashed;
                    try {
                        hashed = sha256(password);
                    } catch (Exception e) {
                        throw new SQLException("Hashing failed", e);
                    }
                    ok = hashed.equals(dbPassword);
                }

                if (!ok) return null;

                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                //u.setRole(Role.valueOf(roleStr));
                u.setRole(Role.valueOf(roleStr.toUpperCase()));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(dbPassword);
                u.setPhone(rs.getString("phone"));
                u.setAddress(rs.getString("address"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                return u;
            }
        }
    }


    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE email = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean phoneExists(String phone) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE phone = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int registerClient(User u) throws SQLException {

        String sql =
            "INSERT INTO users (role, name, email, password, phone, address, created_at) " +
            "VALUES ('CLIENT', ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String hashed;
            try {
                hashed = sha256(u.getPassword());
            } catch (Exception e) {
                throw new SQLException("Hashing failed", e);
            }

            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, hashed);
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getAddress());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }

        throw new SQLException("Failed to create client user.");
    }

    public User getUserById(int userId) throws SQLException {
        String sql =
            "SELECT user_id, role, name, email, password, phone, address, created_at " +
            "FROM users WHERE user_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                //u.setRole(Role.valueOf(rs.getString("role")));
                u.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
                u.setName(rs.getString("name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setPhone(rs.getString("phone"));
                u.setAddress(rs.getString("address"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                return u;
            }
        }
    }
    public Role getRoleByUserId(int userId) throws SQLException {
        String sql = "SELECT role FROM users WHERE user_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    //return Role.valueOf(rs.getString("role"));
                	return Role.valueOf(rs.getString("role").toUpperCase());
                }
            }
        }
        return null;
    }



    public boolean updateClientProfile(User u) throws SQLException {
        String sql =
            "UPDATE users SET name=?, phone=?, address=? " +
            "WHERE user_id=? AND role='CLIENT'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getName());
            ps.setString(2, u.getPhone());
            ps.setString(3, u.getAddress());
            ps.setInt(4, u.getUserId());

            return ps.executeUpdate() > 0;
        }
    }

    public void deleteClientAccount(int userId) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // ✅ FIXED: your cart now uses user_id, not client_id
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE ci FROM client_cart_items ci " +
                        "JOIN client_cart c ON ci.cart_id = c.cart_id " +
                        "WHERE c.user_id = ?")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM client_cart WHERE user_id = ?")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM users WHERE user_id = ? AND role = 'CLIENT'")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean updateClientPassword(int userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password=? WHERE user_id=? AND role='CLIENT'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hashed;
            try {
                hashed = sha256(newPassword);
            } catch (Exception e) {
                throw new SQLException("Hashing failed", e);
            }

            ps.setString(1, hashed);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;
        }
    }
}
