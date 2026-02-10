package dbaccess;

import java.sql.*;

public class BookingDAO {

    public void createBookingsFromOrder(int orderId, int userId) throws SQLException {
        // Insert only SERVICE items that have booking_date/time
        String sql = """
            INSERT INTO service_bookings (user_id, order_id, service_id, booking_date, booking_time, booking_time_display, status)
            SELECT ?, oi.order_id, oi.service_id, oi.booking_date, oi.booking_time, oi.booking_time_display, 'CONFIRMED'
            FROM order_items oi
            WHERE oi.order_id = ?
              AND oi.item_type = 'SERVICE'
              AND oi.service_id IS NOT NULL
              AND oi.booking_date IS NOT NULL
              AND oi.booking_time IS NOT NULL
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }
}
