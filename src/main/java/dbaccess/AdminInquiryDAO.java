package dbaccess;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.AdminInquiry.BookingRow;
import models.AdminInquiry.ClientBookedServiceRow;
import models.AdminInquiry.ServiceDemandRow;
import models.AdminInquiry.TopClientRow;

public class AdminInquiryDAO {

    /**
     * Demand per service (only PAID orders), includes:
     * - total booked quantity
     * - number of paid orders that include the service
     * - number of configured time slots (service_time_slot count)
     */
    public List<ServiceDemandRow> getServiceDemandSummary() throws SQLException {

        String sql =
            "SELECT " +
            "  s.service_id, s.service_name, " +
            "  COALESCE(d.total_qty, 0) AS total_qty, " +
            "  COALESCE(d.orders_count, 0) AS orders_count, " +
            "  COALESCE(sc.slots_count, 0) AS slots_count " +
            "FROM service s " +
            "LEFT JOIN ( " +
            "  SELECT oi.service_id, " +
            "    COALESCE(SUM(oi.quantity), 0) AS total_qty, " +
            "    COUNT(DISTINCT o.order_id) AS orders_count " +
            "  FROM order_items oi " +
            "  JOIN orders o ON o.order_id = oi.order_id " +
            "  WHERE o.status = 'PAID' " +
            "  GROUP BY oi.service_id " +
            ") d ON d.service_id = s.service_id " +
            "LEFT JOIN ( " +
            "  SELECT service_id, COUNT(*) AS slots_count " +
            "  FROM service_time_slot " +
            "  GROUP BY service_id " +
            ") sc ON sc.service_id = s.service_id " +
            "ORDER BY total_qty DESC, orders_count DESC, s.service_name ASC";

        List<ServiceDemandRow> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ServiceDemandRow(
                    rs.getInt("service_id"),
                    rs.getString("service_name"),
                    rs.getInt("total_qty"),
                    rs.getInt("orders_count"),
                    rs.getInt("slots_count")
                ));
            }
        }

        return list;
    }

    // Top N demanded services
    public List<ServiceDemandRow> getTopDemanded(int limit) throws SQLException {
        List<ServiceDemandRow> all = getServiceDemandSummary();
        return all.subList(0, Math.min(limit, all.size()));
    }

    // Bottom N demanded services (least booked)
    public List<ServiceDemandRow> getLowestDemanded(int limit) throws SQLException {
        List<ServiceDemandRow> all = getServiceDemandSummary();

        all.sort((a, b) -> {
            if (a.totalQty != b.totalQty) return Integer.compare(a.totalQty, b.totalQty);
            if (a.ordersCount != b.ordersCount) return Integer.compare(a.ordersCount, b.ordersCount);
            return a.serviceName.compareToIgnoreCase(b.serviceName);
        });

        return all.subList(0, Math.min(limit, all.size()));
    }

    /**
     * Services with low configured availability (few time slots).
     * Example: slots_count <= threshold.
     */
    public List<ServiceDemandRow> getLowSlotServices(int threshold) throws SQLException {

        String sql =
            "SELECT " +
            "  s.service_id, s.service_name, " +
            "  COALESCE(d.total_qty, 0) AS total_qty, " +
            "  COALESCE(d.orders_count, 0) AS orders_count, " +
            "  COALESCE(sc.slots_count, 0) AS slots_count " +
            "FROM service s " +
            "LEFT JOIN ( " +
            "  SELECT oi.service_id, " +
            "    COALESCE(SUM(oi.quantity), 0) AS total_qty, " +
            "    COUNT(DISTINCT o.order_id) AS orders_count " +
            "  FROM order_items oi " +
            "  JOIN orders o ON o.order_id = oi.order_id " +
            "  WHERE o.status = 'PAID' " +
            "  GROUP BY oi.service_id " +
            ") d ON d.service_id = s.service_id " +
            "LEFT JOIN ( " +
            "  SELECT service_id, COUNT(*) AS slots_count " +
            "  FROM service_time_slot " +
            "  GROUP BY service_id " +
            ") sc ON sc.service_id = s.service_id " +
            "WHERE COALESCE(sc.slots_count, 0) <= ? " +
            "ORDER BY slots_count ASC, total_qty DESC, s.service_name ASC";

        List<ServiceDemandRow> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ServiceDemandRow(
                        rs.getInt("service_id"),
                        rs.getString("service_name"),
                        rs.getInt("total_qty"),
                        rs.getInt("orders_count"),
                        rs.getInt("slots_count")
                    ));
                }
            }
        }

        return list;
    }

	// ===================== 1) BOOKINGS BY DATE RANGE =====================
	public List<BookingRow> getBookingsByDateRange(Date start, Date end) throws SQLException {
	    String sql =
	        "SELECT o.order_id, DATE(o.created_at) AS order_date, " +
	        "       u.user_id, u.name AS user_name, u.email AS user_email, " +
	        "       s.service_name, oi.quantity, oi.unit_price AS unit_price " +
	        "FROM orders o " +
	        "JOIN users u ON u.user_id = o.user_id " +
	        "JOIN order_items oi ON oi.order_id = o.order_id " +
	        "JOIN service s ON s.service_id = oi.service_id " +
	        "WHERE UPPER(o.status) = 'PAID' " +
	        "  AND oi.item_type = 'SERVICE' " +
	        "  AND oi.service_id IS NOT NULL " +
	        "  AND DATE(o.created_at) BETWEEN ? AND ? " +
	        "ORDER BY o.created_at DESC, o.order_id DESC";

	    List<BookingRow> list = new ArrayList<>();

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setDate(1, start);
	        ps.setDate(2, end);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new BookingRow(
	                    rs.getInt("order_id"),
	                    rs.getString("order_date"),
	                    rs.getInt("user_id"),
	                    rs.getString("user_name"),
	                    rs.getString("service_name"),
	                    rs.getInt("quantity"),
	                    rs.getDouble("unit_price")
	                ));
	            }
	        }
	    }
	    return list;
	}

	// ===================== 2) BOOKINGS BY MONTH =====================
	public List<BookingRow> getBookingsByMonth(int year, int month) throws SQLException {
	    String sql =
	        "SELECT o.order_id, DATE(o.created_at) AS order_date, " +
	        "       u.user_id, u.name AS user_name, u.email AS user_email, " +
	        "       s.service_name, oi.quantity, oi.unit_price AS unit_price " +
	        "FROM orders o " +
	        "JOIN users u ON u.user_id = o.user_id " +
	        "JOIN order_items oi ON oi.order_id = o.order_id " +
	        "JOIN service s ON s.service_id = oi.service_id " +
	        "WHERE UPPER(o.status) = 'PAID' " +
	        "  AND oi.item_type = 'SERVICE' " +
	        "  AND oi.service_id IS NOT NULL " +
	        "  AND YEAR(o.created_at) = ? AND MONTH(o.created_at) = ? " +
	        "ORDER BY o.created_at DESC";

	    List<BookingRow> list = new ArrayList<>();

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, year);
	        ps.setInt(2, month);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new BookingRow(
	                    rs.getInt("order_id"),
	                    rs.getString("order_date"),
	                    rs.getInt("user_id"),
	                    rs.getString("user_name"),
	                    rs.getString("service_name"),
	                    rs.getInt("quantity"),
	                    rs.getDouble("unit_price")
	                ));
	            }
	        }
	    }
	    return list;
	}

	// ===================== 3) TOP CLIENTS BY VALUE =====================
	public List<TopClientRow> getTopClientsByValue(int limit) throws SQLException {
	    String sql =
	        "SELECT u.user_id, u.name AS user_name, " +
	        "       COALESCE(SUM(oi.quantity * oi.unit_price), 0) AS total_spent, " +
	        "       COUNT(DISTINCT o.order_id) AS paid_orders " +
	        "FROM orders o " +
	        "JOIN users u ON u.user_id = o.user_id " +
	        "JOIN order_items oi ON oi.order_id = o.order_id " +
	        "WHERE UPPER(o.status) = 'PAID' " +
	        "  AND oi.item_type = 'SERVICE' " +
	        "  AND oi.service_id IS NOT NULL " +
	        "GROUP BY u.user_id, u.name " +
	        "ORDER BY total_spent DESC " +
	        "LIMIT ?";

	    List<TopClientRow> list = new ArrayList<>();

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, limit);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new TopClientRow(
	                    rs.getInt("user_id"),
	                    rs.getString("user_name"),
	                    rs.getDouble("total_spent"),
	                    rs.getInt("paid_orders")
	                ));
	            }
	        }
	    }
	    return list;
	}

	// ===================== 4) CLIENTS WHO BOOKED A SERVICE =====================
	public List<ClientBookedServiceRow> getClientsWhoBookedService(int serviceId) throws SQLException {
	    String sql =
	        "SELECT u.user_id, u.name AS user_name, u.email, " +
	        "       COALESCE(SUM(oi.quantity), 0) AS total_qty " +
	        "FROM orders o " +
	        "JOIN users u ON u.user_id = o.user_id " +
	        "JOIN order_items oi ON oi.order_id = o.order_id " +
	        "WHERE UPPER(o.status) = 'PAID' " +
	        "  AND oi.item_type = 'SERVICE' " +
	        "  AND oi.service_id IS NOT NULL " +
	        "  AND oi.service_id = ? " +
	        "GROUP BY u.user_id, u.name, u.email " +
	        "ORDER BY total_qty DESC, u.name ASC";

	    List<ClientBookedServiceRow> list = new ArrayList<>();

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, serviceId);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(new ClientBookedServiceRow(
	                    rs.getInt("user_id"),
	                    rs.getString("user_name"),
	                    rs.getString("email"),
	                    rs.getInt("total_qty")
	                ));
	            }
	        }
	    }
	    return list;
	}

}
