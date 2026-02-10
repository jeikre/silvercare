/*package dbaccess;

import models.Service;
import models.TimeSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

	public java.util.List<models.Service> getAllServices() throws java.sql.SQLException {

		String sql = "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name FROM service";


	    java.util.List<models.Service> list = new java.util.ArrayList<>();

	    try (java.sql.Connection conn = DBConnection.getConnection();
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql);
	         java.sql.ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            models.Service s = new models.Service();
	            s.setServiceId(rs.getInt("service_id"));
	            s.setCategoryId((Integer) rs.getObject("category_id"));
	            s.setServiceName(rs.getString("service_name"));
	            s.setServiceDescription(rs.getString("service_description"));
	            s.setPrice(rs.getBigDecimal("price"));
	            s.setDuration(rs.getString("duration"));
	            s.setServiceImage(rs.getString("service_image"));
	            s.setCaregiverName(rs.getString("caregiver_name"));
	            list.add(s);
	        }
	    }

	    return list;
	}


    public List<Service> getServicesByCategory(int categoryId) throws SQLException {
        String sql =
        		"SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
        				"FROM service WHERE category_id = ? ORDER BY service_id DESC";


        List<Service> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("service_id"));
                    s.setCategoryId((Integer) rs.getObject("category_id"));
                    s.setServiceName(rs.getString("service_name"));
                    s.setServiceDescription(rs.getString("service_description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    s.setServiceImage(rs.getString("service_image"));
                    s.setCaregiverName(rs.getString("caregiver_name"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public Service getServiceById(int serviceId) throws SQLException {
        String sql =
        		"SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
        				"FROM service WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setCategoryId((Integer) rs.getObject("category_id"));
                s.setServiceName(rs.getString("service_name"));
                s.setServiceDescription(rs.getString("service_description"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDuration(rs.getString("duration"));
                s.setServiceImage(rs.getString("service_image"));
                s.setCaregiverName(rs.getString("caregiver_name"));
                return s;
            }
        }
    }
    public Service getServiceWithCategoryName(int serviceId) throws SQLException {
        String sql =
        		"SELECT s.service_id, s.category_id, s.service_name, s.service_description, s.price, s.duration, s.service_image, s.caregiver_name, " +
        				"       c.category_name " +
        				"FROM service s " +
        				"JOIN service_category c ON s.category_id = c.category_id " +
        				"WHERE s.service_id = ? " +
        				"LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setCategoryId((Integer) rs.getObject("category_id"));
                s.setServiceName(rs.getString("service_name"));
                s.setServiceDescription(rs.getString("service_description"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDuration(rs.getString("duration"));
                s.setServiceImage(rs.getString("service_image"));
                s.setCaregiverName(rs.getString("caregiver_name"));


                // store categoryName into Service if you have that field,
                // otherwise controller will set request attribute "categoryName"
                // request attr is enough; no need to add field.
                // We'll return categoryName separately by reading again in controller.
                return s;
            }
        }
    }

    // ✅ get category name by serviceId (simple helper)
    public String getCategoryNameByServiceId(int serviceId) throws SQLException {
        String sql =
            "SELECT c.category_name " +
            "FROM service s JOIN service_category c ON s.category_id=c.category_id " +
            "WHERE s.service_id=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("category_name") : "";
            }
        }
    }

    // ✅ time slots
    public List<TimeSlot> getTimeSlotsByServiceId(int serviceId) throws SQLException {
        String sql =
            "SELECT display_label, time_value " +
            "FROM service_time_slot " +
            "WHERE service_id = ? " +
            "ORDER BY time_value";

        List<TimeSlot> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new TimeSlot(
                        rs.getString("display_label"),
                        rs.getString("time_value")
                    ));
                }
            }
        }
        return list;
    }
    public boolean insertService(models.Service s) throws java.sql.SQLException {

        String sql =
        		"INSERT INTO service (category_id, service_name, service_description, price, duration, service_image, caregiver_name) " +
        				"VALUES (?, ?, ?, ?, ?, ?, ?)";


        try (java.sql.Connection conn = DBConnection.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, s.getCategoryId());
            ps.setString(2, s.getServiceName());
            ps.setString(3, s.getServiceDescription());
            ps.setBigDecimal(4, s.getPrice());
            ps.setString(5, s.getDuration());
            ps.setString(6, s.getServiceImage()); // can be null/empty
            ps.setString(7, s.getCaregiverName());

            return ps.executeUpdate() > 0;
        }
    }
    public List<Service> searchServices(String q) throws SQLException {
        String sql =
        		 "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
        			        "FROM service " +
        			        "WHERE LOWER(service_name) LIKE ? OR LOWER(service_description) LIKE ? OR LOWER(caregiver_name) LIKE ? " +
        			        "ORDER BY service_id DESC";

        List<Service> list = new ArrayList<>();
        String like = "%" + q.toLowerCase().trim() + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("service_id"));
                    s.setCategoryId((Integer) rs.getObject("category_id"));
                    s.setServiceName(rs.getString("service_name"));
                    s.setServiceDescription(rs.getString("service_description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    s.setServiceImage(rs.getString("service_image"));
                    s.setCaregiverName(rs.getString("caregiver_name"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public List<Service> searchServicesInCategory(int categoryId, String q) throws SQLException {
        String sql =
        		 "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
        			        "FROM service " +
        			        "WHERE category_id = ? AND (" +
        			        "    LOWER(service_name) LIKE ? OR LOWER(service_description) LIKE ? OR LOWER(caregiver_name) LIKE ?" +
        			        ") " +
        			        "ORDER BY service_id DESC";
        List<Service> list = new ArrayList<>();
        String like = "%" + q.toLowerCase().trim() + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like); 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("service_id"));
                    s.setCategoryId((Integer) rs.getObject("category_id"));
                    s.setServiceName(rs.getString("service_name"));
                    s.setServiceDescription(rs.getString("service_description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    s.setServiceImage(rs.getString("service_image"));
                    s.setCaregiverName(rs.getString("caregiver_name"));
                    list.add(s);
                }
            }
        }
        return list;
    }
    public List<Service> getUnassignedServices() throws SQLException {
        String sql =
            "SELECT s.service_id, s.category_id, s.service_name, s.service_description, " +
            "       s.price, s.duration, s.service_image " +
            "FROM service s " +
            "LEFT JOIN caregiver_service cs ON s.service_id = cs.service_id " +
            "WHERE cs.caregiver_id IS NULL " +  
            "ORDER BY s.service_name ASC";

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
    public String getCaregiverNameForService(int serviceId) throws SQLException {
        String sql =
            "SELECT c.name " +
            "FROM caregiver_service cs " +
            "JOIN caregiver c ON cs.caregiver_id = c.caregiver_id " +
            "WHERE cs.service_id = ? " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : "";
            }
        }
    }
    public boolean updateCaregiverNameForService(int serviceId) throws SQLException {
        String sql =
            "UPDATE service s " +
            "JOIN caregiver_service cs ON s.service_id = cs.service_id " +
            "JOIN caregiver c ON cs.caregiver_id = c.caregiver_id " +
            "SET s.caregiver_name = c.name " +
            "WHERE s.service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serviceId);
            return ps.executeUpdate() > 0;
        }
    }




    public void insertTimeSlotsForService(int serviceId, String[] labels, String[] times) throws SQLException {
        if (labels == null || times == null) return;

        String sql = "INSERT INTO service_time_slot (service_id, display_label, time_value) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int n = Math.min(labels.length, times.length);

            for (int i = 0; i < n; i++) {
                String lbl = labels[i] == null ? "" : labels[i].trim();
                String t   = times[i] == null ? "" : times[i].trim();

                if (lbl.isEmpty() || t.isEmpty()) continue;

                // <input type="time"> gives HH:mm; your DB is time_value like HH:mm:ss
                if (t.length() == 5) t = t + ":00";

                ps.setInt(1, serviceId);
                ps.setString(2, lbl);
                ps.setString(3, t);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
}
*/

package dbaccess;

import models.Service;
import models.TimeSlot;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    // =========================
    // SERVICES
    // =========================
    public List<Service> getAllServices() throws SQLException {

        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
            "FROM service";

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
                s.setCaregiverName(rs.getString("caregiver_name"));
                list.add(s);
            }
        }
        return list;
    }

    public List<Service> getServicesByCategory(int categoryId) throws SQLException {

        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
            "FROM service WHERE category_id = ? ORDER BY service_id DESC";

        List<Service> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("service_id"));
                    s.setCategoryId((Integer) rs.getObject("category_id"));
                    s.setServiceName(rs.getString("service_name"));
                    s.setServiceDescription(rs.getString("service_description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    s.setServiceImage(rs.getString("service_image"));
                    s.setCaregiverName(rs.getString("caregiver_name"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public Service getServiceById(int serviceId) throws SQLException {

        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
            "FROM service WHERE service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setCategoryId((Integer) rs.getObject("category_id"));
                s.setServiceName(rs.getString("service_name"));
                s.setServiceDescription(rs.getString("service_description"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDuration(rs.getString("duration"));
                s.setServiceImage(rs.getString("service_image"));
                s.setCaregiverName(rs.getString("caregiver_name"));
                return s;
            }
        }
    }

    public Service getServiceWithCategoryName(int serviceId) throws SQLException {

        String sql =
            "SELECT s.service_id, s.category_id, s.service_name, s.service_description, s.price, s.duration, s.service_image, s.caregiver_name, " +
            "       c.category_name " +
            "FROM service s " +
            "JOIN service_category c ON s.category_id = c.category_id " +
            "WHERE s.service_id = ? " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setCategoryId((Integer) rs.getObject("category_id"));
                s.setServiceName(rs.getString("service_name"));
                s.setServiceDescription(rs.getString("service_description"));
                s.setPrice(rs.getBigDecimal("price"));
                s.setDuration(rs.getString("duration"));
                s.setServiceImage(rs.getString("service_image"));
                s.setCaregiverName(rs.getString("caregiver_name"));

                // category_name is selected but not stored into Service here (ok)
                return s;
            }
        }
    }

    public String getCategoryNameByServiceId(int serviceId) throws SQLException {

        String sql =
            "SELECT c.category_name " +
            "FROM service s JOIN service_category c ON s.category_id=c.category_id " +
            "WHERE s.service_id=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("category_name") : "";
            }
        }
    }

    public boolean insertService(Service s) throws SQLException {

        String sql =
            "INSERT INTO service (category_id, service_name, service_description, price, duration, service_image, caregiver_name) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (s.getCategoryId() == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, s.getCategoryId());

            ps.setString(2, s.getServiceName());
            ps.setString(3, s.getServiceDescription());
            ps.setBigDecimal(4, s.getPrice());
            ps.setString(5, s.getDuration());
            ps.setString(6, s.getServiceImage()); // can be null/empty
            ps.setString(7, s.getCaregiverName());

            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // SEARCH
    // =========================
    public List<Service> searchServices(String q) throws SQLException {

        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
            "FROM service " +
            "WHERE LOWER(service_name) LIKE ? OR LOWER(service_description) LIKE ? OR LOWER(caregiver_name) LIKE ? " +
            "ORDER BY service_id DESC";

        List<Service> list = new ArrayList<>();
        String like = "%" + (q == null ? "" : q.toLowerCase().trim()) + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("service_id"));
                    s.setCategoryId((Integer) rs.getObject("category_id"));
                    s.setServiceName(rs.getString("service_name"));
                    s.setServiceDescription(rs.getString("service_description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    s.setServiceImage(rs.getString("service_image"));
                    s.setCaregiverName(rs.getString("caregiver_name"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    public List<Service> searchServicesInCategory(int categoryId, String q) throws SQLException {

        String sql =
            "SELECT service_id, category_id, service_name, service_description, price, duration, service_image, caregiver_name " +
            "FROM service " +
            "WHERE category_id = ? AND (" +
            "    LOWER(service_name) LIKE ? OR LOWER(service_description) LIKE ? OR LOWER(caregiver_name) LIKE ?" +
            ") " +
            "ORDER BY service_id DESC";

        List<Service> list = new ArrayList<>();
        String like = "%" + (q == null ? "" : q.toLowerCase().trim()) + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Service s = new Service();
                    s.setServiceId(rs.getInt("service_id"));
                    s.setCategoryId((Integer) rs.getObject("category_id"));
                    s.setServiceName(rs.getString("service_name"));
                    s.setServiceDescription(rs.getString("service_description"));
                    s.setPrice(rs.getBigDecimal("price"));
                    s.setDuration(rs.getString("duration"));
                    s.setServiceImage(rs.getString("service_image"));
                    s.setCaregiverName(rs.getString("caregiver_name"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    // =========================
    // CAREGIVER / ASSIGNMENT HELPERS
    // =========================
    public List<Service> getUnassignedServices() throws SQLException {

        String sql =
            "SELECT s.service_id, s.category_id, s.service_name, s.service_description, " +
            "       s.price, s.duration, s.service_image " +
            "FROM service s " +
            "LEFT JOIN caregiver_service cs ON s.service_id = cs.service_id " +
            "WHERE cs.caregiver_id IS NULL " +
            "ORDER BY s.service_name ASC";

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

    public String getCaregiverNameForService(int serviceId) throws SQLException {

        String sql =
            "SELECT c.name " +
            "FROM caregiver_service cs " +
            "JOIN caregiver c ON cs.caregiver_id = c.caregiver_id " +
            "WHERE cs.service_id = ? " +
            "LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("name") : "";
            }
        }
    }

    public boolean updateCaregiverNameForService(int serviceId) throws SQLException {

        String sql =
            "UPDATE service s " +
            "JOIN caregiver_service cs ON s.service_id = cs.service_id " +
            "JOIN caregiver c ON cs.caregiver_id = c.caregiver_id " +
            "SET s.caregiver_name = c.name " +
            "WHERE s.service_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            return ps.executeUpdate() > 0;
        }
    }

    // =========================
    // TIME SLOTS
    // =========================
    public List<TimeSlot> getTimeSlotsByServiceId(int serviceId) throws SQLException {

        String sql =
            "SELECT slot_id, slot_time " +
            "FROM service_time_slot " +
            "WHERE service_id = ? " +
            "ORDER BY slot_id ASC";

        List<TimeSlot> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // display label = slot_time, value = slot_time (or slot_id if you prefer)
                    String slotTime = rs.getString("slot_time");
                    list.add(new TimeSlot(slotTime, slotTime));
                }
            }
        }
        return list;
    }


    /**
     * Insert slots allowing EITHER label OR time.
     * - If both empty: skip row
     * - If label empty but time present: auto label
     * - If time empty but label present: try extract start time from label
     */
    public void insertTimeSlotsForService(int serviceId, String[] labels, String[] times) throws SQLException {
        if (labels == null || times == null) return;

        String sql = "INSERT INTO service_time_slot (service_id, display_label, time_value) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int n = Math.min(labels.length, times.length);

            for (int i = 0; i < n; i++) {
                String label = labels[i] == null ? "" : labels[i].trim();
                String t     = times[i] == null ? "" : times[i].trim();

                if (label.isEmpty() && t.isEmpty()) continue;

                // Case A: label only → extract start time from label
                if (!label.isEmpty() && t.isEmpty()) {
                    String extracted = extractStartTimeFromLabel(label);
                    if (extracted == null) continue; // can't parse
                    t = extracted; // already HH:mm:ss
                }

                // Case B: time only → create a simple label
                if (label.isEmpty() && !t.isEmpty()) {
                    label = buildLabelFromTime(t);
                }

                // Convert HH:mm → HH:mm:ss for DB
                if (t.length() == 5) t = t + ":00";

                ps.setInt(1, serviceId);
                ps.setString(2, label);
                ps.setString(3, t);
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }

    // ===== helpers for insertTimeSlotsForService =====
    private String buildLabelFromTime(String timeHHmm) {
        // timeHHmm may be "08:54" or "08:54:00"
        String t = timeHHmm;
        if (t.length() >= 5) t = t.substring(0, 5);
        return t + " – (custom)";
    }

    private String extractStartTimeFromLabel(String label) {
        try {
            // supports "06:00 AM – 07:00 AM" or "06:00 AM - 07:00 AM"
            String[] parts = label.split("–|-");
            if (parts.length == 0) return null;

            String start = parts[0].trim().toUpperCase();

            // Try parse "hh:mm AM/PM"
            java.time.format.DateTimeFormatter inFmt =
                java.time.format.DateTimeFormatter.ofPattern("hh:mm a");
            java.time.LocalTime lt = java.time.LocalTime.parse(start, inFmt);

            return lt.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        } catch (Exception e) {
            return null;
        }
    }
}
