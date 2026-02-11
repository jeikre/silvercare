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
    // TIME SLOTS (MATCH YOUR TABLE)
    // =========================
    public List<TimeSlot> getTimeSlotsByServiceId(int serviceId, String bookingDate) throws SQLException {

        // 1) Load all slots for this service
        String sql =
            "SELECT slot_id, service_id, display_label, time_value, start_time, end_time " +
            "FROM service_time_slot " +
            "WHERE service_id = ? " +
            "ORDER BY time_value ASC, slot_id ASC";

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

                    Time tv = rs.getTime("time_value");
                    t.setTimeValue(tv == null ? "" : tv.toString()); // "HH:mm:ss"

                    t.setStartTime(rs.getString("start_time"));
                    t.setEndTime(rs.getString("end_time"));

                    // default values (will be updated below)
                    t.setBookedCount(0);
                    t.setRemaining(1); // capacity = 1 by your requirement

                    list.add(t);
                }
            }
        }

        // 2) If no date given, just return the list (no availability calc)
        if (bookingDate == null || bookingDate.isBlank()) {
            return list;
        }

        // 3) Get how many already taken (cart holds + orders)
        java.util.Map<Integer, Integer> bookedMap = getBookedCountBySlot(serviceId, bookingDate);

        // 4) Apply capacity logic: capacity = 1
        final int CAPACITY = 1;

        for (TimeSlot t : list) {
            int booked = bookedMap.getOrDefault(t.getSlotId(), 0);
            int remaining = CAPACITY - booked;

            t.setBookedCount(booked);
            t.setRemaining(remaining);
        }

        return list;
    }


    // Admin add slot (single time or range)
    public boolean addTimeSlot(int serviceId, String mode, String singleTimeHHmm, String startHHmm, String endHHmm) throws SQLException {

        // mode: "single" or "range"
        String displayLabel;
        String timeValue;
        String startTime = null;
        String endTime = null;

        if ("range".equalsIgnoreCase(mode)) {
            // require start + end
            if (startHHmm == null || startHHmm.trim().isEmpty()) return false;
            if (endHHmm == null || endHHmm.trim().isEmpty()) return false;

            startHHmm = startHHmm.trim();
            endHHmm = endHHmm.trim();

            // store display_label as "HH:mm - HH:mm" (you can change to AM/PM if you want)
            displayLabel = startHHmm + " - " + endHHmm;

            // time_value used for sorting; use start as TIME
            timeValue = normalizeHHmmToHHmmss(startHHmm);

            startTime = startHHmm;
            endTime = endHHmm;
        } else {
            // single
            if (singleTimeHHmm == null || singleTimeHHmm.trim().isEmpty()) return false;

            singleTimeHHmm = singleTimeHHmm.trim();
            displayLabel = singleTimeHHmm;
            timeValue = normalizeHHmmToHHmmss(singleTimeHHmm);
        }

        String sql =
            "INSERT INTO service_time_slot (service_id, display_label, time_value, start_time, end_time) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setString(2, displayLabel);
            ps.setTime(3, Time.valueOf(timeValue));
            ps.setString(4, startTime);
            ps.setString(5, endTime);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteTimeSlot(int slotId, int serviceId) throws SQLException {
        String sql = "DELETE FROM service_time_slot WHERE slot_id=? AND service_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slotId);
            ps.setInt(2, serviceId);

            return ps.executeUpdate() > 0;
        }
    }

    private String normalizeHHmmToHHmmss(String hhmm) {
        String t = hhmm;
        if (t == null) return "00:00:00";
        t = t.trim();
        if (t.length() == 5) return t + ":00";
        if (t.length() == 8) return t;
        // fallback
        return "00:00:00";
    }
    public List<Service> getUnassignedServices() throws SQLException {
        String sql =
            "SELECT s.service_id, s.category_id, s.service_name, s.service_description, " +
            "       s.price, s.duration, s.service_image, s.caregiver_name " +
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
                s.setCaregiverName(rs.getString("caregiver_name"));
                list.add(s);
            }
        }
        return list;
    }
    public void insertTimeSlotsForService(int serviceId, String[] labels, String[] times) throws SQLException {
        if (labels == null || times == null) return;

        String sql =
            "INSERT INTO service_time_slot (service_id, display_label, time_value, start_time, end_time) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int n = Math.min(labels.length, times.length);

            for (int i = 0; i < n; i++) {
                String lbl = labels[i] == null ? "" : labels[i].trim();
                String t   = times[i] == null ? "" : times[i].trim();

                // skip empty row
                if (lbl.isEmpty() && t.isEmpty()) continue;

                // must have time to insert
                if (t.isEmpty()) continue;

                // if label empty, use time as label
                if (lbl.isEmpty()) lbl = t;

                // convert HH:mm -> HH:mm:ss for Time.valueOf
                String hhmmss = normalizeHHmmToHHmmss(t);

                ps.setInt(1, serviceId);
                ps.setString(2, lbl);
                ps.setTime(3, Time.valueOf(hhmmss));
                ps.setString(4, null); // start_time
                ps.setString(5, null); // end_time
                ps.addBatch();
            }

            ps.executeBatch();
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
    public java.util.Map<Integer, Integer> getBookedCountBySlot(int serviceId, String bookingDate) throws SQLException {

        // counts BOTH:
        // 1) existing cart holds (block at add-to-cart)
        // 2) paid/created orders already saved in order_items

        String sql =
            "SELECT x.slot_id, SUM(x.cnt) AS booked " +
            "FROM ( " +
            "   SELECT ci.slot_id, COUNT(*) AS cnt " +
            "   FROM client_cart_items ci " +
            "   WHERE ci.service_id=? AND ci.booking_date=? AND ci.slot_id IS NOT NULL " +
            "   GROUP BY ci.slot_id " +
            "   UNION ALL " +
            "   SELECT oi.slot_id, COUNT(*) AS cnt " +
            "   FROM order_items oi " +
            "   JOIN orders o ON o.order_id = oi.order_id " +
            "   WHERE oi.service_id=? AND oi.booking_date=? AND oi.slot_id IS NOT NULL " +
            "     AND o.status IN ('CREATED','PAID') " +
            "   GROUP BY oi.slot_id " +
            ") x " +
            "GROUP BY x.slot_id";

        java.util.Map<Integer, Integer> map = new java.util.HashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, serviceId);
            ps.setString(2, bookingDate);
            ps.setInt(3, serviceId);
            ps.setString(4, bookingDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("slot_id"), rs.getInt("booked"));
                }
            }
        }
        return map;
    }

}

