package dbaccess;

import models.Role;
import models.Service;
import models.User;
import models.TimeSlot;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
public class AdminDAO {

    // =========================
    // CLIENTS (role = CLIENT)
    // =========================
	public List<User> getAllClients() throws SQLException {

	    // ✅ remove username + created_at from SELECT to avoid unknown column crash
	    String sql =
	        "SELECT user_id, role, name, email, password, phone, address " +
	        "FROM users WHERE role='CLIENT' ORDER BY user_id DESC";

	    List<User> list = new ArrayList<>();

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            User u = new User();
	            u.setUserId(rs.getInt("user_id"));

	            // ✅ role-safe (client/client/CLIENT all ok)
	            String roleStr = rs.getString("role");
	            if (roleStr != null) roleStr = roleStr.trim().toUpperCase();
	            u.setRole(Role.valueOf(roleStr));

	            u.setName(rs.getString("name"));
	            u.setEmail(rs.getString("email"));
	            u.setPassword(rs.getString("password"));
	            u.setPhone(rs.getString("phone"));
	            u.setAddress(rs.getString("address"));

	            // username + createdAt are optional; leave null
	            list.add(u);
	        }
	    }

	    return list;
	}



	public User getClientById(int userId) throws SQLException {

	    String sql =
	        "SELECT user_id, role, name, email, password, phone, address " +
	        "FROM users WHERE user_id=? AND role='CLIENT' LIMIT 1";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, userId);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) return null;

	            User u = new User();
	            u.setUserId(rs.getInt("user_id"));

	            String roleStr = rs.getString("role");
	            if (roleStr != null) roleStr = roleStr.trim().toUpperCase();
	            u.setRole(Role.valueOf(roleStr));

	            u.setName(rs.getString("name"));
	            u.setEmail(rs.getString("email"));
	            u.setPassword(rs.getString("password"));
	            u.setPhone(rs.getString("phone"));
	            u.setAddress(rs.getString("address"));

	            return u;
	        }
	    }
	}


	public boolean updateClient(models.User u) throws java.sql.SQLException {

	    String sql =
	        "UPDATE users SET name=?, email=?, password=?, phone=?, address=? " +
	        "WHERE user_id=? AND role='CLIENT'";

	    try (java.sql.Connection conn = DBConnection.getConnection();
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setString(1, u.getName());
	        ps.setString(2, u.getEmail());
	        ps.setString(3, u.getPassword());
	        ps.setString(4, u.getPhone());
	        ps.setString(5, u.getAddress());
	        ps.setInt(6, u.getUserId());

	        return ps.executeUpdate() > 0;
	    }
	}


	public boolean deleteClient(int userId) throws java.sql.SQLException {

	    String sql = "DELETE FROM users WHERE user_id=? AND role='CLIENT'";

	    try (java.sql.Connection conn = DBConnection.getConnection();
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, userId);
	        return ps.executeUpdate() > 0;
	    }
	}

    public int createClient(models.User u) throws java.sql.SQLException {

        String sql =
            "INSERT INTO users (role, name, email, password, phone, address, created_at) " +
            "VALUES ('CLIENT', ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPassword());
            ps.setString(4, u.getPhone());
            ps.setString(5, u.getAddress());

            ps.executeUpdate();

            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }

        throw new java.sql.SQLException("Failed to create client.");
    }


    // =========================
    // SERVICES CRUD
    // =========================
    public List<Service> getAllServices() throws SQLException {
        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image " +
            "FROM service ORDER BY service_id DESC";

        List<Service> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setCategoryId((Integer) rs.getObject("category_id"));
                s.setServiceName(rs.getString("service_name"));
                s.setServiceDescription(rs.getString("service_description"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDuration(rs.getString("duration"));
                s.setServiceImage(rs.getString("service_image"));
                list.add(s);
            }
        }
        return list;
    }

    public models.Service getServiceById(int serviceId) throws java.sql.SQLException {

        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image " +
            "FROM service WHERE service_id = ? LIMIT 1";

        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                models.Service s = new models.Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setCategoryId((Integer) rs.getObject("category_id"));
                s.setServiceName(rs.getString("service_name"));
                s.setServiceDescription(rs.getString("service_description"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDuration(rs.getString("duration"));
                s.setServiceImage(rs.getString("service_image"));
                return s;
            }
        }
    }

    public int createService(Service s) throws SQLException {
        String sql =
            "INSERT INTO service (category_id, service_name, service_description, price, duration, service_image) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (s.getCategoryId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, s.getCategoryId());

            ps.setString(2, s.getServiceName());
            ps.setString(3, s.getServiceDescription());
            ps.setBigDecimal(4, s.getPrice());
            ps.setString(5, s.getDuration());
            ps.setString(6, s.getServiceImage());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Failed to create service.");
    }

    public boolean updateService(models.Service s) throws java.sql.SQLException {

        String sql =
            "UPDATE service SET category_id=?, service_name=?, service_description=?, price=?, duration=?, service_image=? " +
            "WHERE service_id=?";

        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            if (s.getCategoryId() == null) ps.setNull(1, java.sql.Types.INTEGER);
            else ps.setInt(1, s.getCategoryId());

            ps.setString(2, s.getServiceName());
            ps.setString(3, s.getServiceDescription());
            ps.setBigDecimal(4, s.getPrice());
            ps.setString(5, s.getDuration());
            ps.setString(6, s.getServiceImage());
            ps.setInt(7, s.getServiceId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteService(int serviceId) throws SQLException {

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1) delete child rows first
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM caregiver_service WHERE service_id=?")) {
                    ps.setInt(1, serviceId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM client_cart_items WHERE service_id=?")) {
                    ps.setInt(1, serviceId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM service_time_slot WHERE service_id=?")) {
                    ps.setInt(1, serviceId);
                    ps.executeUpdate();
                }

                // 2) now delete the service
                int affected;
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM service WHERE service_id=?")) {
                    ps.setInt(1, serviceId);
                    affected = ps.executeUpdate();
                }

                conn.commit();
                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
 // =========================
 // TIME SLOTS (ADMIN)
 // =========================
    // =========================
    // TIME SLOTS (ADMIN)
    // =========================

    public List<TimeSlot> getTimeSlotsByServiceId(int serviceId) throws SQLException {

        String sql =
            "SELECT slot_id, service_id, display_label, time_value, start_time, end_time " +
            "FROM service_time_slot " +
            "WHERE service_id = ? " +
            "ORDER BY slot_id ASC";

        List<TimeSlot> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimeSlot t = new TimeSlot();
                    t.setSlotId(rs.getInt("slot_id"));
                    t.setServiceId(rs.getInt("service_id"));
                    t.setDisplayLabel(rs.getString("display_label"));
                    t.setTimeValue(rs.getString("time_value"));   // e.g. "18:00:00"
                    t.setStartTime(rs.getString("start_time"));   // may be null
                    t.setEndTime(rs.getString("end_time"));       // may be null
                    list.add(t);
                }
            }
        }

        return list;
    }

    /**
     * mode:
     *  - "single": use single HH:mm
     *  - "range": use start HH:mm and end HH:mm
     *
     * Stores:
     *  - time_value = start time (HH:mm:00)
     *  - display_label = either "06:00 PM" or "01:00 PM - 06:00 PM"
     *  - start_time/end_time = friendly 12h strings (optional)
     */
    public boolean addTimeSlot(int serviceId, String mode, String singleHHmm, String startHHmm, String endHHmm)
            throws SQLException {

        if (mode == null) mode = "single";
        mode = mode.trim().toLowerCase();

        String displayLabel;
        String timeValue;
        String startTimeFriendly = null;
        String endTimeFriendly = null;

        if ("range".equals(mode)) {

            if (isBlank(startHHmm) || isBlank(endHHmm)) return false;

            // DB time_value uses start time
            timeValue = toHHmmss(startHHmm);

            startTimeFriendly = to12h(startHHmm);
            endTimeFriendly = to12h(endHHmm);

            displayLabel = startTimeFriendly + " - " + endTimeFriendly;

        } else {
            // default = single
            if (isBlank(singleHHmm)) return false;

            timeValue = toHHmmss(singleHHmm);

            startTimeFriendly = to12h(singleHHmm);
            displayLabel = startTimeFriendly;  // e.g. "06:00 PM"
        }

        // Optional: prevent duplicate for same service
        if (existsSameSlot(serviceId, displayLabel, timeValue)) {
            return false;
        }

        String sql =
            "INSERT INTO service_time_slot (service_id, display_label, time_value, start_time, end_time) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setString(2, displayLabel);
            ps.setString(3, timeValue);

            if (startTimeFriendly == null) ps.setNull(4, Types.VARCHAR);
            else ps.setString(4, startTimeFriendly);

            if (endTimeFriendly == null) ps.setNull(5, Types.VARCHAR);
            else ps.setString(5, endTimeFriendly);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTimeSlot(int slotId, int serviceId) throws SQLException {
        String sql = "DELETE FROM service_time_slot WHERE slot_id = ? AND service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slotId);
            ps.setInt(2, serviceId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- helpers ----------

    private boolean existsSameSlot(int serviceId, String displayLabel, String timeValue) throws SQLException {
        String sql =
            "SELECT 1 FROM service_time_slot " +
            "WHERE service_id=? AND display_label=? AND time_value=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setString(2, displayLabel);
            ps.setString(3, timeValue);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // "18:00" -> "18:00:00"
    private static String toHHmmss(String hhmm) {
        String t = hhmm.trim();
        if (t.length() == 5) return t + ":00";
        return t; // assume already HH:mm:ss
    }

    // "18:00" -> "06:00 PM"
    private static String to12h(String hhmm) {
        LocalTime lt = LocalTime.parse(hhmm.trim(), DateTimeFormatter.ofPattern("HH:mm"));
        return lt.format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

}
