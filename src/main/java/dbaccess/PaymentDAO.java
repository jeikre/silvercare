package dbaccess;

import java.math.BigDecimal;
import java.sql.*;

public class PaymentDAO {

    public void recordPaypalPayment(int orderId, String paypalOrderId, String rawJson) throws SQLException {
        // amount is stored in orders.total
        BigDecimal amount = getOrderTotal(orderId);

        String sql = """
            INSERT INTO payments (order_id, provider, paypal_order_id, status, amount, currency, raw_response)
            VALUES (?, 'PAYPAL', ?, 'COMPLETED', ?, 'SGD', CAST(? AS JSON))
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, paypalOrderId);
            ps.setBigDecimal(3, amount);
            ps.setString(4, rawJson);
            ps.executeUpdate();
        }
    }

    private BigDecimal getOrderTotal(int orderId) throws SQLException {
        String sql = "SELECT total FROM orders WHERE order_id=? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal("total");
            }
        }
        return BigDecimal.ZERO;
    }
}
