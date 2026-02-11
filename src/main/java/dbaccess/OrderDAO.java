package dbaccess;

import models.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

public class OrderDAO {

    public int createOrder(int userId, BigDecimal subtotal, BigDecimal gst, BigDecimal total, String currency) throws SQLException {
        String sql = "INSERT INTO orders (user_id, currency, subtotal, gst, total, status) VALUES (?,?,?,?,?, 'CREATED')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, currency);
            ps.setBigDecimal(3, subtotal);
            ps.setBigDecimal(4, gst);
            ps.setBigDecimal(5, total);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Failed to create order");
    }
    public void insertOrderItemsFromCart(int orderId, List<CartItem> items) throws SQLException {
        String sql = """
            INSERT INTO order_items
            (order_id, item_type, product_id, service_id, item_name, unit_price, quantity, 
             booking_date, booking_time, booking_time_display)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (CartItem it : items) {

                String type = (it.getProductId() != null) ? "PRODUCT" : "SERVICE";

                ps.setInt(1, orderId);
                ps.setString(2, type);

                if (it.getProductId() == null) ps.setNull(3, Types.INTEGER);
                else ps.setInt(3, it.getProductId());

                if (it.getServiceId() == null) ps.setNull(4, Types.INTEGER);
                else ps.setInt(4, it.getServiceId());

                ps.setString(5, it.getItemName());
                ps.setBigDecimal(6, it.getUnitPrice());
                ps.setInt(7, it.getQuantity());

                // booking_date
                String bd = it.getBookingDate();
                if (bd == null || bd.equals("-") || bd.isBlank()) {
                    ps.setNull(8, Types.DATE);
                } else {
                    ps.setDate(8, Date.valueOf(bd));
                }

                // booking_time  (THIS IS WHERE THE NEW CODE GOES)
                String bt = it.getBookingTime();
                if (bt == null || bt.equals("-") || bt.isBlank()) {
                    ps.setNull(9, Types.TIME);
                } else {
                    bt = bt.trim();
                    if (bt.length() == 5) bt = bt + ":00"; // HH:mm → HH:mm:ss
                    ps.setTime(9, Time.valueOf(bt));
                }

                ps.setString(10, it.getBookingTimeDisplay());

                ps.addBatch();
            }

            ps.executeBatch();
        }
    }


    public void attachPaypalOrderId(int orderId, String paypalOrderId) throws SQLException {
        String sql = "UPDATE orders SET paypal_order_id=? WHERE order_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paypalOrderId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public int findLocalOrderIdByPaypalOrderId(String paypalOrderId) throws SQLException {
        String sql = "SELECT order_id FROM orders WHERE paypal_order_id=? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paypalOrderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("order_id");
            }
        }
        throw new SQLException("No local order for paypal_order_id=" + paypalOrderId);
    }

    public void markPaid(int orderId) throws SQLException {
        String sql = "UPDATE orders SET status='PAID' WHERE order_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }

    public void markFailed(int orderId) throws SQLException {
        String sql = "UPDATE orders SET status='FAILED' WHERE order_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        }
    }
    public boolean hasAnyOrderItems(int orderId) throws SQLException {
        String sql = "SELECT 1 FROM order_items WHERE order_id=? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

}
